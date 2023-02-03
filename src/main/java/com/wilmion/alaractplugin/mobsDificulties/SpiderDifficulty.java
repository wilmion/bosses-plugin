package com.wilmion.alaractplugin.mobsDificulties;

import com.wilmion.alaractplugin.mobsDificulties.boss.QueenSpider;
import com.wilmion.alaractplugin.mobsDificulties.boss.SoldierSpider;
import com.wilmion.alaractplugin.models.MobDifficulty;
import com.wilmion.alaractplugin.utils.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class SpiderDifficulty extends MobDifficulty {
    public SpiderDifficulty(Plugin plugin) {
        super(plugin);
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
            new QueenSpider((Player) event.getDamager(), location, this.plugin);
            return;
        }

        if(probability > 1 && probability < 10) {
            new SoldierSpider((Player) event.getDamager(), location, this.plugin);
        }
    }
}
