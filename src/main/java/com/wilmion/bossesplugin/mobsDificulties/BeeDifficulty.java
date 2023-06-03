package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.mobsDificulties.boss.QueenBee;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.EventUtils;
import com.wilmion.bossesplugin.utils.RandomUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;


public class BeeDifficulty extends MobDifficulty {

    public BeeDifficulty() {
        super();
    }

    public void onEntityKnockbackByEntityBeeEvent(EntityKnockbackByEntityEvent event) {
        QueenBee.handleEntityKnockbackByEntity(event);
    }

    public void onDamageBeeEvent(EntityDamageEvent event) {
        QueenBee.handleDamage(event);
    }

    public void onDamageByEntityBeeEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Boolean isDead = EventUtils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.BEE);
        Boolean isValidCause = EventUtils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");
        Boolean isContinueBoss = QueenBee.handleDamageByEntity(event);

        if(!isDead || !isValidCause || !isContinueBoss) return;

        int probability = RandomUtils.getRandomInPercentage();

        if(probability <= 1) new QueenBee(entity.getLocation());
    }

    public void onDeathBeeEvent(EntityDeathEvent event) {
        QueenBee.handleDead(event);
    }
}
