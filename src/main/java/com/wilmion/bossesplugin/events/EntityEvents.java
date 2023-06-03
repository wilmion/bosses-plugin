package com.wilmion.bossesplugin.events;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.generation.StructureGeneration;
import com.wilmion.bossesplugin.mobsDificulties.*;
import com.wilmion.bossesplugin.models.metadata.BlockMetadata;
import com.wilmion.bossesplugin.player.PlayerDifficulty;
import com.wilmion.bossesplugin.player.PlayerExp;
import com.wilmion.bossesplugin.utils.PluginUtils;

import io.papermc.paper.event.entity.EntityMoveEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerLoadEvent;

public class EntityEvents implements Listener {
    PlayerDifficulty playerDifficulty = new PlayerDifficulty();
    PlayerExp playerExp = new PlayerExp();
    ZombieDifficulty zombie = new ZombieDifficulty();
    CreeperDifficulty creeper = new CreeperDifficulty();
    SkeletonDifficulty skeleton = new SkeletonDifficulty();
    SpiderDifficulty spider = new SpiderDifficulty();
    WitchDifficulty witch = new WitchDifficulty();
    EndermanDifficulty enderman = new EndermanDifficulty();
    BeeDifficulty bee = new BeeDifficulty();

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
    public void onTarget(EntityTargetEvent event) {
        skeleton.onChangeTargetSkeletonEvent(event);
    }

    @EventHandler
    public void onEntityMoveEvent(EntityMoveEvent event) {
        spider.onEntityMoveSpiderEvent(event);
    }

    @EventHandler
    public void onTransformEntity(EntityTransformEvent event) {
        zombie.onTransformZombieEvent(event);
    }

    @EventHandler
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        creeper.onEntityKnockbackByEntityCreeperEvent(event);
        witch.onEntityKnockbackByEntityWitchEvent(event);
        spider.onEntityKnockbackByEntitySpiderEvent(event);
        bee.onEntityKnockbackByEntityBeeEvent(event);
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
    public void onTeleport(EntityTeleportEvent event) {
        enderman.onTeleportEndermanEvent(event);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        zombie.onDamageZombieEvent(event);
        skeleton.onDamageSkeletonEvent(event);
        spider.onDamageSpiderEvent(event);
        creeper.onDamageCreeperEvent(event);
        witch.onDamageWitchEvent(event);
        enderman.onDamageEndermanEvent(event);
        bee.onDamageBeeEvent(event);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        playerExp.onDamageByEntityPlayerEvent(event);
        zombie.onDamageByEntityZombieEvent(event);
        skeleton.onDamageByEntitySkeletonEvent(event);
        spider.OnDamageByEntitySpiderEvent(event);
        creeper.onDamageByEntityCrepperEvent(event);
        witch.onDamageByEntityWitchEvent(event);
        enderman.onDamageByEntityEndermanEvent(event);
        bee.onDamageByEntityBeeEvent(event);
    }

    @EventHandler
    public void onDeathEntity(EntityDeathEvent event) {
        zombie.onDeathZombieEvent(event);
        skeleton.onDeadSkeletonEvent(event);
        spider.OnDeadSpiderEvent(event);
        creeper.onDeathCreeperEvent(event);
        witch.onDeathWitchEvent(event);
        enderman.onDeathEndermanEvent(event);
        bee.onDeathBeeEvent(event);
    }

    @EventHandler
    public void onSeverLoad(ServerLoadEvent event) {
        BlockMetadata.getData();
        new StructureGeneration();

        if (PluginUtils.getPlugin().isEnabled()) PlayerDifficulty.watchDifficulty();
    }
}
