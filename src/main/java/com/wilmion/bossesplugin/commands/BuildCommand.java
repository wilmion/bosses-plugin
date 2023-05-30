package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.mobsDificulties.special.SpecialEntity;
import com.wilmion.bossesplugin.models.metadata.BlockMetadata;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;
import com.wilmion.bossesplugin.utils.entities.ArmorStandUtils;
import com.wilmion.bossesplugin.utils.entities.FrameUtils;

import com.google.common.reflect.TypeToken;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
public class BuildCommand {
    private String path = "plugins/bosses-plugin-data/buildings/";

    private Plugin plugin;

    private List<String> buildingsNames;

    private Type buildType = new TypeToken<Map<String, List<BuildFileModel>>>() {}.getType();

    public BuildCommand(Plugin plugin) {
        this.plugin = plugin;
        this.reloadBuildingsNames();
    }

    private void reloadBuildingsNames() {
        List<String> files = Resources.getJsonFilesInDirectory(path);

        this.buildingsNames = files.stream().map(file -> file.replace(".json", "")).collect(Collectors.toList());
    }

    public boolean handleCommand(Player player, String[] args) {
        this.reloadBuildingsNames();

        if(args.length < 2) return false;

        String buildName = args[1];
        String rotate = args.length > 2 ? args[2] : "0deg";

        if(!buildingsNames.stream().anyMatch(buildName::equals)) return false;

        buildStructure(player.getLocation().getBlock().getLocation(), buildName, rotate);
        player.sendMessage(ChatColor.DARK_GREEN + buildName + " built!");

        return true;
    }

    public void buildStructure(Location location, String buildName, String rotate) {
        BuildFileModel obj = Resources.getJsonByLocalData(path + buildName + ".json", BuildFileModel.class);

        obj.getData().forEach((info) -> {
            setRotate(rotate, info);

            BlockData blockData = Bukkit.createBlockData(info.getBlockData());
            Location loc = location.clone();

            loc.add(info.getAlterX(), info.getAlterY(), info.getAlterZ());
            loc.getBlock().setBlockData(blockData);

            if(loc.getBlock().getType().toString().equals("CHEST")) setChestContent(loc.getBlock());

            buildEntities(info, loc, location);
            setMetadataAndSpawnBosses(info, loc);
            setMetadataAndSpawnSpecialEntities(loc, info);
        });
    }
    private void setMetadataAndSpawnBosses(BuildFileDataModel info, Location loc) {
        if(info.getBossSpawn().isEmpty()) return;

        BlockMetadata.upsertBlockMetadata(loc.getBlock(), "bossSpawn", info.getBossSpawn().get());

        SpawnBossCommand.spawnBoss(info.getBossSpawn().get(), loc, plugin);
    }

    private void setMetadataAndSpawnSpecialEntities(Location loc, BuildFileDataModel info) {
        if(info.getEntitySpawn().isEmpty() || info.getQuantitySpawn().isEmpty()) return;

        BlockMetadata.upsertBlockMetadata(loc.getBlock(),"entitySpawn", info.getEntitySpawn().get());
        BlockMetadata.upsertBlockMetadata(loc.getBlock(), "quantitySpawn", info.getQuantitySpawn().get());

        for (int i = 0; i < Integer.parseInt(info.getQuantitySpawn().get()); i++) new SpecialEntity(loc, info.getEntitySpawn().get());
    }

    private void setRotate(String rotate, BuildFileDataModel info) {
        if (rotate.equals("180deg")) info.setAlterX(info.getAlterX() * -1);
        if (rotate.equals("90deg")) {
            double newX = info.getAlterZ();
            double newZ = info.getAlterX();

            info.setAlterX(newX);
            info.setAlterZ(newZ);
        }
        if (rotate.equals("270deg")) {
            double newX = info.getAlterZ();
            double newZ = info.getAlterX() * -1;

            info.setAlterX(newX);
            info.setAlterZ(newZ);
        }
    }

    private void setChestContent(Block block) {
        Chest chest = (Chest) block.getState();

        Map<String, Object> data = Resources.getJsonByData("chest-rewards.json", Map.class);
        List<String> items = (List<String>) data.get("items");
        Random random = new Random();

        for (int i = 0; i < chest.getInventory().getSize(); i++) {
            if(Utils.getRandomInPercentage() > 10) continue;

            Integer index = random.nextInt(items.size());

            chest.getInventory().setItem(i, new ItemStack(Material.valueOf(items.get(index))));
        }
    }

    private void buildEntities(BuildFileDataModel dataModel, Location location, Location playerLoc) {
        Optional<String> dataEntities = dataModel.getEntityData();

        if(dataEntities.isEmpty()) return;

        ArmorStandUtils.spawnArmorStand(dataEntities.get(), playerLoc);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> FrameUtils.spawnItemFrame(dataEntities.get(), playerLoc), 40);
    }
}
