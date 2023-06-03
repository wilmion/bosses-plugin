package com.wilmion.bossesplugin.utils;

import org.bukkit.Location;

public class ConstructionUtils {
    public static Double[] getAlters(String alterString) {
        String[] locationParts = alterString.split("-");

        Double x = Double.parseDouble(locationParts[0]);
        Double y = Double.parseDouble(locationParts[1]);
        Double z = Double.parseDouble(locationParts[2]);

        return new Double[]{x,y,z};
    }

    public static String convertLocationToAlters(Location location, Location playerLoc) {
        Location loc = location.add(-playerLoc.getX(), -playerLoc.getY(), -playerLoc.getZ());
        String result = loc.getX() + "-" + loc.getY() + "-" + loc.getZ();

        return result;
    }
}
