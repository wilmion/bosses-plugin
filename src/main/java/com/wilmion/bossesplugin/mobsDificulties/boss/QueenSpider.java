package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.models.metadata.EntityScoreboard;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.objects.metadata.MetadataModel;
import com.wilmion.bossesplugin.utils.AreaUtils;
import com.wilmion.bossesplugin.utils.RandomUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class QueenSpider extends BoosesModel {
    private static String idMetadataMinion = "QUEEN_SPIDER_MINION_BOSS";
    private int minions = 0;
    private boolean ultimate2Used = false;

    public QueenSpider(Location location) {
        super(location, 3);
    }

    private Spider getBoss() {
        return (Spider) this.entity;
    }

    @Override
    public void useSchedulerEvents() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 50, 50);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useUltimate1, 20, 20);
    }

    @Override
    protected void equipBoss() {
        Spider boss = getBoss();

        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 2);

        boss.addPotionEffect(damageResistance);
    }

    @Override
    public void deadFunctionality() {
        Location location = getBoss().getLocation();

        super.deadFunctionality();

        ActionRangeBlocks actionRange = (locationParam) -> {
            locationParam.getBlock().setType(Material.COBWEB);
            return true;
        };

        AreaUtils.executeActionInARangeOfBlock(4, 0, location, actionRange);
        AreaUtils.executeActionInARangeOfBlock(4, 1, location, actionRange);

        Perk.generatePerk(3, location);
    }

    public void lessMinions(int n) {
        this.minions -= n;
    }

    private Spider generateMinion(Location location) {
        String entityID = String.valueOf(entity.getUniqueId());

        location.getBlock().setType(Material.COBWEB);

        Spider spider = world.spawn(location, Spider.class);
        spider.setTarget(getBoss().getTarget());

        EntityScoreboard.upsertScoreboard(spider, idMetadataMinion, entityID);

        return spider;
    }

    private void usePassive() {
        Spider boss = getBoss();
        Entity target = boss.getTarget();
        BlockFace face = boss.getFacing();

        if(!isAlive() || minions >= 8 || target == null) return;

        int x = 2 * face.getModX();
        int z = 2 * face.getModZ();

        if(face.getModX() != 0) z *= RandomUtils.getRandomNumberForSpace();
        else x = RandomUtils.getRandomNumberForSpace();

        Location location = boss.getLocation().clone();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        generateMinion(location);
        minions++;
    }

    private void useUltimate1() {
        Spider boss = getBoss();
        Double health = boss.getHealth();

        if(!isAlive() || health > maxHealth * 0.7) return;

        Location location = boss.getLocation().clone();
        BlockFace face = boss.getFacing();

        location.setX(location.getX() + face.getModX());
        location.setZ(location.getZ() + face.getModZ());

        location.getBlock().setType(Material.COBWEB);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> location.getBlock().setType(Material.AIR), 600);
    }

    public void useUltimate2() {
        if(ultimate2Used) return;

        ultimate2Used = true;

        ActionRangeBlocks actionRange = (location) -> {
            if(RandomUtils.getRandomInPercentage() <= 80) location.getBlock().setType(Material.COBWEB);
            return true;
        };

        Spider boss = getBoss();
        BlockFace face = boss.getFacing();

        AreaUtils.executeActionInARangeOfBlock(3, 0, boss.getLocation(), actionRange);

        for(int i = -1; i <= 1; i++) {
            int x = 0;
            int z = 0;

            if(face.getModX() != 0) z =  2 * i;
            else x = 2 * i;

            Location location = boss.getLocation().clone();

            location.setX(location.getX() + x);
            location.setZ(location.getZ() + z);

            EntityType entityType = i != 0 ? EntityType.SKELETON : EntityType.WITHER_SKELETON;

            Spider minionSpider = generateMinion(location);
            Monster entity = (Monster) world.spawnEntity(location, entityType);

            entity.setTarget(boss.getTarget());
            minionSpider.setTarget(boss.getTarget());
            minionSpider.addPassenger(entity);
            boss.addPassenger(world.spawn(boss.getLocation(), WitherSkeleton.class));
        }
    }

    public static void handleEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        BossDataModel bossData = getMetadata(3);
        boolean isBoss = event.getEntity().hasMetadata(bossData.getMetadata());
        if(isBoss) event.setCancelled(true);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda<QueenSpider> lambda = (health, boss) -> {
            if(health <= boss.maxHealth * 0.4)  boss.useUltimate2();
        };

        boolean isMinion = EntityScoreboard.getScoreboard(event.getEntity(), idMetadataMinion).isPresent();
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, 3, lambda);

        return isMinion? false : continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, 3, () -> {});
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, 3);

        Entity entity = event.getEntity();

        Optional<MetadataModel> idParent = EntityScoreboard.getScoreboard(entity, idMetadataMinion);

        if(idParent.isEmpty()) return;

        Optional<QueenSpider> boss = BossesMetadata.getBoss(idParent.get().getValue());
        if(boss.isPresent()) boss.get().lessMinions(1);
    }

}
