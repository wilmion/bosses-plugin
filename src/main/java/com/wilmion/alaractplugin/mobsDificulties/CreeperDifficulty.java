package com.wilmion.alaractplugin.mobsDificulties;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.wilmion.alaractplugin.utils.Utils;

public class CreeperDifficulty implements Listener {
    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.CREEPER);
        Boolean isValidCause = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        if(!isDead || !isValidCause) return;

        int probability = Utils.getRandomIntNumber();

        if(probability <= 3) {
            spawnCreeperCharge(entity.getLocation());
            entity.getServer().broadcastMessage("Aparecio un creeper sobrecargado, yo que tu me alejaria mua jua jua");
            return;
        }

        if (probability <= 2) {
            spawnTNTCharge(entity.getLocation());
            entity.getServer().broadcastMessage("Aparecio un TNT 0.0");
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
