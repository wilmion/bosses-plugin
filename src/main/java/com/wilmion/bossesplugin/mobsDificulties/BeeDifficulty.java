package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.mobsDificulties.boss.QueenBee;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;


public class BeeDifficulty extends MobDifficulty {

    public BeeDifficulty(Plugin plugin) {
        super(plugin);
    }

    public void onEntityKnockbackByEntityBeeEvent(EntityKnockbackByEntityEvent event) {
        QueenBee.handleEntityKnockbackByEntity(event);
    }

    public void onDamageBeeEvent(EntityDamageEvent event) {
        QueenBee.handleDamage(event);
    }

    public void onDamageByEntityBeeEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.BEE);
        Boolean isValidCause = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");
        Boolean isContinueBoss = QueenBee.handleDamageByEntity(event);

        if(!isDead || !isValidCause || !isContinueBoss) return;

        int probability = Utils.getRandomInPercentage();

        if(probability <= 1) {
            new QueenBee(entity.getLocation(), plugin);
        }
    }

    public void onDeathBeeEvent(EntityDeathEvent event) {
        QueenBee.handleDead(event);
    }
}
