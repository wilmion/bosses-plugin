package com.wilmion.bossesplugin.models;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.objects.boss.BossModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class BoosesModel {
    public static Map<String, BoosesModel> bosses = new TreeMap<>();

    protected transient Plugin plugin;
    protected transient World world;
    protected transient Server server;
    protected transient LivingEntity entity;
    protected Double maxHealth;
    protected String idMetadata;

    public BoosesModel(Location location, Plugin plugin, Integer id) {
        BossDataModel bossData = getMetadata(id);

        this.maxHealth = bossData.getHealth();
        this.idMetadata = bossData.getMetadata();
        this.plugin = plugin;
        this.world = location.getWorld();
        this.server = plugin.getServer();
        this.entity = (LivingEntity) world.spawnEntity(location, EntityType.valueOf(bossData.getType()));

        setTemporalInvunerability();

        world.spawn(location, LightningStrike.class);

        AttributeInstance healthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(maxHealth);

        entity.setHealth(maxHealth);
        entity.setRemoveWhenFarAway(false);
        entity.setCustomName(bossData.getName());
        entity.setCustomNameVisible(true);
        entity.setMetadata(idMetadata, new FixedMetadataValue(this.plugin, "true"));

        bosses.put(String.valueOf(entity.getUniqueId()), this);

        this.useSchedulerEvents();
        this.setMetadata();
        this.equipBoss();
    }

    public void useSchedulerEvents() {}

    protected void setMetadata() {
        entity.setMetadata(idMetadata, new FixedMetadataValue(this.plugin, "true"));
    }

    protected boolean isAlive() {
        return entity.isValid() && !entity.isDead();
    }

    protected void equipBoss() {}

    protected void setTemporalInvunerability() {
        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 10);
        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30,12);

        entity.addPotionEffect(fireResistance);
        entity.addPotionEffect(damageResistance);
    }

    public void deadFunctionality() {
        String entityID = String.valueOf(entity.getUniqueId());

        bosses.remove(entityID);
        world.playSound(this.entity.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 0);
    }

    private static void upsertHealthBar(String name, LivingEntity shooter, double health, BarColor color, double maxHealth, String idMetadata) {
        ProgressBar progressBar = new ProgressBar(idMetadata);
        progressBar.setTitle(name);
        progressBar.setColor(color);
        progressBar.setProgress(health / maxHealth);

        if(health > 0.0) progressBar.enableBar();

        if(shooter != null && shooter instanceof Player) progressBar.addPlayer((Player) shooter);
    }

    public static BossDataModel getMetadata(Integer id) {
        BossModel file = Resources.getJsonByData("boss.json", BossModel.class);
        List<BossDataModel> data = file.getBosses();

        return data.stream().filter(d -> d.getId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public static<T> boolean handleDamageByEntity(EntityDamageByEntityEvent event, Integer id, IUltimateLambda<T> lambda) {
        BossDataModel bossData = getMetadata(id);
        BarColor color = BarColor.valueOf(bossData.getBarColor());
        Entity entity = event.getEntity();
        LivingEntity living = (LivingEntity) entity;
        String entityID = String.valueOf(entity.getUniqueId());

        boolean isZombie = entity.getType() == EntityType.valueOf(bossData.getType());
        boolean isSupported = entity.hasMetadata(bossData.getMetadata());
        LivingEntity shooter = Utils.livingDamager(event.getDamager());

        double health = Utils.getHealthByDamage(event.getFinalDamage(), living.getHealth());

        if (!isZombie || !isSupported) return true;

        upsertHealthBar(bossData.getName(), shooter, health, color, bossData.getHealth(), bossData.getMetadata());

        lambda.ultimates(health, (T) bosses.get(entityID));

        return false;
    }

    public static void handleDamage(EntityDamageEvent event, Integer id, Runnable action) {
        BossDataModel bossData = getMetadata(id);
        Entity entity = event.getEntity();
        BarColor color = BarColor.valueOf(bossData.getBarColor());

        boolean isTypeEntity = entity.getType() == EntityType.valueOf(bossData.getType());
        boolean isSupportedEntity = entity.hasMetadata(bossData.getMetadata());

        if(!isTypeEntity || !isSupportedEntity) return;

        LivingEntity bossEntity = (LivingEntity) entity;

        action.run();

        double health = Utils.getHealthByDamage(event.getFinalDamage(), bossEntity.getHealth());

        upsertHealthBar(bossData.getName(), null, health, color, bossData.getHealth(), bossData.getMetadata());
    }

    public static void handleDead(EntityDeathEvent event, Integer id) {
        BossDataModel bossData = getMetadata(id);
        Entity entity = event.getEntity();
        String entityID = String.valueOf(entity.getUniqueId());

        boolean isSupported = entity.hasMetadata(bossData.getMetadata());

        if(!isSupported) return;

        ProgressBar progressBar = new ProgressBar(bossData.getMetadata());
        progressBar.disabledBar();
        progressBar.removeAllUsers();

        BoosesModel boss = BoosesModel.bosses.get(entityID);

        boss.deadFunctionality();
    }
}
