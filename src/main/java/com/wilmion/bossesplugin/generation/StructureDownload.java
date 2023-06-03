package com.wilmion.bossesplugin.generation;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.enums.StructureEnum;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.cloud.CloudUtils;
import com.wilmion.bossesplugin.utils.cloud.DownloadUtils;
import com.wilmion.bossesplugin.utils.PluginUtils;
import com.wilmion.bossesplugin.utils.cloud.ZipUtils;

import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;


public class StructureDownload {
    private final StructureEnum[] buildingsRequired = StructureEnum.values();
    private final String nameZipCompress = "v1.3 buildings.zip";
    private final String URIStructures = "https://firebasestorage.googleapis.com/v0/b/bosses-minecraft-plugin.appspot.com/o/build_versions%2Fv1.3%20buildings.zip?alt=media";
    private final Plugin plugin = PluginUtils.getPlugin();
    private Boolean valid = true;

    public StructureDownload() {
        Logger logger = plugin.getLogger();

        if(!hasBuildings()) recoverStructures(logger);
    }

    @SneakyThrows
    private void recoverStructures(Logger logger) {
        Boolean hasInternet = CloudUtils.isInternetAvailable();
        String pathToZip = BuildCommand.path + nameZipCompress;

        if(!hasInternet) {
            logger.warning("You haven't internet connection! Please connect to internet for the plugin works ⚠\uFE0F");
            valid = false;
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        logger.info("Downloading structures...");

        DownloadUtils.downloadFile(URIStructures, pathToZip);

        logger.info("Unzipping buildings...");

        ZipUtils.extractZip(pathToZip, BuildCommand.path);
        Resources.deleteFile(pathToZip);

        logger.info("Finish downloading bosses structures");
    }

    public Boolean getValid() {
        return valid;
    }



    private Boolean hasBuildings() {
        Boolean result = true;

        for(StructureEnum structure: buildingsRequired) {
            String filename = structure.getDescription();
            String path = BuildCommand.path + filename + ".json";
            File file = new File(path);
            Boolean isExist = file.exists();
            Boolean isExistInMc = StructureGeneration.getLocationStructureWhenIsBuilt(filename).isPresent();

            if(!isExistInMc && !isExist) result = false;
        }

        return result;
    }


}
