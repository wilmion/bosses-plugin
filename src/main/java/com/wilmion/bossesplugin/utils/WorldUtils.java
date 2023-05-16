package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.objects.LocationDataModel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class WorldUtils {
    public static void displayFloatingTextByXSeconds(Location location, String text, Integer seconds, Plugin plugin) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setMarker(true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            armorStand.remove();
        }, 20 * seconds);
    }

    public static Entity getEntityByLocation(Location location, String entityId) {
        location.getChunk().load();
        World world = location.getWorld();
        List<Entity> nearbyEntities = (List<Entity>) world.getNearbyEntities(location, 5, 5, 5);

        for (Entity entity : nearbyEntities) {
            if (entity.getUniqueId().equals(UUID.fromString(entityId))) return entity;
        }

        return null;
    }

    public static Location getLocationByData(LocationDataModel boss, Plugin plugin) {
        World world = plugin.getServer().getWorld(UUID.fromString(boss.getWorldId()));
        return new Location(world, boss.getX(), boss.getY(), boss.getZ());
    }
}
