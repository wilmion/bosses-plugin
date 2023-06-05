package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.enums.BossEnum;
import com.wilmion.bossesplugin.models.metadata.BlockMetadata;
import com.wilmion.bossesplugin.utils.AreaUtils;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.WorldUtils;

import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MetadataBlockCommand {
    public List<String> specialEntitiesName;
    private List<String> bossesNames = BossEnum.getKeys();

    private String helpMtdShow = "/bsspl metadata-block show <Duration = 5> -> Show metadata in 20x20x20 range with duration\n";
    private String helpMtdEntitySpawn = "/bsspl metadata-block spawn-entity <NAME> <Quantity> -> Set metadata on current block of spawn entity\n";
    private String helpMtdBossSpawn = "/bsspl metadata-block boss <NAME> -> Set metadata on current block of spawn boss\n";
    private String helpMtdDelete = "/bsspl metadata-block delete -> Delete all metadata on the block in your current position\n";

    public MetadataBlockCommand() {
        Map<String, Object> file = Resources.getJsonByData("special-entities.json", Map.class);
        List<Map<String, Object>> entities = (List<Map<String, Object>>) file.get("entities");

        this.specialEntitiesName = entities.stream().map(entity -> (String) entity.get("key")).collect(Collectors.toList());
    }

    public List<String> handleCommand(Player player, String[] args) {
        if(args.length < 2) return Arrays.asList(helpMtdEntitySpawn, helpMtdBossSpawn, helpMtdShow, helpMtdDelete);

        if(args[1].equals("spawn-entity")) return spawnEntityMetadata(player, args);
        if(args[1].equals("boss")) return bossEntityMetadata(player, args);
        if(args[1].equals("show")) return showMetadataPosition(player, args);
        if(args[1].equals("delete")) return deleteMetadataPosition(player);

        return Arrays.asList(helpMtdEntitySpawn, helpMtdBossSpawn, helpMtdShow, helpMtdDelete);
    }

    private List<String> spawnEntityMetadata(Player player, String[] args) {
        if(args.length < 4 || !specialEntitiesName.stream().anyMatch(args[2]::equals)) {
            ArrayList<String> listHelp = new ArrayList<>();

            listHelp.add(helpMtdEntitySpawn + "\nNAME can be:\n");
            specialEntitiesName.forEach(name -> listHelp.add("\n" + name));

            return listHelp;
        }

        Block block = player.getLocation().clone().getBlock();

        BlockMetadata.upsertBlockMetadata(block, "entitySpawn", args[2]);
        BlockMetadata.upsertBlockMetadata(block,"quantitySpawn", args[3]);

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

        BlockMetadata.upsertBlockMetadata(block, "bossSpawn", args[2]);
        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Boss metadata set."));

        return null;
    }

    private List<String> showMetadataPosition(Player player, String[] args) {
        Integer duration = args.length >= 3? Integer.parseInt(args[2]) : 5;

        Consumer<Block> callback = (block) -> {
            String result = "";

            Optional<String> entitySpawn = BlockMetadata.getBlockMetadata(block, "entitySpawn");
            Optional<String> quantitySpawn = BlockMetadata.getBlockMetadata(block, "quantitySpawn");
            Optional<String> bossSpawn = BlockMetadata.getBlockMetadata(block, "bossSpawn");

            if(entitySpawn.isPresent()) result += "E-S : " + entitySpawn.get() + " | ";
            if(quantitySpawn.isPresent()) result += "Q-S : " + quantitySpawn.get() + " | ";
            if(bossSpawn.isPresent()) result += "B-S : " + bossSpawn.get();

            if(!result.equals("")) WorldUtils.displayFloatingTextByXSeconds(block.getLocation().clone(), result, duration);
        };

        AreaUtils.executeActionIn3DRange(player.getLocation().clone(), 20, callback);
        player.sendMessage(ChatColor.DARK_GREEN + "Nearly metadata Showed!.");

        return null;
    }

    private List<String> deleteMetadataPosition(Player player) {
        BlockMetadata.deleteBlock(player.getLocation().getBlock());

        player.sendMessage(ChatColor.DARK_GREEN + "Metadata cleaned!");

        return null;
    }

}
