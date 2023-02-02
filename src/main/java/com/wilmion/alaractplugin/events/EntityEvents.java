package com.wilmion.alaractplugin.events;

import com.wilmion.alaractplugin.mobsDificulties.CreeperDifficulty;
import com.wilmion.alaractplugin.mobsDificulties.SkeletonDifficulty;
import com.wilmion.alaractplugin.mobsDificulties.SpiderDifficulty;
import com.wilmion.alaractplugin.mobsDificulties.ZombieDifficulty;
import com.wilmion.alaractplugin.player.PlayerDifficulty;
import com.wilmion.alaractplugin.player.PlayerExp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class EntityEvents implements Listener {
    Plugin plugin;

    PlayerDifficulty playerDifficulty;
    PlayerExp playerExp;
    ZombieDifficulty zombie;
    CreeperDifficulty creeper;
    SkeletonDifficulty skeleton;
    SpiderDifficulty spider;

    public EntityEvents(Plugin plugin) {
        this.plugin = plugin;

        this.playerDifficulty = new PlayerDifficulty(plugin);
        this.playerExp = new PlayerExp(plugin);

        this.zombie = new ZombieDifficulty(plugin);
        this.creeper = new CreeperDifficulty(plugin);
        this.skeleton = new SkeletonDifficulty(plugin);
        this.spider = new SpiderDifficulty(plugin);
    }

    @EventHandler
    public void onMovePlayer(PlayerMoveEvent event) {
        playerExp.onMovePlayerEvent(event);
        playerDifficulty.onMovePlayerDifficultyEvent(event);
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        skeleton.onShootSkeletonEvent(event);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        zombie.onDamageZombieEvent(event);
        skeleton.onDamageSkeletonEvent(event);
        spider.onDamageSpiderEvent(event);
        creeper.onDamageCrepperEvent(event);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        playerExp.onDamageByEntityPlayerEvent(event);
        zombie.onDamageByEntityZombieEvent(event);
        skeleton.onDamageByEntitySkeletonEvent(event);
        spider.OnDamageByEntitySpiderEvent(event);
    }

    @EventHandler
    public void onDeathEntity(EntityDeathEvent event) {
        zombie.onDeathZombieEvent(event);
        skeleton.onDeadSkeletonEvent(event);
        spider.OnDeadSpiderEvent(event);
    }
}
