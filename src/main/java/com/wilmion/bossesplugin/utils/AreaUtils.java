package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

        executeActionInXOfBlocks(rangeX, 1, rangeZ, loc, actionRangeBlocks);
    }

    public static void createSpiral(Location location, Integer range) {
        ActionRangeBlocks actionRangeBlocks = (loc) -> {
            loc.add(0, -1, 0).getBlock().setType(Material.GRASS_BLOCK);
            return true;
        };
        Location loc = location.clone();

        executeActionInXOfBlocks(range, 1, range, location, actionRangeBlocks);

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

    public static void executeActionInPosition(List<Integer[]> posAvailable, Location target, BiConsumer<Location, Integer> lambda) {
        Random random = new Random();

        for (int i = 0; i < posAvailable.size(); i++) {
            Integer index = random.nextInt(posAvailable.size());
            Integer[] dataPos = posAvailable.get(index);
            Location loc = target.clone();

            loc.add(dataPos[0], 0, dataPos[1]);
            loc = WorldUtils.getLocationYInNearAir(loc, 9999);
            posAvailable.remove(index);

            lambda.accept(loc, i);
        }
    }

    public static void executeActionInARangeOfBlock(int range, int modY, Location locationParam, ActionRangeBlocks lambda) {
        for (int x = range * -1; x <= range ; x++) {
            for (int z = range; z >= range * -1 ; z--) {
                Location location = locationParam.clone();

                location.setX(location.getX() + x);
                location.setZ(location.getZ() + z);
                location.setY(location.getY() + modY);

                if(!lambda.action(location)) return;
            }
        }
    }

    public static void executeActionInXOfBlocks(int rangeX, int rangeY, int rangeZ, Location location, ActionRangeBlocks actionRangeBlocks) {
        for (int x = 1; x <= rangeX ; x++) {
            for (int y = 1; y <= rangeY; y++) {
                for (int z = 1; z <= rangeZ; z++) {
                    Location loc = location.clone();

                    loc.setX(loc.getX() + x);
                    loc.setZ(loc.getZ() + z);
                    loc.setY(loc.getY() + y);

                    if(!actionRangeBlocks.action(loc)) return;
                }
            }
        }
    }

    public static void executeActionIn3DRange(Location centerLocation, Integer radius, Consumer<Block> callback) {
        int minX = centerLocation.getBlockX() - radius;
        int minY = centerLocation.getBlockY() - radius;
        int minZ = centerLocation.getBlockZ() - radius;

        int maxX = centerLocation.getBlockX() + radius;
        int maxY = centerLocation.getBlockY() + radius;
        int maxZ = centerLocation.getBlockZ() + radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = centerLocation.getWorld().getBlockAt(x, y, z);
                    callback.accept(block);
                }
            }
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
