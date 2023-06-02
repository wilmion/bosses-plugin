package com.wilmion.bossesplugin.mobsDificulties;

import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;
import com.wilmion.bossesplugin.mobsDificulties.boss.MasterSkeleton;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.*;

public class SkeletonDifficulty extends MobDifficulty {
    public SkeletonDifficulty() {
        super();
    }

    public void onChangeTargetSkeletonEvent(EntityTargetEvent event) {
        Boolean isSkeleton = event.getEntity() instanceof Skeleton;
        Boolean isTargetSkeleton = event.getTarget() instanceof Skeleton;

        if(isSkeleton && isTargetSkeleton) event.setCancelled(true);
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

        if(probability <= 1) new MasterSkeleton(location);
    }

}
