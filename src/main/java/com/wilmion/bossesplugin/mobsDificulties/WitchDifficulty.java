package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.mobsDificulties.boss.MasterWizard;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.EventUtils;
import com.wilmion.bossesplugin.utils.RandomUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class WitchDifficulty extends MobDifficulty {

    public WitchDifficulty() {
        super();
    }

    public void onEntityKnockbackByEntityWitchEvent(EntityKnockbackByEntityEvent event) {
        MasterWizard.handleEntityKnockbackByEntity(event);
    }

    public void onDamageWitchEvent(EntityDamageEvent event) {
        MasterWizard.handleDamage(event);
    }

    public void onDamageByEntityWitchEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Boolean isDead = EventUtils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.WITCH);
        Boolean isValidCause = EventUtils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");
        Boolean isContinueBoss = MasterWizard.handleDamageByEntity(event);

        if(!isDead || !isValidCause || !isContinueBoss) return;

        int probability = RandomUtils.getRandomInPercentage();

        if(probability <= 1) new MasterWizard(entity.getLocation());
    }

    public void onDeathWitchEvent(EntityDeathEvent event) {
        MasterWizard.handleDead(event);
    }
}
