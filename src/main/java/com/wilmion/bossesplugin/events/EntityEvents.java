package com.wilmion.bossesplugin.events;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.wilmion.bossesplugin.generation.StructureGeneration;
import com.wilmion.bossesplugin.mobsDificulties.*;
import com.wilmion.bossesplugin.models.BlockMetadata;
import com.wilmion.bossesplugin.models.KeepMetadata;
import com.wilmion.bossesplugin.player.PlayerDifficulty;
import com.wilmion.bossesplugin.player.PlayerExp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

public class EntityEvents implements Listener {
    Plugin plugin;

    PlayerDifficulty playerDifficulty;
    PlayerExp playerExp;
    ZombieDifficulty zombie;
    CreeperDifficulty creeper;
    SkeletonDifficulty skeleton;
    SpiderDifficulty spider;
    WitchDifficulty witch;

    public EntityEvents(Plugin plugin) {
        this.plugin = plugin;

        this.playerDifficulty = new PlayerDifficulty(plugin);
        this.playerExp = new PlayerExp(plugin);

        this.zombie = new ZombieDifficulty(plugin);
        this.creeper = new CreeperDifficulty(plugin);
        this.skeleton = new SkeletonDifficulty(plugin);
        this.spider = new SpiderDifficulty(plugin);
        this.witch = new WitchDifficulty(plugin);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        StructureGeneration.sendPlayerMessageLocation(event.getPlayer());
    }

    @EventHandler
    public void onMovePlayer(PlayerMoveEvent event) {
        playerExp.onMovePlayerEvent(event);
        playerDifficulty.onMovePlayerDifficultyEvent(event);
    }

    @EventHandler
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        creeper.onEntityKnockbackByEntityCreeperEvent(event);
        witch.onEntityKnockbackByEntityWitchEvent(event);
        spider.onEntityKnockbackByEntitySpiderEvent(event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        creeper.onEntityExplodeCreeperEvent(event);
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
        creeper.onDamageCreeperEvent(event);
        witch.onDamageWitchEvent(event);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        playerExp.onDamageByEntityPlayerEvent(event);
        zombie.onDamageByEntityZombieEvent(event);
        skeleton.onDamageByEntitySkeletonEvent(event);
        spider.OnDamageByEntitySpiderEvent(event);
        creeper.onDamageByEntityCrepperEvent(event);
        witch.onDamageByEntityWitchEvent(event);
    }

    @EventHandler
    public void onDeathEntity(EntityDeathEvent event) {
        zombie.onDeathZombieEvent(event);
        skeleton.onDeadSkeletonEvent(event);
        spider.OnDeadSpiderEvent(event);
        creeper.onDeathCreeperEvent(event);
        witch.onDeathWitchEvent(event);
    }

    @EventHandler
    public void onSeverLoad(ServerLoadEvent event) {
        BlockMetadata.getData();
        new StructureGeneration(plugin);
        KeepMetadata keepMetadata = new KeepMetadata(plugin);
        keepMetadata.keepMetadata();
    }
}
