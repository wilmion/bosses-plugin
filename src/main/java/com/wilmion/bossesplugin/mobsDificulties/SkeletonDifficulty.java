package com.wilmion.bossesplugin.mobsDificulties;

import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;
import com.wilmion.bossesplugin.mobsDificulties.boss.MasterSkeleton;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.Plugin;

public class SkeletonDifficulty extends MobDifficulty {
    public SkeletonDifficulty(Plugin plugin) {
        super(plugin);
    }

    public void onShootSkeletonEvent(EntityShootBowEvent event) {
        MasterSkeleton.handleShoot(event);
    }

    public void onDeadSkeletonEvent(EntityDeathEvent event) {
        MasterSkeleton.handleDead(event);
    }

    public void onDamageSkeletonEvent(EntityDamageEvent event) {
        MasterSkeleton.handleDamage(event);
    }

    public void onDamageByEntitySkeletonEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        world = entity.getWorld();
        server = entity.getServer();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.SKELETON);
        Boolean isAttack = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        Boolean isContinueBoss = MasterSkeleton.handleDamageByEntity(event);

        if(!isDead || !isAttack || !isContinueBoss) return;

        int probability = Utils.getRandomInPercentage();

        Location location = entity.getLocation();

        if(probability <= 1) new MasterSkeleton(location, this.plugin);

    }

}
