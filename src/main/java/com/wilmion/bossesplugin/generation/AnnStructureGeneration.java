package com.wilmion.bossesplugin.generation;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.utils.AreaUtils;
import com.wilmion.bossesplugin.utils.PluginUtils;
import com.wilmion.bossesplugin.utils.WorldUtils;

import org.bukkit.Location;

import java.util.Optional;
import java.util.logging.Logger;

public class AnnStructureGeneration {
    private BuildCommand buildCommand;
    private String aperture ="Hay algo ominoso y esquelético por aquí \uD83D\uDC80, está cerca de:";

    public AnnStructureGeneration() {
        Optional<Location> annLocationBuilt = StructureGeneration.getLocationStructureWhenIsBuilt("ann-tower");

        this.buildCommand = new BuildCommand();

        if(annLocationBuilt.isEmpty()) generate(PluginUtils.getPlugin().getLogger());
        else StructureGeneration.addTextLocationToShow(aperture, annLocationBuilt.get());
    }

    private void generate(Logger logger) {
        logger.info("Building Ann Tower...");

        StructureGeneration.posGeneration = new Integer[]{0, 400, 10, 350};

        Location location = StructureGeneration.getLocationStructure();
        Integer rangeClean = 120;

        WorldUtils.cleanInRange(location.clone(), rangeClean, rangeClean);
        AreaUtils.createSpiral(location, rangeClean);
        StructureGeneration.generateBuild(location, "ann-tower", buildCommand);
        StructureGeneration.addTextLocationToShow(aperture, location);

        logger.info("Ann Tower built \uD83D\uDC80");
    }
}
