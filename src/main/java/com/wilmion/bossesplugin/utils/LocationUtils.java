package com.wilmion.bossesplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class LocationUtils {
    public static String convertLocationAsString(Location loc, Location playerLoc) {
        StringBuilder dataBuilder = new StringBuilder();
        Location location = loc.clone().add(-playerLoc.getX(), -playerLoc.getY(), -playerLoc.getZ());

        dataBuilder.append(location.getX()).append(":");
        dataBuilder.append(location.getY()).append(":");
        dataBuilder.append(location.getZ()).append(":");
        dataBuilder.append(location.getYaw()).append(":");
        dataBuilder.append(location.getPitch()).append(":");

        return ChatColor.translateAlternateColorCodes('&', dataBuilder.toString());
    }

    public static Location getLocationFromString(String data, Location playerLoc) {
        String[] locationParts = data.split(":");
        Location location = playerLoc.clone();

        Double x = Double.parseDouble(locationParts[0]);
        Double y = Double.parseDouble(locationParts[1]);
        Double z = Double.parseDouble(locationParts[2]);
        Float yaw = Float.parseFloat(locationParts[3]);
        Float pitch = Float.parseFloat(locationParts[4]);

        location.add(x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);

        return location;
    }
}
