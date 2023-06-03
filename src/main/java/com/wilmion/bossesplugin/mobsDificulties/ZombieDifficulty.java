package com.wilmion.bossesplugin.mobsDificulties;

import com.wilmion.bossesplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.EventUtils;
import com.wilmion.bossesplugin.utils.RandomUtils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;

public class ZombieDifficulty extends MobDifficulty  {
    public ZombieDifficulty() {
        super();
    }

    public void onTransformZombieEvent(EntityTransformEvent event) {
        SupportZombie.handleTransformEntity(event);
    }

    public void onDeathZombieEvent(EntityDeathEvent event) {
       SupportZombie.handleDead(event);
    }

    public void onDamageByEntityZombieEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        world = entity.getWorld();
        server = entity.getServer();

        Boolean isDead = EventUtils.isDeadEntityOnDamage(entity, event.getFinalDamage(), EntityType.ZOMBIE);
        Boolean isAttack = EventUtils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        boolean continueToAlgthSZ = SupportZombie.handleDamageByEntity(event);

        if(!isDead || !isAttack || !continueToAlgthSZ) return;

        Zombie originalZombie = (Zombie) entity;

        Location location = entity.getLocation();

        if(!originalZombie.isAdult()) return;

        int probability = RandomUtils.getRandomInPercentage();

        if(probability <= 1) new SupportZombie(location);
    }

    public void onDamageZombieEvent(EntityDamageEvent event) {
        SupportZombie.handleDamage(event);
    }
}
