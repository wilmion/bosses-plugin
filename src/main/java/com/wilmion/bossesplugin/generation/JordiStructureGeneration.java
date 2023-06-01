package com.wilmion.bossesplugin.generation;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.utils.AreaUtils;
import com.wilmion.bossesplugin.utils.WorldUtils;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.logging.Logger;

public class JordiStructureGeneration {
    private BuildCommand buildCommand;
    private Integer cleanRange = 120;
    private String aperture = "¡Hay algo extraño y putrefacto! \uD83E\uDDDF, está cerca de:";

    public JordiStructureGeneration(Plugin plugin) {
        Optional<Location> jordiLocationBuilt = StructureGeneration.getLocationStructureWhenIsBuilt("jordi_tower");

        this.buildCommand = new BuildCommand(plugin);

        if(jordiLocationBuilt.isEmpty()) generate(plugin.getLogger());
        else StructureGeneration.addTextLocationToShow(aperture, jordiLocationBuilt.get());
    }

    private void generate(Logger logger) {
        logger.info("Building Jordi Tower...");

        StructureGeneration.posGeneration = new Integer[]{-800, -400, -600, -200};

        Location location = StructureGeneration.getLocationStructure();

        WorldUtils.cleanInRange(location.clone(), cleanRange,  cleanRange);
        AreaUtils.createSpiral(location, cleanRange);
        StructureGeneration.generateBuild(location, "jordi_tower", buildCommand);
        StructureGeneration.addTextLocationToShow(aperture, location);

        logger.info("Jordi Tower built \uD83E\uDDDF");
    }

}
