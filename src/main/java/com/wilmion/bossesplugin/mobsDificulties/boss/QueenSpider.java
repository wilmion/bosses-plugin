package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.TreeMap;

public class QueenSpider extends BoosesModel {
    private static Map<String, QueenSpider> bosses = new TreeMap<>();
    private static String idMetadata = "QUEEN_SPIDER_BOSS";
    private static String idMetadataMinion = "QUEEN_SPIDER_MINION_BOSS";
    private static double maxHealth = 310.0;

    private int minions = 0;
    private boolean ultimate2Used = false;
    public QueenSpider(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth, "SPIDER", idMetadata, "AURORA LA REINA");

        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);

        //Attacks
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 50, 50);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useUltimate1, 20, 20);
    }

    private Spider getBoss() {
        return (Spider) this.entity;
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
        };

        Utils.executeActionInARangeOfBlock(4, 0, location, actionRange);
        Utils.executeActionInARangeOfBlock(4, 1, location, actionRange);

        Perk.generatePerk("Pila de la fertilidad", Material.BLACK_DYE, entity.getLocation(), ChatColor.BLACK, world, plugin);
    }

    public void lessMinions(int n) {
        this.minions -= n;
    }

    private Spider generateMinion(Location location) {
        String entityID = String.valueOf(entity.getEntityId());

        location.getBlock().setType(Material.COBWEB);

        Spider spider = world.spawn(location, Spider.class);
        spider.setMetadata(idMetadataMinion, new FixedMetadataValue(plugin, entityID));

        return spider;
    }

    private void usePassive() {
        if(!this.isAlive() || minions >= 8) return;

        Spider boss = getBoss();
        BlockFace face = boss.getFacing();

        int x = 2 * face.getModX();
        int z = 2 * face.getModZ();

        if(face.getModX() != 0) z *= Utils.getRandomNumberForSpace();
        else x = Utils.getRandomNumberForSpace();

        Location location = boss.getLocation().clone();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        this.generateMinion(location);
        minions++;
    }

    private void useUltimate1() {
        Spider boss = getBoss();

        double health = boss.getHealth();

        if(!this.isAlive() || health > maxHealth * 0.7) return;

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
            int probability = Utils.getRandomInPercentage();

            if(probability <= 80) location.getBlock().setType(Material.COBWEB);
        };

        Spider boss = getBoss();

        BlockFace face = boss.getFacing();

        Utils.executeActionInARangeOfBlock(3, 0, boss.getLocation(), actionRange);

        for(int i = -1; i <= 1; i++) {
            int x = 0;
            int z = 0;

            if(face.getModX() != 0) z =  2 * i;
            else x = 2 * i;

            Location location = boss.getLocation().clone();

            location.setX(location.getX() + x);
            location.setZ(location.getZ() + z);

            EntityType entityType = i != 0 ? EntityType.SKELETON : EntityType.WITHER_SKELETON;

            Spider minionSpider = this.generateMinion(location);
            Entity entity = world.spawnEntity(location, entityType);

            minionSpider.addPassenger(entity);
            boss.addPassenger(world.spawn(boss.getLocation(), WitherSkeleton.class));
        }
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda lambda = (health, entityID) -> {
            QueenSpider boss = bosses.get(entityID);

            Utils.removeKnockback(event);

            if(health <= maxHealth * 0.4)  boss.useUltimate2();
        };

        boolean isMinion = event.getEntity().hasMetadata(idMetadataMinion);
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, BarColor.PURPLE, maxHealth, idMetadata, "SPIDER", lambda);

        return isMinion? false : continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "SPIDER", BarColor.PURPLE, maxHealth, idMetadata, null);
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, idMetadata, bosses);

        Entity entity = event.getEntity();

        boolean isMinion = entity.hasMetadata(idMetadataMinion);

        if(!isMinion) return;

        String idParent = (String) entity.getMetadata(idMetadataMinion).get(0).value();

        QueenSpider boss = bosses.get(idParent);
        boss.lessMinions(1);
    }

}
