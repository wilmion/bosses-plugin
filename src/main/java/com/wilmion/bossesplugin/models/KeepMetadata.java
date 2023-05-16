package com.wilmion.bossesplugin.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import com.wilmion.bossesplugin.objects.LocationDataModel;
import com.wilmion.bossesplugin.objects.metadata.BossMetadataModel;
import com.wilmion.bossesplugin.objects.metadata.GlobalMetadataModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;

import com.wilmion.bossesplugin.utils.WorldUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class KeepMetadata {
    public static ArrayList<Map<String, Object>> entitiesWithMetadata = new ArrayList<>();
    public static ArrayList<Map<String, Object>> blocksWithMetadata = new ArrayList<>();
    private static String pathBoss = "plugins/bosses-plugin-data/game-data-bosses.json";
    private static String pathEntities = "plugins/bosses-plugin-data/game-data-entities.json";
    private static String pathBlocks = "plugins/bosses-plugin-data/game-data-blocks.json";

    private Plugin plugin;

    public void save() {
        saveBosses();
        saveEntities();
        saveBlocks();
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

    private void saveBlocks() {
        Map<String, GlobalMetadataModel> file = new TreeMap<>();

        for(var blocksData: blocksWithMetadata) {
            Block block = (Block) blocksData.get("block");

            GlobalMetadataModel blockMetadata = new GlobalMetadataModel();
            ArrayList<Map<String, String>> mtd = new ArrayList();
            List<String> keys = (List<String>) blocksData.get("metadata");

            keys.forEach(key -> {
                Map<String, String> obj = new TreeMap<>();
                Optional<MetadataValue> mtdValue = Utils.getMetadataValue(key, block.getState());

                if(mtdValue.isEmpty()) return;

                obj.put("key", key);
                obj.put("value", mtdValue.get().asString());

                mtd.add(obj);
            });

            setLocationOnObject(blockMetadata, block.getLocation());
            blockMetadata.setMetadata(mtd);
            file.put(UUID.randomUUID().toString(), blockMetadata);
        }

        Resources.writeFile(pathBlocks, file);
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
        keepBlocksMetadata();
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

    @SneakyThrows
    private void keepBlocksMetadata() {
        Type type = new TypeToken<Map<String, GlobalMetadataModel>>() {}.getType();
        Map<String, GlobalMetadataModel> file = Resources.getJsonByLocalData(pathBlocks, type);

        if(file == null) return;

        for(var entry: file.entrySet()) {
            GlobalMetadataModel mtd = entry.getValue();
            Location location = WorldUtils.getLocationByData(mtd, plugin);

            for(Map<String, String> data: mtd.getMetadata()) {
                Utils.setMetadataValue(data.get("key"), data.get("value"), location.getBlock().getState() , plugin);
            }
        }
    }

    public static void addEntityWithMetadata(Entity entity, String... mtd) {
        Map<String, Object> data = new TreeMap<>();

        data.put("entity", entity);
        data.put("metadata", Arrays.asList(mtd));

        entitiesWithMetadata.add(data);
    }

    public static void addBlockWithMetadata(Block block, String key) {
        Map<String, Object> data = new TreeMap<>();
        Predicate<Map<String, Object>> filterFunc = (d) -> {
            Block dBlock = (Block) d.get("block");
            return dBlock.getLocation().equals(block.getLocation());
        };
        List<Map<String, Object>> exist = blocksWithMetadata.stream().filter(filterFunc).collect(Collectors.toList());

        if(exist.stream().count() == 0) {
            ArrayList<String> keys = new ArrayList<>();
            keys.add(key);

            data.put("block", block);
            data.put("metadata", keys);

            blocksWithMetadata.add(data);
            return;
        }

        Map<String, Object> blockData = exist.get(0);
        ArrayList<String> mtd = (ArrayList<String>) blockData.get("metadata");
        mtd.add(key);
    }


}
