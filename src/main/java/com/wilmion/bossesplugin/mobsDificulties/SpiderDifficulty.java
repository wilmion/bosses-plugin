package com.wilmion.bossesplugin.mobsDificulties;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.mobsDificulties.boss.QueenSpider;
import com.wilmion.bossesplugin.mobsDificulties.boss.SoldierSpider;
import com.wilmion.bossesplugin.models.MobDifficulty;
import com.wilmion.bossesplugin.utils.Utils;

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
import java.util.concurrent.atomic.AtomicReference;

public class SpiderDifficulty extends MobDifficulty {
    public SpiderDifficulty() {
        super();
    }

    public void onEntityMoveSpiderEvent(EntityMoveEvent event) {
        Entity entity = event.getEntity();

        if(!(entity instanceof Monster)) return;

        Monster monster = (Monster) entity;
        List<Entity> entities = monster.getPassengers();

        if(entities.stream().count() == 0l) return;

        for (int i = 1; i <= 2; i++) {
            AtomicReference<Boolean> isSolid = new AtomicReference<>(false);

            ActionRangeBlocks action = (location) -> {
                if(!isSolid.get()) isSolid.set(location.getBlock().getType().isSolid());
                return !isSolid.get();
            };

            Utils.executeActionInARangeOfBlock(1, i, monster.getLocation().clone(), action);

            if (!isSolid.get()) continue;

            monster.setVelocity(new Vector(0, -1, 0));
            return;
        }
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

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.SPIDER);
        Boolean isAttack = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        Boolean isContinueSoldierBoss = SoldierSpider.handleDamageByEntity(event);
        Boolean isContinueQueenBoss = QueenSpider.handleDamageByEntity(event);


        if(!isDead || !isAttack || !isContinueSoldierBoss || !isContinueQueenBoss) return;

        int probability = Utils.getRandomInPercentage();

        Location location = entity.getLocation();

        if(probability <= 1) {
            new QueenSpider(location);
            return;
        }

        if(probability > 1 && probability < 10) new SoldierSpider(location);
    }
}
