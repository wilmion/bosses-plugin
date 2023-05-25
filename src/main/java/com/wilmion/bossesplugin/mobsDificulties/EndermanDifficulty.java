package com.wilmion.bossesplugin.mobsDificulties;

import com.wilmion.bossesplugin.mobsDificulties.boss.ExpertEnderman;

import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.plugin.Plugin;


public class EndermanDifficulty extends MobDifficulty {
    public EndermanDifficulty(Plugin plugin) {
        super(plugin);
    }

    public void onTeleportEndermanEvent(EntityTeleportEvent event) {
        ExpertEnderman.handleTeleport(event);
    }

    public void onDamageEndermanEvent(EntityDamageEvent event) {
        ExpertEnderman.handleDamage(event);
    }

    public void onDamageByEntityEndermanEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.ENDERMAN);
        Boolean isValidCause = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");
        Boolean isContinueBoss = ExpertEnderman.handleDamageByEntity(event);

        if(!isDead || !isValidCause || !isContinueBoss) return;

        int probability = Utils.getRandomInPercentage();

        if(probability <= 1) {
            new ExpertEnderman(entity.getLocation(), plugin);
        }
    }

    public void onDeathEndermanEvent(EntityDeathEvent event) {
        ExpertEnderman.handleDead(event);
    }
}
