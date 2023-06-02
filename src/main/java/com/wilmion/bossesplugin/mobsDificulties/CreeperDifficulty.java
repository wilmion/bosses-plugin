package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.mobsDificulties.boss.MasterCreeper;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CreeperDifficulty extends MobDifficulty {
    public CreeperDifficulty() {
        super();
    }

    public void onEntityKnockbackByEntityCreeperEvent(EntityKnockbackByEntityEvent event) {
        MasterCreeper.handleEntityKnockbackByEntity(event);
    }

    public void onEntityExplodeCreeperEvent(EntityExplodeEvent event) {
        MasterCreeper.handleExplode(event);
    }

    public void onDeathCreeperEvent(EntityDeathEvent event) {
        MasterCreeper.handleDead(event);
    }

    public void onDamageCreeperEvent(EntityDamageEvent event) {
        MasterCreeper.handleDamage(event);
    }

    public void onDamageByEntityCrepperEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.CREEPER);
        Boolean isValidCause = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");
        Boolean isContinueBoss = MasterCreeper.handleDamageByEntity(event);

        if(!isDead || !isValidCause || !isContinueBoss) return;

        int probability = Utils.getRandomInPercentage();

        if(probability <= 1) {
            new MasterCreeper(entity.getLocation());
            return;
        }


        if(probability <= 10) {
            spawnCreeperCharge(entity.getLocation());
            return;
        }

        if (probability <= 13) {
            spawnTNTCharge(entity.getLocation());
        }
    }

    private void spawnCreeperCharge(Location location) {
        World world = location.getWorld();

        Creeper creeper =  world.spawn(location, Creeper.class);

        creeper.setPowered(true);

    }

    private void spawnTNTCharge(Location location) {
        World world = location.getWorld();

        world.spawn(location, TNTPrimed.class);
    }
}
