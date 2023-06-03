package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.utils.RandomUtils;

import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SoldierSpider extends BoosesModel {
    public SoldierSpider(Location location) {
        super(location, 4);
    }

    @Override
    public void useSchedulerEvents() {
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

        super.deadFunctionality();

        world.createExplosion(location, 5f , false);

        int probability = RandomUtils.getRandomInPercentage();

        if(probability >= 50.0) server.getScheduler().scheduleSyncDelayedTask(plugin, () -> Perk.generatePerk(4, location), 20);
    }

    private CaveSpider getBoss() {
        return (CaveSpider) this.entity;
    }

    private void usePassive() {
        if(!isAlive()) return;

        equipBoss();

        CaveSpider boss = getBoss();
        PotionEffect invisible = new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1);

        if(RandomUtils.getRandomInPercentage() <= 50.0) boss.addPotionEffect(invisible);
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

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        handleAttack(event);

        boolean continueAlth = BoosesModel.handleDamageByEntity(event, 4, (a, b) -> {});

        return continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, 4, () -> {});
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, 4);
    }

    public static void handleAttack(EntityDamageByEntityEvent event) {
        BossDataModel bossData = BoosesModel.getMetadata(4);
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        boolean isBoss = damager.hasMetadata(bossData.getMetadata());

        if(!isBoss) return;

        LivingEntity living = (LivingEntity) entity;

        int probability = RandomUtils.getRandomInPercentage();

        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 200, 2);
        PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 30, 1);
        PotionEffect potion = new PotionEffect(PotionEffectType.POISON, 100, 5);

        if(probability <= 20.0) living.addPotionEffect(slow);
        if(probability > 20.0 && probability <= 40.0) living.addPotionEffect(weakness);
        if(probability > 40.0 && probability <= 60.0) living.addPotionEffect(potion);
    }
}
