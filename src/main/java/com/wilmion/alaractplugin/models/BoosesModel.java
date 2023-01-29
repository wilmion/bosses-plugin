package com.wilmion.alaractplugin.models;

import com.wilmion.alaractplugin.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class BoosesModel {

    protected Plugin plugin;
    protected World world;
    protected Server server;

    protected Entity entity;
    public BoosesModel(Player player, Location location, Plugin plugin, Double initialHealth, String type, String id) {
        this.plugin = plugin;
        this.world = player.getWorld();
        this.server = plugin.getServer();

        world.spawn(location, LightningStrike.class);
        this.entity = world.spawnEntity(location, EntityType.valueOf(type));

        LivingEntity living = (LivingEntity) entity;

        AttributeInstance healthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(initialHealth);

        living.setHealth(initialHealth);
        this.entity.setCustomName("ANN LA MAESTRA");
        this.entity.setCustomNameVisible(true);
        this.entity.setMetadata(id, new FixedMetadataValue(this.plugin, "true"));
        this.equipBoss();
    }

    protected void equipBoss() {

    }

    private static void upsertHealthBar(Entity zombie, Player player, double health, BarColor color, double maxHealth, String idMetadata) {
        String name = zombie.getCustomName();

        ProgressBar progressBar = new ProgressBar(idMetadata);
        progressBar.setTitle(name);
        progressBar.setColor(color);
        progressBar.setProgress(health / maxHealth);

        if(health > 0.0) progressBar.enableBar();

        if(player != null) progressBar.addPlayer(player);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event, BarColor color, double maxHealth, String idMetadata, String key) {
        Entity entity = event.getEntity();
        LivingEntity living = (LivingEntity) entity;

        boolean isZombie = entity.getType() == EntityType.valueOf(key);
        boolean isSupportedZombie = entity.hasMetadata(idMetadata);
        Player playerDamager = Utils.playerDamager(event.getDamager());

        double health = Utils.getHealthByDamage(event.getFinalDamage(), living.getHealth());

        if (!isZombie || !isSupportedZombie) return true;

        upsertHealthBar(entity, playerDamager, health, color, maxHealth, idMetadata);

        boolean continueAlth = health <= 0.0 &&  isSupportedZombie;

        return !continueAlth;
    }


    public static void handleDamage(EntityDamageEvent event, String key, BarColor color, double maxHealth, String idMetadata) {
        Entity entity = event.getEntity();

        boolean isTypeEntity = entity.getType() == EntityType.valueOf(key);
        boolean isSupportedEntity = entity.hasMetadata(idMetadata);

        if(!isTypeEntity || !isSupportedEntity) return;
        LivingEntity bossEntity = (LivingEntity) entity;

        double health = Utils.getHealthByDamage(event.getFinalDamage(), bossEntity.getHealth());

        upsertHealthBar(entity, null, health, color, maxHealth, idMetadata);
    }
}
