package com.wilmion.bossesplugin.mobsDificulties;

import com.wilmion.bossesplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class ZombieDifficulty extends MobDifficulty  {
    public ZombieDifficulty(Plugin plugin) {
        super(plugin);
    }

    public void onDeathZombieEvent(EntityDeathEvent event) {
       SupportZombie.handleDead(event);
    }

    public void onDamageByEntityZombieEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        world = entity.getWorld();
        server = entity.getServer();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getFinalDamage(), EntityType.ZOMBIE);
        Boolean isAttack = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        boolean continueToAlgthSZ = SupportZombie.handleDamageByEntity(event);

        if(!isDead || !isAttack || !continueToAlgthSZ) return;

        Zombie originalZombie = (Zombie) entity;

        Location location = entity.getLocation();

        if(!originalZombie.isAdult()) return;

        int probability = Utils.getRandomInPercentage();


        if(probability <= 1) new SupportZombie((Player) event.getDamager(), location, this.plugin);
    }

    public void onDamageZombieEvent(EntityDamageEvent event) {
        SupportZombie.handleDamage(event);
    }
}
