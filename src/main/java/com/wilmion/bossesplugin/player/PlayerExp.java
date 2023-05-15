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
        Integer level = userData.getLevel();
        Double newHearts = level + 20.00;

        maxHealthAttr.setBaseValue(newHearts); // 20.00 is the base value

    }

    public void onDamageByEntityPlayerEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damagerEntity = event.getDamager();
        LivingEntity livingEntity = (LivingEntity) entity;
        double health = Utils.getHealthByDamage(event.getFinalDamage(), livingEntity.getHealth());

        damagerEntity = Utils.livingDamager(damagerEntity, Player.class);

        Boolean isDead = health <= 0.00;

        if(!isDead || damagerEntity == null) return;

        Player player = (Player) damagerEntity;

        String name = entity.getName();

        addExp(player, name.length());
    }

    private void addExp(Player player, Integer exp) {
        String trimmedName = player.getName().trim();
        UserDataLevel userData = new UserDataLevel(player.getName());

        Runnable handleLevelUp = () -> {
            Integer level = userData.getLevel();
            String title = ChatColor.BLUE + "Bien hecho! Subiste a nivel " + level;

            Utils.setTitleOnPlayer(player, title, "<=> Crecez en vida <=>");

            player.playSound(player.getLocation(),Sound.ENTITY_WITHER_SKELETON_HURT, 1 , 0 );
        };

        userData.addExp(exp, handleLevelUp);

        ProgressBar progressBar = new ProgressBar(trimmedName);
        Integer nextLevel = userData.getLevel() + 1;
        Double progress = userData.getRemainPercentage();

        progressBar.setTitle("Para nivel " + nextLevel);
        progressBar.setProgress(progress);
        progressBar.addPlayer(player);
        progressBar.enableBar();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, progressBar::disabledBar, 100);
    }
}
