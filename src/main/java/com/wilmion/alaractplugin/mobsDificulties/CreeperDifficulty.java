package com.wilmion.alaractplugin.mobsDificulties;

import com.wilmion.alaractplugin.models.MobDifficulty;
import com.wilmion.alaractplugin.utils.Utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class CreeperDifficulty extends MobDifficulty {
    public CreeperDifficulty(Plugin plugin) {
        super(plugin);
    }
    public void onDamageCrepperEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.CREEPER);
        Boolean isValidCause = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        if(!isDead || !isValidCause) return;

        int probability = Utils.getRandomIntNumber();

        if(probability <= 3) {
            spawnCreeperCharge(entity.getLocation());
            return;
        }

        if (probability <= 2) {
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
