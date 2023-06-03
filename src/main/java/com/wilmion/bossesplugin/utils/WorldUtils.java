package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.objects.LocationDataModel;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class WorldUtils {
    public static void displayFloatingTextByXSeconds(Location location, String text, Integer seconds) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setCustomName(ChatColor.DARK_GREEN + text);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setMarker(true);

        Bukkit.getScheduler().runTaskLater(PluginUtils.getPlugin(), () -> armorStand.remove(), 20 * seconds);
    }

    public static Location getLocationByData(LocationDataModel boss) {
        World world = PluginUtils.getPlugin().getServer().getWorld(UUID.fromString(boss.getWorldId()));
        return new Location(world, boss.getX(), boss.getY(), boss.getZ());
    }

    public static Location getLocationYInNearAir(Location location, Integer maximumRange) {
        Location result = location.clone();
        Boolean isValid = false;
        Integer iterations = 0;

        while(!isValid && iterations < maximumRange) {
            Location nextYLoc = result.clone();
            nextYLoc.setY(nextYLoc.getY() + 1);

            boolean currentBlockIsAir = result.getBlock().getType().isAir();
            boolean nextBlockIsAir = result.getBlock().getType().isAir();

            isValid = currentBlockIsAir && nextBlockIsAir;
            iterations++;

            if(!isValid) result.setY(result.getY() + 1);
        }

        if(!isValid) return null;

        return result;
    }

    public static void cleanInRange(Location loc, Integer rangeX, Integer rangeZ) {
        ActionRangeBlocks actionRangeBlocks = (location) -> {
            Integer modY = location.getWorld().getHighestBlockYAt(location);

            for(Double y = location.getY(); y <= modY; y++) {
                Location loc2 = location.clone();

                loc2.setY(y);
                loc2.getBlock().setType(Material.AIR);
            }
            return true;
        };

        AreaUtils.executeActionInXOfBlocks(rangeX, 1, rangeZ, loc, actionRangeBlocks);
    }
}
