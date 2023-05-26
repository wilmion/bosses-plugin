package com.wilmion.bossesplugin.models.metadata;

import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.objects.metadata.BossMetadataModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.WorldUtils;

import com.google.common.reflect.TypeToken;

import lombok.SneakyThrows;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class BossesMetadata {
    public static Map<String, BoosesModel> bosses = new TreeMap<>();
    private static String path = "plugins/bosses-plugin-data/game-data/bosses.json";
    public static Plugin plugin;

    public BossesMetadata(Plugin pl) {
        plugin = pl;
    }

    public static void loadChunk(ChunkLoadEvent event) {
        Chunk loadedChunk = event.getChunk();
        Entity[] entities = loadedChunk.getEntities();

        for (Entity entity : entities) loadBoss(String.valueOf(entity.getUniqueId()));
    }

    public static <T extends BoosesModel> Optional<T> getBoss(String entityUUID) {
        BoosesModel boss = bosses.get(entityUUID);

        if(boss != null) return Optional.of((T) boss);

        Optional<BoosesModel> bossLoaded = loadBoss(entityUUID);

        return bossLoaded.isEmpty() ? Optional.empty() : Optional.of((T) bossLoaded.get());
    }

    public static Optional<BoosesModel> loadBoss(String entityUUID) {
        Map<String, BossMetadataModel> internalData = fetchData();
        BossMetadataModel dataBoss = internalData.get(entityUUID);

        if(dataBoss == null) return Optional.empty();

        BoosesModel boss = renderBoss(dataBoss, entityUUID);

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
            Location loc = boss.entity.getLocation().clone();

            if(boss.entity == null || !boss.entity.isValid()) continue;

            bossMetadataModel.setWorldId(loc.getWorld().getUID().toString());
            bossMetadataModel.setX(loc.getX());
            bossMetadataModel.setY(loc.getY());
            bossMetadataModel.setZ(loc.getZ());
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
    private static BoosesModel renderBoss(BossMetadataModel bossMetadataModel, String uniqueId) {
        Location location = WorldUtils.getLocationByData(bossMetadataModel, plugin);
        Entity entity = WorldUtils.getEntityByLocation(location, uniqueId);

        Class<? extends BoosesModel> ClassEntity = (Class<? extends BoosesModel>) Class.forName(bossMetadataModel.getNameOfClass());
        BoosesModel boss = Resources.gson.fromJson(bossMetadataModel.getClassData(), ClassEntity);

        boss.entity = (LivingEntity) entity;
        boss.world = location.getWorld();
        boss.plugin = plugin;
        boss.server = plugin.getServer();

        if(entity != null) boss.useSchedulerEvents();

        return boss;
    }
}
