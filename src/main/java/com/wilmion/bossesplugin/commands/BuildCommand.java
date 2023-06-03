package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.mobsDificulties.special.SpecialEntity;
import com.wilmion.bossesplugin.models.metadata.BlockMetadata;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileModel;
import com.wilmion.bossesplugin.utils.*;
import com.wilmion.bossesplugin.utils.entities.ArmorStandUtils;
import com.wilmion.bossesplugin.utils.entities.FrameUtils;

import com.google.common.reflect.TypeToken;

import com.wilmion.bossesplugin.utils.material.EnchantmentUtils;
import com.wilmion.bossesplugin.utils.material.EquipmentUtils;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class BuildCommand {
    public static String path = "plugins/bosses-plugin-data/buildings/";

    private Plugin plugin;

    private List<String> buildingsNames;

    private Type buildType = new TypeToken<Map<String, List<BuildFileModel>>>() {}.getType();

    public BuildCommand() {
        this.plugin = PluginUtils.getPlugin();
        Resources.createDirsIfNotExist(path);
        this.reloadBuildingsNames();
    }

    public void reloadBuildingsNames() {
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
            Double[] alters = ConstructionUtils.getAlters(info.getL());

            setRotate(rotate, alters);

            BlockData blockData = Bukkit.createBlockData(info.getB());
            Location loc = location.clone();

            loc.add(alters[0], alters[1], alters[2]);
            loc.getBlock().setBlockData(blockData);

            if(loc.getBlock().getType().toString().equals("CHEST")) setChestContent(loc.getBlock());

            buildEntities(info, location);
            setMetadataAndSpawnBosses(info, loc);
            setMetadataAndSpawnSpecialEntities(loc, info);
        });
    }
    private void setMetadataAndSpawnBosses(BuildFileDataModel info, Location loc) {
        if(info.getBS().isEmpty()) return;

        BlockMetadata.upsertBlockMetadata(loc.getBlock(), "bossSpawn", info.getBS().get());
        SpawnBossCommand.spawnBoss(info.getBS().get(), loc);
    }

    private void setMetadataAndSpawnSpecialEntities(Location loc, BuildFileDataModel info) {
        if(info.getES().isEmpty() || info.getQS().isEmpty()) return;

        BlockMetadata.upsertBlockMetadata(loc.getBlock(),"entitySpawn", info.getES().get());
        BlockMetadata.upsertBlockMetadata(loc.getBlock(), "quantitySpawn", info.getQS().get());

        for (int i = 0; i < Integer.parseInt(info.getQS().get()); i++) new SpecialEntity(loc, info.getES().get());
    }

    private void setRotate(String rotate, Double[] alters) {
        if (rotate.equals("180deg")) alters[0] = alters[0] * -1;
        if (rotate.equals("90deg")) {
            double newX = alters[2];
            double newZ = alters[0];

            alters[0] = newX;
            alters[2] = newZ;
        }
        if (rotate.equals("270deg")) {
            double newX = alters[2];
            double newZ = alters[0] * -1;

            alters[0] = newX;
            alters[2] = newZ;
        }
    }

    private void setChestContent(Block block) {
        Chest chest = (Chest) block.getState();
        Random random = new Random();

        Map<String, Object> data = Resources.getJsonByData("chest-rewards.json", Map.class);
        List<String> itemsEndWith = (List<String>) data.get("items");
        List<String> itemsToEnchant = (List<String>) data.get("items_to_enchant");
        
        Predicate<Material> filter = material -> itemsEndWith.stream().anyMatch(m -> material.name().endsWith(m) || material.isEdible()) ;
        List<Material> items = Arrays.stream(Material.values()).filter(filter).collect(Collectors.toList());

        for (int i = 0; i < chest.getInventory().getSize(); i++) {
            if(RandomUtils.getRandomInPercentage() > 30) continue;

            ItemStack item;
            Integer index = random.nextInt(items.size());
            Material material = items.get(index);
            Integer quantity = random.nextInt(material.getMaxStackSize()) + 1;
            Boolean isEnchantment = itemsToEnchant.stream().anyMatch(e -> material.name().endsWith(e));


            if(material.name().equals("ENCHANTED_BOOK")) item = EnchantmentUtils.getRandomEnchantmentBook();
            else if(material.name().equals("ENCHANTED_GOLDEN_APPLE")) continue;
            else item = new ItemStack(material, quantity);
            
            if(isEnchantment && random.nextBoolean()) item = EquipmentUtils.enchantmentToItemStack(item);

            chest.getInventory().setItem(i, item);
        }
    }

    private void buildEntities(BuildFileDataModel dataModel, Location playerLoc) {
        Optional<String> dataEntities = dataModel.getED();

        if(dataEntities.isEmpty()) return;

        ArmorStandUtils.spawnArmorStand(dataEntities.get(), playerLoc);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> FrameUtils.spawnItemFrame(dataEntities.get(), playerLoc), 40);
    }
}
