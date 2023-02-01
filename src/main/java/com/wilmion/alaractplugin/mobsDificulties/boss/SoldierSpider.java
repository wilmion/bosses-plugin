package com.wilmion.alaractplugin.mobsDificulties.boss;

import com.wilmion.alaractplugin.models.BoosesModel;
import com.wilmion.alaractplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.TreeMap;

public class SoldierSpider extends BoosesModel {
    static Map<String, SoldierSpider> bosses = new TreeMap<>();
    static double maxHealth = 70.0;
    static String idMetadata = "SOLDIER_SPIDER_BOSS";
    public SoldierSpider(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth,"CAVE_SPIDER",  idMetadata, "Escencia de los primordiales", "MIQUEL EL SOLDADO");

        this.colorTextPerk = ChatColor.DARK_GREEN;
        this.materialPerk = Material.GREEN_DYE;

        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 200, 200);
    }

    @Override
    protected void equipBoss() {
        CaveSpider boss = getBoss();

        PotionEffect velocity = new PotionEffect(PotionEffectType.SPEED, 200, 5);
        PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 500, 3);

        boss.addPotionEffect(velocity);
        boss.addPotionEffect(strength);

        if(boss.getHealth() <= maxHealth * 0.5) this.useUltimate1();
        if(boss.getHealth() <= maxHealth * 0.3) this.useUltimate2();
    }

    @Override
    public void deadFunctionality() {
        Location location = this.entity.getLocation();
        world.playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 0);
        world.createExplosion(location, 5f , false);

        int probability = Utils.getRandomInPercentage();

        if(probability >= 50.0) server.getScheduler().scheduleSyncDelayedTask(plugin, super::deadFunctionality, 20);
    }

    private void usePassive() {
        if(!this.isAlive()) return;

        CaveSpider boss = getBoss();
        this.equipBoss();

        int probability = Utils.getRandomInPercentage();

        PotionEffect invisible = new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1);

        if(probability <= 50.0) boss.addPotionEffect(invisible);
    }

    public void useUltimate1() {
        CaveSpider boss = getBoss();

        PotionEffect velocity = new PotionEffect(PotionEffectType.SPEED, 200, 8);

        boss.addPotionEffect(velocity);
    }

    public void useUltimate2() {
        CaveSpider boss = getBoss();

        PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 500, 7);

        boss.addPotionEffect(strength);
    }
    private CaveSpider getBoss() {
        return (CaveSpider) this.entity;
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        handleAttack(event);

        boolean continueAlth = BoosesModel.handleDamageByEntity(event, BarColor.GREEN, maxHealth, idMetadata, "CAVE_SPIDER", null);

        return continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "CAVE_SPIDER", BarColor.GREEN, maxHealth, idMetadata, null);
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, idMetadata, bosses);
    }

    public static void handleAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        boolean isBoss = damager.hasMetadata(idMetadata);

        if(!isBoss) return;

        LivingEntity living = (LivingEntity) entity;

        int probability = Utils.getRandomInPercentage();

        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 200, 2);
        PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 30, 1);
        PotionEffect potion = new PotionEffect(PotionEffectType.POISON, 100, 5);

        if(probability <= 20.0) living.addPotionEffect(slow);
        if(probability > 20.0 && probability <= 40.0) living.addPotionEffect(weakness);
        if(probability > 40.0 && probability <= 60.0) living.addPotionEffect(potion);
    }
}
