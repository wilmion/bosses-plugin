package com.wilmion.bossesplugin.generation;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.enums.StructureEnum;
import com.wilmion.bossesplugin.utils.AreaUtils;
import com.wilmion.bossesplugin.utils.PluginUtils;
import com.wilmion.bossesplugin.utils.WorldUtils;

import org.bukkit.Location;

import java.util.Optional;
import java.util.logging.Logger;

public class JordiStructureGeneration {
    private String structure = StructureEnum.JORDI_TOWER.getDescription();
    private BuildCommand buildCommand;
    private Integer cleanRange = 120;
    private String aperture = "¡Hay algo extraño y putrefacto! \uD83E\uDDDF, está cerca de:";

    public JordiStructureGeneration() {
        Optional<Location> jordiLocationBuilt = StructureGeneration.getLocationStructureWhenIsBuilt(structure);

        this.buildCommand = new BuildCommand();

        if(jordiLocationBuilt.isEmpty()) generate(PluginUtils.getPlugin().getLogger());
        else StructureGeneration.addTextLocationToShow(aperture, jordiLocationBuilt.get());
    }

    private void generate(Logger logger) {
        logger.info("Building Jordi Tower...");

        StructureGeneration.posGeneration = new Integer[]{-800, -400, -600, -200};

        Location location = StructureGeneration.getLocationStructure();

        WorldUtils.cleanInRange(location.clone(), cleanRange,  cleanRange);
        AreaUtils.createSpiral(location, cleanRange);
        StructureGeneration.generateBuild(location, structure, buildCommand);
        StructureGeneration.addTextLocationToShow(aperture, location);

        logger.info("Jordi Tower built \uD83E\uDDDF");
    }

}
