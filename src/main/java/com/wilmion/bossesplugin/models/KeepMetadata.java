package com.wilmion.bossesplugin.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wilmion.bossesplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.bossesplugin.objects.CommandDataModel;
import com.wilmion.bossesplugin.objects.metadata.BossMetadataModel;
import com.wilmion.bossesplugin.utils.Resources;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

@AllArgsConstructor
public class KeepMetadata {
    private static Boolean chargeMetadata = false;
    private static String pathBoss = "plugins/bosses-plugin-data/game-data-bosses.json";

    private Plugin plugin;

    public void saveBossMetadata(Map<String, ? extends BoosesModel> bosses) {
        Gson gson = new Gson();
        Map<String, BossMetadataModel> file = new TreeMap<>();

        for(var entry: bosses.entrySet()) {
            BossMetadataModel bossMetadataModel = new BossMetadataModel();
            bossMetadataModel.setNameOfClass(entry.getValue().getClass().getName());
            bossMetadataModel.setClassData(gson.toJson(entry.getValue()));

            file.put(entry.getKey(), bossMetadataModel);
        }

        Resources.writeFile(pathBoss, file);
    }

    @SneakyThrows
    public void keepMetadata() {
        if(chargeMetadata) return;
        chargeMetadata = true;

        keepBossMetadata();
    }

    @SneakyThrows
    public void keepBossMetadata() {
        Type type = new TypeToken<Map<String, BossMetadataModel>>() {}.getType();
        Gson gson = new Gson();
        Map<String, BossMetadataModel> file = Resources.getJsonByLocalData(pathBoss, type);

        if(file == null) return;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                String uniqueId = String.valueOf(entity.getUniqueId());
                BossMetadataModel mtd = file.get(uniqueId);

                if(mtd == null) continue;

                Class<? extends BoosesModel> ClassEntity = (Class<? extends BoosesModel>) Class.forName(mtd.getNameOfClass());
                BoosesModel boss = gson.fromJson(mtd.getClassData(), ClassEntity);

                boss.entity = (LivingEntity) entity;
                boss.world = world;
                boss.plugin = plugin;
                boss.server = plugin.getServer();

                boss.setMetadata();
                boss.useSchedulerEvents();

                BoosesModel.bosses.put(uniqueId, boss);
            }
        }
    }
}
