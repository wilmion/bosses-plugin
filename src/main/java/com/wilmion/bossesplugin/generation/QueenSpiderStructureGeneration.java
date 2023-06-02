package com.wilmion.bossesplugin.generation;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.utils.AreaUtils;
import com.wilmion.bossesplugin.utils.PluginUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.Optional;
import java.util.logging.Logger;

public class QueenSpiderStructureGeneration {
    private BuildCommand buildCommand;
    private String aperture = "Se rumorea que algo acecha en las profundidades \uD83D\uDD77\uFE0F, está cerca de:";

    public QueenSpiderStructureGeneration() {
        Optional<Location> annLocationBuilt = StructureGeneration.getLocationStructureWhenIsBuilt("spider-base");

        this.buildCommand = new BuildCommand();

        if(annLocationBuilt.isEmpty()) generate(PluginUtils.getPlugin().getLogger());
        else StructureGeneration.addTextLocationToShow(aperture, annLocationBuilt.get());
    }

    private void generate(Logger logger) {
        logger.info("Building Spider base...");

        StructureGeneration.posGeneration = new Integer[]{800, 1200, 500, 1000};

        Location location = StructureGeneration.getLocationStructure(40);

        AreaUtils.cleanWithLimit(100, 13, 100, location.clone().add(10, 0, 10));
        StructureGeneration.generateBuild(location, "spider-base", buildCommand);
        StructureGeneration.addTextLocationToShow(aperture, location);
        generateLadderToTheGround(location);

        logger.info("Built Spider Base \uD83D\uDD77\uFE0F");
    }

    private void generateLadderToTheGround(Location location) {
        Location loc = location.clone().add(79, 0, 0);
        Integer iterations = 0;
        Boolean hasGround = false;

        while (!hasGround) {
            Integer greaterY = iterations * 1;
            Integer greaterZ = -2 * (iterations + 1);
            Location finalLoc = loc.clone().add(0, greaterY, greaterZ);
            Integer maxY = finalLoc.getWorld().getHighestBlockYAt(finalLoc);

            hasGround = maxY.doubleValue() <= finalLoc.getY() + 1;

            AreaUtils.cleanWithLimit(6, 6, 2, finalLoc);
            buildCommand.buildStructure(finalLoc, "spider-entry", "0deg");
            iterations++;

            if(!hasGround) continue;
            //Generate banners in the last interation
            BlockData banner = Bukkit.createBlockData("minecraft:lime_banner[rotation=8]");

            finalLoc.clone().add(2, 2,1).getBlock().setBlockData(banner);
            finalLoc.clone().add(5, 2,1).getBlock().setBlockData(banner);
        }
    }
}
