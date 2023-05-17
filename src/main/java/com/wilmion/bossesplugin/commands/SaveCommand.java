package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;

import lombok.AllArgsConstructor;

import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Optional;

@AllArgsConstructor
public class SaveCommand {
    private Plugin plugin;

    public Boolean handleCommand(Player player, String[] args) {
        if(args.length < 5) return false;

        Location loc = player.getLocation().clone();
        BuildFileModel obj = new BuildFileModel();
        ArrayList<BuildFileDataModel> buildData = new ArrayList<>();
        String path = "plugins/bosses-plugin-data/buildings/" + args[1] + ".json";

        ActionRangeBlocks actionRangeBlocks = (location) -> {
            BuildFileDataModel data = new BuildFileDataModel();
            Block block = location.getBlock();

            Optional<MetadataValue> entitySpawn = Utils.getMetadataValue("entitySpawn", block.getState());
            Optional<MetadataValue> quantitySpawn = Utils.getMetadataValue("quantitySpawn", block.getState());
            Optional<MetadataValue> bossSpawn = Utils.getMetadataValue("bossSpawn", block.getState());
            Boolean hasMetadata = entitySpawn.isPresent() || quantitySpawn.isPresent() || bossSpawn.isPresent();

            data.setAlterX(location.getX() - loc.getX());
            data.setAlterY(location.getY() - loc.getY());
            data.setAlterZ(location.getZ() - loc.getZ());
            data.setBlockData(block.getBlockData().getAsString());

            if(entitySpawn.isPresent()) data.setEntitySpawn(Optional.of(entitySpawn.get().asString()));
            if(quantitySpawn.isPresent()) data.setQuantitySpawn(Optional.of(quantitySpawn.get().asString()));
            if(bossSpawn.isPresent()) data.setBossSpawn(Optional.of(bossSpawn.get().asString()));

            if(!block.getType().isEmpty() || hasMetadata) buildData.add(data);
            return true;
        };

        Utils.executeActionInXOfBlocks(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), loc, actionRangeBlocks);

        obj.setData(buildData);
        Resources.writeFile(path, obj);

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Build saved."));

        return true;
    }
}
