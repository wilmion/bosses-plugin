package com.wilmion.alaractplugin.models;

import com.wilmion.alaractplugin.interfaces.IUltimateLambda;
import com.wilmion.alaractplugin.utils.Utils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;


public class BoosesModel {
    protected Plugin plugin;
    protected World world;
    protected Server server;

    protected Entity entity;
    protected String perkName;
    protected ChatColor colorTextPerk;
    protected Material materialPerk;
    public BoosesModel(Player player, Location location, Plugin plugin, Double initialHealth, String type, String id, String perkName, String nameEntity) {
        this.plugin = plugin;
        this.world = player.getWorld();
        this.server = plugin.getServer();
        this.perkName = perkName;

        world.spawn(location, LightningStrike.class);
        this.entity = world.spawnEntity(location, EntityType.valueOf(type));
        this.setTemporalInvunerability();

        LivingEntity living = (LivingEntity) entity;

        AttributeInstance healthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(initialHealth);

        living.setHealth(initialHealth);
        this.entity.setCustomName(nameEntity);
        this.entity.setCustomNameVisible(true);
        this.entity.setMetadata(id, new FixedMetadataValue(this.plugin, "true"));
        this.equipBoss();
    }

    protected boolean isAlive() {
        boolean valid = this.entity.isValid();
        boolean alive = !this.entity.isDead();

        return alive && valid;
    }

    protected Player getTarget() {
        Monster living = (Monster) this.entity;
        LivingEntity target = living.getTarget();

        if(target instanceof Player) return (Player) target;

        return null;
    }

    protected void equipBoss() {

    }

    protected void setTemporalInvunerability() {
        Monster entity = (Monster) this.entity;

        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 10);
        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30,12);

        entity.addPotionEffect(fireResistance);
        entity.addPotionEffect(damageResistance);
    }

    public void deadFunctionality() {
        ItemStack perk = new ItemStack(materialPerk, 1, (short) 0);

        ItemMeta perkMetadata = perk.getItemMeta();

        perkMetadata.setDisplayName(colorTextPerk + perkName); //Deprecated function

        perk.setItemMeta(perkMetadata);

        world.dropItem(this.entity.getLocation(), perk);
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

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event, BarColor color, double maxHealth, String idMetadata, String key, IUltimateLambda lambda) {
        Entity entity = event.getEntity();
        LivingEntity living = (LivingEntity) entity;
        String entityID = String.valueOf(entity.getEntityId());

        boolean isZombie = entity.getType() == EntityType.valueOf(key);
        boolean isSupported = entity.hasMetadata(idMetadata);
        Player playerDamager = Utils.playerDamager(event.getDamager());

        double health = Utils.getHealthByDamage(event.getFinalDamage(), living.getHealth());

        if (!isZombie || !isSupported) return true;

        upsertHealthBar(entity, playerDamager, health, color, maxHealth, idMetadata);

        lambda.ultimates(health, entityID);

        return false;
    }


    public static void handleDamage(EntityDamageEvent event, String key, BarColor color, double maxHealth, String idMetadata, Runnable action) {
        Entity entity = event.getEntity();

        boolean isTypeEntity = entity.getType() == EntityType.valueOf(key);
        boolean isSupportedEntity = entity.hasMetadata(idMetadata);

        if(!isTypeEntity || !isSupportedEntity) return;

        LivingEntity bossEntity = (LivingEntity) entity;

        double health = Utils.getHealthByDamage(event.getFinalDamage(), bossEntity.getHealth());

        upsertHealthBar(entity, null, health, color, maxHealth, idMetadata);
        action.run();
    }

    public static void handleDead(EntityDeathEvent event, String idMetadata, Map<String, ? extends BoosesModel> bosses) {
        Entity entity = event.getEntity();
        String entityID = String.valueOf(entity.getEntityId());

        boolean isSupported = entity.hasMetadata(idMetadata);

        if(!isSupported) return;

        ProgressBar progressBar = new ProgressBar(idMetadata);
        progressBar.disabledBar();
        progressBar.removeAllUsers();

        BoosesModel boss = bosses.get(entityID);

        boss.deadFunctionality();
    }
}
