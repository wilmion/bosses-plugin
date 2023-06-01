package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;

import org.bukkit.Location;
import org.bukkit.Material;

public class AreaUtils {
    public static void cleanWithLimit(Integer rangeX, Integer limitY, Integer rangeZ, Location loc) {
        ActionRangeBlocks actionRangeBlocks = (location) -> {
            Double modY = location.getY() + limitY;

            for(Double y = location.getY(); y <= modY.intValue(); y++) {
                Location loc2 = location.clone();

                loc2.setY(y);
                loc2.getBlock().setType(Material.AIR);
            }
            return true;
        };

        Utils.executeActionInXOfBlocks(rangeX, 1, rangeZ, loc, actionRangeBlocks);
    }

    public static void createSpiral(Location location, Integer range) {
        ActionRangeBlocks actionRangeBlocks = (loc) -> {
            loc.add(0, -1, 0).getBlock().setType(Material.GRASS_BLOCK);
            return true;
        };
        Location loc = location.clone();

        Utils.executeActionInXOfBlocks(range, 1, range, location, actionRangeBlocks);

        loc.add(1, 1, 1);

        for(Integer i = 1; i <= 120; i++) {
            Integer modStarIndex = i - 1;
            Integer maxRange = range + i;
            Integer maxRangeLestOne = range + modStarIndex;
            Integer startRange = maxRangeLestOne;

            loc.add(0 ,1,0);

            iterateInSpiral(loc, 0, -i, -modStarIndex, maxRange, "X");
            iterateInSpiral(loc, startRange, 0, -modStarIndex, maxRange, "Z");
            iterateInSpiral(loc, 0, startRange, -i, maxRangeLestOne, "X");
            iterateInSpiral(loc, -i, 0, -i, maxRangeLestOne, "Z");
        }
    }

    private static void iterateInSpiral(Location location, Integer alterX, Integer alterZ, Integer startIndex, Integer endIndex, String iterationOn) {
        Location loc = location.clone();

        loc.add(alterX, 0, alterZ);

        for(Integer i = startIndex; i < endIndex; i++) {
            Location locPrepare = loc.clone();

            if(iterationOn.equals("X")) locPrepare.add(i, 0, 0);
            else locPrepare.add(0,0, i);

            Integer maxY = locPrepare.getWorld().getHighestBlockYAt(locPrepare);
            Boolean isSolid = locPrepare.clone().add(0,-1,0).getBlock().isSolid();
            Boolean isNotLeaves = !locPrepare.clone().add(0,-1,0).getBlock().getType().name().endsWith("_LEAVES");
            Boolean isValidToSetGrass = isSolid && isNotLeaves;

            if(isValidToSetGrass) locPrepare.clone().add(0, -1, 0).getBlock().setType(Material.GRASS_BLOCK);

            for(Double y = locPrepare.getY(); y <= maxY; y++) {
                Location locFinal = locPrepare.clone();

                locFinal.setY(y);
                locFinal.getBlock().setType(Material.AIR);
            }
        }
    }
}
