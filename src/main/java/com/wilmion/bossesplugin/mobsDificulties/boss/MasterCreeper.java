package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.models.ProgressBar;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.TreeMap;

public class MasterCreeper extends BoosesModel {
    private static Map<String, MasterCreeper> bosses = new TreeMap();

    static final String idMetadata = "CREPPER-MASTER-BOSS";
    static final String idMinionMetadata = "CREEPER-MINION-MASTER-BOSS";
    static final double maxHealth = 150.0;

    private int minions = 0;
    private boolean usedUltimate1 = false;

    public MasterCreeper(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth, "CREEPER", idMetadata, "JACK EL PIROMANO");

        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 100, 100);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useUtimate1, 50, 50);
    }
    private Creeper getBoss() {
        return (Creeper) this.entity;
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
        world.createExplosion(entity.getLocation(), 3f, false);

        int probability = Utils.getRandomInPercentage();

        if(probability > 50) return;

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> Perk.generatePerk(5, entity.getLocation(), plugin), 40);
    }

    private void usePassive() {
        if(!this.isAlive() || minions >= 5) return;
        this.setTemporalInvunerability();

        Creeper boss = getBoss();
        BlockFace face = boss.getFacing();
        PotionEffect fire = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 1);

        int probabilityToCharge = Utils.getRandomInPercentage();

        int x = 2 * face.getModX();
        int z = 2 * face.getModZ();

        if(face.getModX() != 0) z *= Utils.getRandomNumberForSpace();
        else x = Utils.getRandomNumberForSpace();

        Location location = boss.getLocation().clone();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        Creeper minion = world.spawn(location, Creeper.class);

        minion.setMetadata(idMinionMetadata, new FixedMetadataValue(plugin, String.valueOf(entity.getEntityId())));
        minion.addPotionEffect(fire);

        if(probabilityToCharge > 30) world.spawnEntity(location, EntityType.LIGHTNING);
        minions++;
    };

    private void useUtimate1() {
        Creeper boss = getBoss();

        if(!this.isAlive() || boss.getHealth() >= maxHealth * 0.5) return;

        Entity target = boss.getTarget();

        boolean isLivingEntity = target instanceof LivingEntity;

        if(!isLivingEntity) return;

        LivingEntity living = (LivingEntity) target;

        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW , 100, 3);

        living.addPotionEffect(slow);
        living.damage(2);
    }

    public void useUtimate2() {
        if(usedUltimate1) return;

        Creeper boss = getBoss();

        Entity target = boss.getTarget();

        boolean isLivingEntity = target instanceof LivingEntity;

        if(!isLivingEntity) return;

        usedUltimate1 = true;

        Location location = target.getLocation().clone();

        ActionRangeBlocks actionRangeBlocks = (locationSpawn) -> {
            world.spawn(locationSpawn, TNTPrimed.class);
        };

        Utils.executeActionInARangeOfBlock(3,  10, location, actionRangeBlocks);
    }

    public void lessMinion() {
        this.minions--;
    }

    public void generateDeathExplosion() {
        world.createExplosion(entity.getLocation(), 8f , false);
    }

    public static void handleEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        boolean isBoss = event.getEntity().hasMetadata(idMetadata);

        if(isBoss) event.setCancelled(true);
    }
    public static void handleExplode(EntityExplodeEvent event) {
        detectDeathOfMinion(event.getEntity());
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda useUltimates = (health, entityID) -> {
            MasterCreeper boss = bosses.get(entityID);

            if(health <= maxHealth * 0.3) boss.useUtimate2();
        };

        boolean isMinion = event.getEntity().hasMetadata(idMinionMetadata);
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, BarColor.BLUE, maxHealth, idMetadata, "CREEPER",useUltimates);

        return isMinion? false : continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "CREEPER", BarColor.BLUE, maxHealth, idMetadata, () -> detectInvunerabilityToExplosions(event));
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, idMetadata, bosses);
        detectDeathOfMinion(event.getEntity());
    }

    private static void detectInvunerabilityToExplosions(EntityDamageEvent event) {
        boolean isExplosion = Utils.isDamageType(event.getCause().name(), "ENTITY_EXPLOSION");

        if(!isExplosion) return;

        event.setDamage(0.0);
        event.setCancelled(true);
    }

    private static void detectDeathOfMinion(Entity entity) {
        boolean isMinion = entity.hasMetadata(idMinionMetadata);
        boolean isBoss = entity.hasMetadata(idMetadata);

        if(isBoss) {
            String idParent = String.valueOf(entity.getEntityId());

            MasterCreeper boss = bosses.get(idParent);

            boss.generateDeathExplosion();

            ProgressBar progressBar = new ProgressBar(idMetadata);
            progressBar.disabledBar();
            progressBar.removeAllUsers();
        }

        if(isMinion) {
            String idParent = (String) entity.getMetadata(idMinionMetadata).get(0).value();

            MasterCreeper boss = bosses.get(idParent);
            boss.lessMinion();
        }
    }

}
