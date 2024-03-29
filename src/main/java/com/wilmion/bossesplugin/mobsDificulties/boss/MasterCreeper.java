package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.*;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.models.metadata.EntityScoreboard;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.objects.metadata.MetadataModel;
import com.wilmion.bossesplugin.utils.*;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class MasterCreeper extends BoosesModel {
    static final String idMinionMetadata = "CREEPER-MINION-MASTER-BOSS";
    private int minions = 0;
    private boolean usedUltimate1 = false;

    public MasterCreeper(Location location) {
        super(location, 5);
    }

    private Creeper getBoss() {
        return (Creeper) this.entity;
    }

    @Override
    public void useSchedulerEvents() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 100, 100);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useUtimate1, 50, 50);
    }

    @Override
    protected void equipBoss() {
        Creeper boss = getBoss();
        AttributeInstance followRange = boss.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

        followRange.setBaseValue(60);
    }

    @Override
    public void deadFunctionality() {
        super.deadFunctionality();
        world.createExplosion(entity.getLocation(), 8f, false);

        if(RandomUtils.getRandomInPercentage() > 50) return;

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> Perk.generatePerk(5, entity.getLocation()), 40);
    }

    private void usePassive() {
        if(!isAlive() || minions >= 5 || getBoss().getTarget() == null) return;

        setTemporalInvunerability();

        Creeper boss = getBoss();
        BlockFace face = boss.getFacing();
        PotionEffect fire = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 1);

        int probabilityToCharge = RandomUtils.getRandomInPercentage();

        int x = 2 * face.getModX();
        int z = 2 * face.getModZ();

        if(face.getModX() != 0) z *= RandomUtils.getRandomNumberForSpace();
        else x = RandomUtils.getRandomNumberForSpace();

        Location location = boss.getLocation().clone();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);
        location = WorldUtils.getLocationYInNearAir(location, 10);

        if(location == null) return;

        Creeper minion = world.spawn(location, Creeper.class);

        minion.setTarget(boss.getTarget());
        minion.addPotionEffect(fire);
        minion.setRemoveWhenFarAway(false);

        EntityScoreboard.upsertScoreboard(minion, idMinionMetadata, String.valueOf(entity.getUniqueId()));

        if(probabilityToCharge > 30) world.spawnEntity(location, EntityType.LIGHTNING);
        minions++;
    };

    private void useUtimate1() {
        Creeper boss = getBoss();
        Entity target = boss.getTarget();
        Boolean isLivingEntity = target instanceof LivingEntity;

        if(!isAlive() || boss.getHealth() >= maxHealth * 0.5 || !isLivingEntity) return;

        LivingEntity living = (LivingEntity) target;
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW , 100, 3);

        living.addPotionEffect(slow);
        living.damage(2);
    }

    public void useUtimate2() {
        Creeper boss = getBoss();
        Entity target = boss.getTarget();
        Boolean isLivingEntity = target instanceof LivingEntity;

        if(usedUltimate1 || !isLivingEntity) return;

        usedUltimate1 = true;

        Location location = target.getLocation().clone();

        ActionRangeBlocks actionRangeBlocks = (locationSpawn) -> {
            world.spawn(locationSpawn, TNTPrimed.class);
            return true;
        };

        AreaUtils.executeActionInARangeOfBlock(3,  10, location, actionRangeBlocks);
    }

    public void lessMinion() {
        this.minions--;
    }

    public void generateDeathExplosion() {
        world.createExplosion(entity.getLocation(), 8f , false);
    }

    public static void handleEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        BossDataModel bossData = getMetadata(5);
        boolean isBoss = event.getEntity().hasMetadata(bossData.getMetadata());

        if(isBoss) event.setCancelled(true);
    }

    public static void handleExplode(EntityExplodeEvent event) {
        detectDeathOfMinion(event.getEntity());
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda<MasterCreeper> useUltimates = (health, boss) -> {
            if(health <= boss.maxHealth * 0.3) boss.useUtimate2();
        };

        boolean isMinion = EntityScoreboard.getScoreboard(event.getEntity(), idMinionMetadata).isPresent();
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, 5, useUltimates);

        return isMinion? false : continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, 5, () -> detectInvunerabilityToExplosions(event));
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, 5);
        detectDeathOfMinion(event.getEntity());
    }

    private static void detectInvunerabilityToExplosions(EntityDamageEvent event) {
        boolean isExplosion = EventUtils.isDamageType(event.getCause().name(), "ENTITY_EXPLOSION");

        if(!isExplosion) return;

        event.setDamage(0.0);
        event.setCancelled(true);
    }

    private static void detectDeathOfMinion(Entity entity) {
        BossDataModel bossData = getMetadata(5);
        Optional<MetadataModel> idParentOnMinion = EntityScoreboard.getScoreboard(entity, idMinionMetadata);
        boolean isBoss = EntityScoreboard.getScoreboard(entity, bossData.getMetadata()).isPresent();

        if(isBoss) {
            String idParent = String.valueOf(entity.getUniqueId());
            Optional<MasterCreeper> boss = BossesMetadata.getBoss(idParent);

            if(boss.isPresent()) boss.get().generateDeathExplosion();

            BossesMetadata.deleteBoss(idParent);

            ProgressBar progressBar = new ProgressBar(bossData.getMetadata());
            progressBar.disabledBar();
            progressBar.removeAllUsers();
        }

        if(idParentOnMinion.isPresent()) {
            Optional<MasterCreeper> boss = BossesMetadata.getBoss(idParentOnMinion.get().getValue());
            if(boss.isPresent()) boss.get().lessMinion();
        }
    }

}
