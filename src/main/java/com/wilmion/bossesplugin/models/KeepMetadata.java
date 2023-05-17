package com.wilmion.bossesplugin.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import com.wilmion.bossesplugin.objects.LocationDataModel;
import com.wilmion.bossesplugin.objects.metadata.BossMetadataModel;
import com.wilmion.bossesplugin.objects.metadata.GlobalMetadataModel;
import com.wilmion.bossesplugin.utils.Resources;

import com.wilmion.bossesplugin.utils.WorldUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class KeepMetadata {
    public static ArrayList<Map<String, Object>> entitiesWithMetadata = new ArrayList<>();
    public static ArrayList<Map<String, Object>> blocksWithMetadata = new ArrayList<>();
    private static String pathBoss = "plugins/bosses-plugin-data/game-data-bosses.json";
    private static String pathEntities = "plugins/bosses-plugin-data/game-data-entities.json";

    private Plugin plugin;

    public void save() {
        saveBosses();
        saveEntities();
    }

    private void saveBosses() {
        Gson gson = new Gson();
        Map<String, BossMetadataModel> file = new TreeMap<>();

        for(var entry: BoosesModel.bosses.entrySet()) {
            var boss = entry.getValue();

            BossMetadataModel bossMetadataModel = new BossMetadataModel();
            setLocationOnObject(bossMetadataModel, boss.entity.getLocation().clone());
            bossMetadataModel.setNameOfClass(boss.getClass().getName());
            bossMetadataModel.setClassData(gson.toJson(boss));

            file.put(entry.getKey(), bossMetadataModel);
        }

        Resources.writeFile(pathBoss, file);
    }

    private void saveEntities() {
        Map<String, GlobalMetadataModel> file = new TreeMap<>();

        for(var entityData: entitiesWithMetadata) {
            Entity entity = (Entity) entityData.get("entity");

            if(entity.isDead()) continue;

            GlobalMetadataModel entityWithMetadata = new GlobalMetadataModel();
            ArrayList<Map<String, String>> mtd = new ArrayList();
            List<String> keys = (List<String>) entityData.get("metadata");

            keys.forEach(key -> {
                Map<String, String> obj = new TreeMap<>();
                String value = (String) entity.getMetadata(key).get(0).value();

                obj.put("key", key);
                obj.put("value", value);

                mtd.add(obj);
            });

            setLocationOnObject(entityWithMetadata, entity.getLocation().clone());
            entityWithMetadata.setMetadata(mtd);
            file.put(String.valueOf(entity.getUniqueId()), entityWithMetadata);
        }

        Resources.writeFile(pathEntities, file);
    }

    private void setLocationOnObject(LocationDataModel model, Location loc) {
        model.setWorldId(loc.getWorld().getUID().toString());
        model.setX(loc.getX());
        model.setY(loc.getY());
        model.setZ(loc.getZ());
    }

    public void keepMetadata() {
        keepBossMetadata();
        keepEntitiesMetadata();
    }

    @SneakyThrows
    private void keepBossMetadata() {
        Type type = new TypeToken<Map<String, BossMetadataModel>>() {}.getType();
        Gson gson = new Gson();
        Map<String, BossMetadataModel> file = Resources.getJsonByLocalData(pathBoss, type);

        if(file == null) return;

        for(var entry: file.entrySet()) {
            String uniqueId = entry.getKey();
            BossMetadataModel mtd = entry.getValue();
            Location location = WorldUtils.getLocationByData(mtd, plugin);
            Entity entity = WorldUtils.getEntityByLocation(location, uniqueId);

            if(entity == null) continue;

            Class<? extends BoosesModel> ClassEntity = (Class<? extends BoosesModel>) Class.forName(mtd.getNameOfClass());
            BoosesModel boss = gson.fromJson(mtd.getClassData(), ClassEntity);

            boss.entity = (LivingEntity) entity;
            boss.world = location.getWorld();
            boss.plugin = plugin;
            boss.server = plugin.getServer();

            boss.setMetadata();
            boss.useSchedulerEvents();

            BoosesModel.bosses.put(uniqueId, boss);
        }
    }

    @SneakyThrows
    private void keepEntitiesMetadata() {
        Type type = new TypeToken<Map<String, GlobalMetadataModel>>() {}.getType();
        Map<String, GlobalMetadataModel> file = Resources.getJsonByLocalData(pathEntities, type);

        if(file == null) return;

        for(var entry: file.entrySet()) {
            String uniqueId = entry.getKey();
            GlobalMetadataModel mtd = entry.getValue();
            Location location = WorldUtils.getLocationByData(mtd, plugin);
            Entity entity = WorldUtils.getEntityByLocation(location, uniqueId);

            if(entity == null) continue;

            List<String> keys = mtd.getMetadata().stream().map(d -> d.get("key")).collect(Collectors.toList());
            Long count = keys.stream().count();

            addEntityWithMetadata(entity, keys.toArray(new String[count.intValue()]));

            mtd.getMetadata().forEach(data -> {
                entity.setMetadata(data.get("key"), new FixedMetadataValue(plugin, data.get("value")));
            });
        }
    }

    public static void addEntityWithMetadata(Entity entity, String... mtd) {
        Map<String, Object> data = new TreeMap<>();

        data.put("entity", entity);
        data.put("metadata", Arrays.asList(mtd));

        entitiesWithMetadata.add(data);
    }
}
