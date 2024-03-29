package com.wilmion.bossesplugin.models.metadata;

import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.objects.metadata.BossMetadataModel;
import com.wilmion.bossesplugin.utils.PluginUtils;
import com.wilmion.bossesplugin.utils.Resources;

import com.google.common.reflect.TypeToken;

import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class BossesMetadata {
    public static Map<String, BoosesModel> bosses = new TreeMap<>();
    private static String path = "plugins/bosses-plugin-data/game-data/bosses.json";

    private static Plugin plugin = PluginUtils.getPlugin();

    public static <T extends BoosesModel> Optional<T> getBoss(String entityUUID) {
        BoosesModel boss = bosses.get(entityUUID);

        if(boss != null) return Optional.of((T) boss);

        return Optional.empty();
    }

    public static Optional<BoosesModel> loadBoss(LivingEntity entity, BossDataModel bossDataModel) {
        String entityUUID = entity.getUniqueId().toString();
        BoosesModel boosesModel = bosses.get(entityUUID);

        if(boosesModel != null) return Optional.of(boosesModel);

        Map<String, BossMetadataModel> internalData = fetchData();
        BossMetadataModel dataBoss = internalData.get(entityUUID);

        if(dataBoss == null) return Optional.empty();

        BoosesModel boss = renderBoss(dataBoss, entity);

        if(boss.entity == null) return Optional.empty();

        upsertBoss(entityUUID, boss);

        return Optional.ofNullable(bosses.get(entityUUID));
    }

    public static <T extends BoosesModel> void upsertBoss(String entityUUID, T boss) {
        bosses.put(entityUUID, boss);
    }

    public static void deleteBoss(String entityUUID) {
        Map<String, BossMetadataModel> internalData = fetchData();

        internalData.remove(entityUUID);
        bosses.remove(entityUUID);

        Resources.writeFile(path, internalData);
    }

    public static void saveData() {
        Map<String, BossMetadataModel> internalData = fetchData();

        for(var entry: bosses.entrySet()) {
            BoosesModel boss = entry.getValue();
            BossMetadataModel bossMetadataModel = new BossMetadataModel();

            bossMetadataModel.setNameOfClass(boss.getClass().getName());
            bossMetadataModel.setClassData(Resources.gson.toJson(boss));

            internalData.put(entry.getKey(), bossMetadataModel);
        }

        Resources.writeFile(path, internalData);
    }

    private static Map<String, BossMetadataModel> fetchData() {
        Type bossType = new TypeToken<Map<String, BossMetadataModel>>() {}.getType();
        Map<String, BossMetadataModel> data = Resources.getJsonByLocalData(path, bossType);

        return data == null? new TreeMap<>() : data;
    }

    @SneakyThrows
    private static BoosesModel renderBoss(BossMetadataModel bossMetadataModel, LivingEntity entity) {
        Class<? extends BoosesModel> ClassEntity = (Class<? extends BoosesModel>) Class.forName(bossMetadataModel.getNameOfClass());
        BoosesModel boss = Resources.gson.fromJson(bossMetadataModel.getClassData(), ClassEntity);

        boss.entity = entity;
        boss.world = entity.getLocation().getWorld();
        boss.plugin = plugin;
        boss.server = plugin.getServer();

        boss.useSchedulerEvents();

        return boss;
    }
}
