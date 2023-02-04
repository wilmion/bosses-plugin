package com.wilmion.bossesplugin.player;

import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.models.UserDataLevel;
import com.wilmion.bossesplugin.models.ProgressBar;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class PlayerExp extends MobDifficulty {
    public PlayerExp(Plugin plugin) {
        super(plugin);
    }

    public void onMovePlayerEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        UserDataLevel userData = new UserDataLevel(player.getName());

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        int level = userData.getLevel();

        double newHearts = level + 20.00;
        // 20.00 is the base value
        maxHealthAttr.setBaseValue(newHearts);

    }

    public void onDamageByEntityPlayerEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damagerEntity = event.getDamager();
        LivingEntity livingEntity = (LivingEntity) entity;

        damagerEntity = Utils.playerDamager(damagerEntity);

        double health = Utils.getHealthByDamage(event.getFinalDamage(), livingEntity.getHealth());

        Boolean isDead = health <= 0.00;

        if(!isDead || damagerEntity == null) return;

        Player player = (Player) damagerEntity;

        this.setExpPlayer(player, entity);
    }

    private void setExpPlayer(Player player, Entity entity) {
        Boolean isSkeleton = entity.getType() == EntityType.SKELETON;
        Boolean isZombie = entity.getType() == EntityType.ZOMBIE;
        Boolean isCreeper = entity.getType() == EntityType.CREEPER;
        Boolean isDrowned = entity.getType() == EntityType.DROWNED;
        Boolean isWitch = entity.getType() == EntityType.WITCH;
        Boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        Boolean isZombieVillager = entity.getType() == EntityType.ZOMBIE_VILLAGER;
        Boolean isSpider = entity.getType() == EntityType.SPIDER;
        Boolean isSlime = entity.getType() == EntityType.SLIME;
        Boolean isZombiePig = entity.getType() == EntityType.ZOMBIFIED_PIGLIN;
        Boolean isPliginBrute = entity.getType() == EntityType.PIGLIN_BRUTE;
        Boolean isPligin = entity.getType() == EntityType.PIGLIN;
        Boolean isWitherSkeleton = entity.getType() == EntityType.WITHER_SKELETON;
        Boolean isBlaze = entity.getType() == EntityType.BLAZE;
        Boolean isPillager = entity.getType() == EntityType.PILLAGER;
        Boolean isVindicator = entity.getType() == EntityType.VINDICATOR;

        if (isSkeleton || isWitherSkeleton) this.addExp(player, 5);
        if (isZombieVillager || isZombie || isZombiePig) this.addExp(player,2);
        if (isCreeper) this.addExp(player,8);
        if (isDrowned || isBlaze) this.addExp(player, 3);
        if (isWitch || isEnderman) this.addExp(player, 6);
        if (isSpider) this.addExp(player,2);
        if (isSlime) this.addExp(player,1);
        if (isPillager || isVindicator || isPligin || isPliginBrute) this.addExp(player, 4);
    }

    private void addExp(Player player, int exp) {
        String name = player.getName();
        String trimedName = name.trim();

        UserDataLevel userData = new UserDataLevel(name);

        Runnable handleLevelUp = () -> {
            int level = userData.getLevel();
            String title = ChatColor.BLUE + "Bien hecho! Subiste a nivel " + level;

            Utils.setTitleOnPlayer(player, title, "<=> Crecez en vida <=>");
            player.playSound(player.getLocation(),Sound.ENTITY_WITHER_SKELETON_HURT, 1 , 0 );
        };

        userData.addExp(exp, handleLevelUp);

        ProgressBar progressBar = new ProgressBar(trimedName);

        int nextLevel = userData.getLevel() + 1;

        progressBar.setTitle("Para nivel " + nextLevel);

        double progress = userData.getRemainPercentage();

        progressBar.setProgress(progress);
        progressBar.addPlayer(player);
        progressBar.enableBar();

        ProgressBar reference = progressBar;

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, reference::disabledBar, 100);
    }
}
