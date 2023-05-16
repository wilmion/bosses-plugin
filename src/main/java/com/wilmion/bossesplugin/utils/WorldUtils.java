package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.objects.LocationDataModel;

import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorldUtils {
    public static void displayFloatingTextByXSeconds(Location location, String text, Integer seconds, Plugin plugin) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setCustomName(ChatColor.DARK_GREEN + text);
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

    public static void cleanInRange(Location loc, Integer rangeX, Integer rangeZ) {
        ActionRangeBlocks actionRangeBlocks = (location) -> {
            Integer modY = location.getWorld().getHighestBlockYAt(location);

            for(Double y = location.getY(); y <= modY; y++) {
                Location loc2 = location.clone();

                loc2.setY(y);
                loc2.getBlock().setType(Material.AIR);
            }
        };

        Utils.executeActionInXOfBlocks(rangeX, 1, rangeZ, loc, actionRangeBlocks);
    }
}
