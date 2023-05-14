package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.bossesplugin.mobsDificulties.special.SpecialEntity;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;

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

        BuildFileModel obj = Resources.getJsonByLocalData(path + buildName + ".json", BuildFileModel.class);

        obj.getData().forEach((info) -> {
            setRotate(rotate, info);

            BlockData blockData = Bukkit.createBlockData(info.getBlockData());
            Location loc = player.getLocation().clone();

            loc.add(info.getAlterX(), info.getAlterY(), info.getAlterZ());
            loc.getBlock().setType(Material.getMaterial(info.getMaterialType()));
            loc.getBlock().setBlockData(blockData);

            if(info.getMaterialType().equals("CHEST")) setChestContent(loc.getBlock());

            setMetadataAndSpawnBosses(info, loc, player);
            setMetadataAndSpawnSpecialEntities(loc, info);
        });

        player.sendMessage(ChatColor.DARK_GREEN + buildName + " built!");

        return true;
    }
    private void setMetadataAndSpawnBosses(BuildFileDataModel info, Location loc, Player player) {
        if(info.getBossSpawn().isEmpty()) return;

        Utils.setMetadataValue("bossSpawn", info.getBossSpawn().get(), loc.getBlock().getState(), plugin);

        SpawnBossCommand.spawnBoss(info.getBossSpawn().get(), player, loc, plugin);
    }

    private void setMetadataAndSpawnSpecialEntities(Location loc, BuildFileDataModel info) {
        if(info.getEntitySpawn().isEmpty() || info.getQuantitySpawn().isEmpty()) return;

        Utils.setMetadataValue("entitySpawn", info.getEntitySpawn().get(), loc.getBlock().getState(), plugin);
        Utils.setMetadataValue("quantitySpawn", info.getQuantitySpawn().get(), loc.getBlock().getState(), plugin);

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
}
