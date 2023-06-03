package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.mobsDificulties.boss.QueenSpider;
import com.wilmion.bossesplugin.mobsDificulties.boss.SoldierSpider;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.EventUtils;
import com.wilmion.bossesplugin.utils.RandomUtils;

import io.papermc.paper.event.entity.EntityMoveEvent;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class SpiderDifficulty extends MobDifficulty {
    public SpiderDifficulty() {
        super();
    }

    public void onEntityMoveSpiderEvent(EntityMoveEvent event) {
        Entity entity = event.getEntity();

        if(!(entity instanceof Monster)) return;

        Monster monster = (Monster) entity;
        List<Entity> entities = monster.getPassengers();

        if(entities.stream().count() == 0l || !monster.isClimbing() || monster.getTarget() != null) return;

        monster.setVelocity(new Vector(0, -1, 0));
    }

    public void onEntityKnockbackByEntitySpiderEvent(EntityKnockbackByEntityEvent event) {
        QueenSpider.handleEntityKnockbackByEntity(event);
    }

    public void OnDeadSpiderEvent(EntityDeathEvent event) {
        SoldierSpider.handleDead(event);
        QueenSpider.handleDead(event);
    }

    public void onDamageSpiderEvent(EntityDamageEvent event) {
        SoldierSpider.handleDamage(event);
        QueenSpider.handleDamage(event);
    }

    public void OnDamageByEntitySpiderEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        world = entity.getWorld();
        server = entity.getServer();

        Boolean isDead = EventUtils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.SPIDER);
        Boolean isAttack = EventUtils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        Boolean isContinueSoldierBoss = SoldierSpider.handleDamageByEntity(event);
        Boolean isContinueQueenBoss = QueenSpider.handleDamageByEntity(event);


        if(!isDead || !isAttack || !isContinueSoldierBoss || !isContinueQueenBoss) return;

        int probability = RandomUtils.getRandomInPercentage();

        Location location = entity.getLocation();

        if(probability <= 1) {
            new QueenSpider(location);
            return;
        }

        if(probability > 1 && probability < 10) new SoldierSpider(location);
    }
}
