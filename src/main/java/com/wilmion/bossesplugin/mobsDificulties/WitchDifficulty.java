package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.wilmion.bossesplugin.mobsDificulties.boss.MasterCreeper;
import com.wilmion.bossesplugin.mobsDificulties.boss.MasterWizard;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;


public class WitchDifficulty extends MobDifficulty {

    public WitchDifficulty(Plugin plugin) {
        super(plugin);
    }

    public void onEntityKnockbackByEntityWitchEvent(EntityKnockbackByEntityEvent event) {
        MasterWizard.handleEntityKnockbackByEntity(event);
    }

    public void onDamageWitchEvent(EntityDamageEvent event) {
        MasterWizard.handleDamage(event);
    }

    public void onDamageByEntityWitchEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.CREEPER);
        Boolean isValidCause = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");
        Boolean isContinueBoss = MasterWizard.handleDamageByEntity(event);

        if(!isDead || !isValidCause || !isContinueBoss) return;

        int probability = Utils.getRandomInPercentage();

        if(probability <= 1) {
            new MasterWizard((Player) event.getDamager(), entity.getLocation(), plugin);
        }
    }

    public void onDeathWitchEvent(EntityDeathEvent event) {
        MasterWizard.handleDead(event);
    }


}
