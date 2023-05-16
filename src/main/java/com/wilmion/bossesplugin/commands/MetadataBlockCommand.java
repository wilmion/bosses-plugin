package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;

import com.wilmion.bossesplugin.utils.WorldUtils;
import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MetadataBlockCommand {
    private Plugin plugin;
    private List<String> specialEntitiesName;
    private List<String> bossesNames;

    private String helpMtdShow = "/bsspl metadata-block show <Duration = 5> -> Show metadata in 20x20x20 range with duration\n";
    private String helpMtdEntitySpawn = "/bsspl metadata-block spawn-entity <NAME> <Quantity> -> Set metadata on current block of spawn entity\n";
    private String helpMtdBossSpawn = "/bsspl metadata-block boss <NAME> -> Set metadata on current block of spawn boss\n";

    public MetadataBlockCommand(Plugin plugin) {
        Map<String, Object> file = Resources.getJsonByData("special-entities.json", Map.class);
        Map<String, Object> bossesFile = Resources.getJsonByData("commands-boss.json", Map.class);
        List<Map<String, Object>> entities = (List<Map<String, Object>>) file.get("entities");

        this.specialEntitiesName = entities.stream().map(entity -> (String) entity.get("key")).collect(Collectors.toList());
        this.plugin = plugin;
        this.bossesNames = (List<String>) bossesFile.get("bosses");
    }

    public List<String> handleCommand(Player player, String[] args) {
        if(args.length < 2) return Arrays.asList(helpMtdEntitySpawn, helpMtdBossSpawn, helpMtdShow);

        if(args[1].equals("spawn-entity")) return spawnEntityMetadata(player, args);
        if(args[1].equals("boss")) return bossEntityMetadata(player, args);
        if(args[1].equals("show")) return showMetadataPosition(player, args);

        return Arrays.asList(helpMtdEntitySpawn, helpMtdBossSpawn, helpMtdShow);
    }

    private List<String> spawnEntityMetadata(Player player, String[] args) {
        if(args.length < 4 && !specialEntitiesName.stream().anyMatch(args[2]::equals)) {
            ArrayList<String> listHelp = new ArrayList<>();

            listHelp.add(helpMtdEntitySpawn + "\nNAME can be:\n");
            specialEntitiesName.forEach(name -> listHelp.add("\n" + name));

            return listHelp;
        }

        Block block = player.getLocation().clone().getBlock();

        Utils.setMetadataValue("entitySpawn", args[2], block.getState(), plugin);
        Utils.setMetadataValue("quantitySpawn", args[3], block.getState(), plugin);

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Spawn-Entity Metadata set."));

        return null;
    }

    private List<String> bossEntityMetadata(Player player, String[] args) {
        if(args.length < 3 || !bossesNames.stream().anyMatch(args[2]::equals)) {
            ArrayList<String> listHelp = new ArrayList<>();
            listHelp.add(helpMtdBossSpawn + "\nNAME-OF-BOSS can be:\n");

            bossesNames.forEach(bossName -> listHelp.add("\n" + bossName));

            return listHelp;
        }

        Block block = player.getLocation().clone().getBlock();

        Utils.setMetadataValue("bossSpawn", args[2], block.getState(), plugin);

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Boss metadata set."));

        return null;
    }

    private List<String> showMetadataPosition(Player player, String[] args) {
        Integer duration = args.length >= 3? Integer.parseInt(args[2]) : 5;

        Consumer<Block> callback = (block) -> {
            String result = "";

            Optional<MetadataValue> entitySpawn = Utils.getMetadataValue("entitySpawn", block.getState());
            Optional<MetadataValue> quantitySpawn = Utils.getMetadataValue("quantitySpawn", block.getState());
            Optional<MetadataValue> bossSpawn = Utils.getMetadataValue("bossSpawn", block.getState());

            if(entitySpawn.isPresent()) result += "E-S : " + entitySpawn.get().asString() + " | ";
            if(quantitySpawn.isPresent()) result += "Q-S : " + quantitySpawn.get().asString() + " | ";
            if(bossSpawn.isPresent()) result += "B-S : " + bossSpawn.get().asString();

            if(!result.equals("")) WorldUtils.displayFloatingTextByXSeconds(block.getLocation().clone(), result, duration, plugin);
        };

        Utils.executeActionIn3DRange(player.getLocation().clone(), 20, callback);

        return null;
    }

}
