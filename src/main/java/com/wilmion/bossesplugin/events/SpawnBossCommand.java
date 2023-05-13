package com.wilmion.bossesplugin.events;

import com.google.gson.Gson;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.mobsDificulties.boss.*;

import com.wilmion.bossesplugin.mobsDificulties.special.SpecialEntity;
import com.wilmion.bossesplugin.models.UserDataLevel;
import com.wilmion.bossesplugin.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SpawnBossCommand implements CommandExecutor {
    Plugin plugin;

    public SpawnBossCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        String subCommand = args[0];

        if(subCommand.equals("spawnboss")) return spawnBossCommand(player, args[1]);
        if(subCommand.equals("help")) return showHelp(player);
        if(subCommand.equals("build")) return build(player, args[1], args);
        if(subCommand.equals("save")) return saveBuild(player, args);
        if(subCommand.equals("blockEntityMtd")) return setBlockEntityMtd(player, args);
        if(subCommand.equals("blockBossMtd")) return setBlockBossMtd(player, args);

        return false;
    }

    private boolean setBlockEntityMtd(Player player, String[] args) {
        Block block = player.getLocation().clone().getBlock();

        Utils.setMetadataValue("entitySpawn", args[1], block.getState(), plugin);
        Utils.setMetadataValue("quantitySpawn", args[2], block.getState(), plugin);

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Entity Metadata set."));

        return true;
    }

    private boolean setBlockBossMtd(Player player, String[] args) {
        Block block = player.getLocation().clone().getBlock();

        Utils.setMetadataValue("bossSpawn", args[1], block.getState(), plugin);

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Boss metadata set."));

        return true;
    }

    private boolean saveBuild(Player player, String[] args) {
        Gson gson = new Gson();

        Location loc = player.getLocation();
        Map<String, Object> obj = new TreeMap<>();
        ArrayList<Map<String, Object>> buildData = new ArrayList<>();

        obj.put("name", args[1]);

        ActionRangeBlocks actionRangeBlocks = (location) -> {
            Map<String, Object> data = new TreeMap<>();
            Block block = location.getBlock();
            String blockName = block.getType().toString();
            String blockData = block.getBlockData().getAsString();

            Double x = location.getX() - loc.getX();
            Double y = location.getY() - loc.getY();
            Double z = location.getZ() - loc.getZ();

            Optional<MetadataValue> entitySpawn = Utils.getMetadataValue("entitySpawn", block.getState());
            Optional<MetadataValue> quantitySpawn = Utils.getMetadataValue("quantitySpawn", block.getState());
            Optional<MetadataValue> bossSpawn = Utils.getMetadataValue("bossSpawn", block.getState());

            data.put("materialType" , blockName);
            data.put("alterY" , y);
            data.put("alterZ" , z);
            data.put("alterX" , x);
            data.put("blockData", blockData);
            if(entitySpawn.isPresent()) data.put("entitySpawn", entitySpawn.get().asString());
            if(quantitySpawn.isPresent()) data.put("quantitySpawn", quantitySpawn.get().asString());
            if(bossSpawn.isPresent()) data.put("bossSpawn", bossSpawn.get().asString());

            buildData.add(data);
        };

        Utils.executeActionInXOfBlocks(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), loc, actionRangeBlocks);

        obj.put("data", buildData);

        String path = "plugins/bosses-plugin-data/buildings/" + args[1] + ".json";

        Utils.writeFile(path, gson.toJson(obj));

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Build saved"));

        return true;
    }

    private boolean build(Player player, String buildName, String[] args) {
        String rotate = args[2];
        Optional<String> flipZ = Optional.ofNullable(args.length > 3? args[3] : null);

        Gson gson = new Gson();
        final String path = "plugins/bosses-plugin-data/buildings/" + buildName + ".json";

        String data = Utils.readFile(path);

        if(data == null) return false;

        Map<String, Object> obj = gson.fromJson(data, Map.class);

        ArrayList<Map<String, Object>> buildData = (ArrayList<Map<String, Object>>) obj.get("data");

        buildData.forEach((mtd) -> {
            String blockName = (String) mtd.get("materialType");
            String blockDataStr = (String) mtd.get("blockData");
            Double x = (Double) mtd.get("alterX");
            Double y = (Double) mtd.get("alterY");
            Double z = (Double) mtd.get("alterZ");
            BlockData blockData = Bukkit.createBlockData(blockDataStr);
            Object entitySpawn = mtd.get("entitySpawn");
            Object quantitySpawn = mtd.get("quantitySpawn");
            Object bossSpawn = mtd.get("bossSpawn");

            if (rotate.equals("180deg")) x *= -1;
            if (rotate.equals("90deg")) {
                x = (Double) mtd.get("alterZ");
                z = (Double) mtd.get("alterX");
            }
            if (rotate.equals("270deg")) {
                x = (Double) mtd.get("alterZ");
                z = (Double) mtd.get("alterX");
                z *= -1;
            }
            if (flipZ.isPresent()) z *= -1;

            Location loc = player.getLocation().clone();
            loc.add(x, y, z);
            loc.getBlock().setType(Material.getMaterial(blockName));
            loc.getBlock().setBlockData(blockData);

            if(blockName.equals("CHEST")) setChestContent(loc.getBlock());

            if(bossSpawn != null && bossSpawn.equals("SUPPORT_ZOMBIE")) new SupportZombie(player, loc, plugin);

            if(entitySpawn == null || quantitySpawn == null) return;

            String quantity = (String) quantitySpawn;
            String entityName = (String) entitySpawn;

            Utils.setMetadataValue("entitySpawn", entityName, loc.getBlock().getState(), plugin);
            Utils.setMetadataValue("quantitySpawn", quantity, loc.getBlock().getState(), plugin);

            for (int i = 0; i < Integer.parseInt(quantity); i++) {
                new SpecialEntity(player.getWorld(), loc, entityName);
            }
        });

        player.sendMessage(ChatColor.DARK_GREEN + buildName + " built");

        return true;
    }

    private void setChestContent(Block block) {
        Chest chest = (Chest) block.getState();

        Gson gson = new Gson();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("chest-rewards.json");
        InputStreamReader reader = new InputStreamReader(inputStream);

        Map<String, Object> data =gson.fromJson(reader, Map.class);
        List<String> items = (List<String>) data.get("items");

        Random random = new Random();

        for (int i = 0; i < chest.getInventory().getSize(); i++) {
            Integer probability = Utils.getRandomInPercentage();

            if(probability > 10) continue;

            Integer index = random.nextInt(items.size());

            chest.getInventory().setItem(i, new ItemStack(Material.valueOf(items.get(index))));
        }

    }

    private boolean showHelp(Player player) {
        player.sendMessage(Component.text(ChatColor.LIGHT_PURPLE + "Help Information"));
        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "/bsspl spawnboss <name-of-boss> -> Generate a boss in your current location"));

        return true;
    }

    private boolean spawnBossCommand(Player player, String bossType) {
        String[] bosses = {"support-zombie", "master-skeleton", "soldier-spider", "queen-spider", "master-creeper", "master-wizard"};

        Location location = player.getLocation().clone();

        if(bossType.equals(bosses[0])) new SupportZombie(player, location, plugin);
        if(bossType.equals(bosses[1])) new MasterSkeleton(player, location, plugin);
        if(bossType.equals(bosses[2])) new SoldierSpider(player, location, plugin);
        if(bossType.equals(bosses[3])) new QueenSpider(player, location, plugin);
        if(bossType.equals(bosses[4])) new MasterCreeper(player, location, plugin);
        if(bossType.equals(bosses[5])) new MasterWizard(player, location, plugin);

        return Arrays.stream(bosses).anyMatch(bossType::equals);
    }
}
