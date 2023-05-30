package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.metadata.BlockMetadata;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileModel;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;
import com.wilmion.bossesplugin.utils.entities.ArmorStandUtils;
import com.wilmion.bossesplugin.utils.entities.FrameUtils;

import lombok.AllArgsConstructor;

import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SaveCommand {
    private static List<String> savedEntitiesUUID = new ArrayList<>();

    private Plugin plugin;

    public Boolean handleCommand(Player player, String[] args) {
        if(args.length < 5) return false;

        Location loc = player.getLocation().getBlock().getLocation().clone();
        BuildFileModel obj = new BuildFileModel();
        ArrayList<BuildFileDataModel> buildData = new ArrayList<>();
        String path = "plugins/bosses-plugin-data/buildings/" + args[1] + ".json";

        savedEntitiesUUID = new ArrayList<>();

        ActionRangeBlocks actionRangeBlocks = (location) -> {
            BuildFileDataModel data = new BuildFileDataModel();
            Block block = location.getBlock();

            getStaticEntities(location, data, loc);

            Optional<String> entitySpawn = BlockMetadata.getBlockMetadata(block ,"entitySpawn");
            Optional<String> quantitySpawn = BlockMetadata.getBlockMetadata(block ,"quantitySpawn");
            Optional<String> bossSpawn = BlockMetadata.getBlockMetadata(block ,"bossSpawn");
            Boolean hasMetadata = entitySpawn.isPresent() || quantitySpawn.isPresent() || bossSpawn.isPresent() || data.getEntityData().isPresent();

            data.setAlterX(location.getX() - loc.getX());
            data.setAlterY(location.getY() - loc.getY());
            data.setAlterZ(location.getZ() - loc.getZ());
            data.setBlockData(block.getBlockData().getAsString());

            if(entitySpawn.isPresent()) data.setEntitySpawn(Optional.of(entitySpawn.get()));
            if(quantitySpawn.isPresent()) data.setQuantitySpawn(Optional.of(quantitySpawn.get()));
            if(bossSpawn.isPresent()) data.setBossSpawn(Optional.of(bossSpawn.get()));

            if(!block.getType().isEmpty() || hasMetadata) buildData.add(data);
            return true;
        };

        Utils.executeActionInXOfBlocks(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), loc, actionRangeBlocks);

        obj.setData(buildData);
        Resources.writeFile(path, obj);

        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "Build saved."));

        return true;
    }

    private void getStaticEntities(Location location, BuildFileDataModel dataModel, Location playerLoc) {
        Collection<ArmorStand> armorStands = location.getNearbyEntitiesByType(ArmorStand.class, 1);
        Collection<ItemFrame> itemsFrames = location.getNearbyEntitiesByType(ItemFrame.class, 1);

        for(ArmorStand armorStand: armorStands) {
            Boolean isExist = savedEntitiesUUID.stream().anyMatch(e -> e.equals(armorStand.getUniqueId().toString()));

            if(isExist) continue;

            ArmorStandUtils armorStandUtils = new ArmorStandUtils(armorStand);
            String data = armorStandUtils.saveArmorStand(playerLoc);

            savedEntitiesUUID.add(armorStand.getUniqueId().toString());
            dataModel.setEntityData(Optional.of(data));
            return;
        }

        for (ItemFrame itemFrame: itemsFrames) {
            Boolean isExist = savedEntitiesUUID.stream().anyMatch(e -> e.equals(itemFrame.getUniqueId().toString()));

            if(isExist) continue;

            String data = FrameUtils.saveFrame(itemFrame, playerLoc);

            savedEntitiesUUID.add(itemFrame.getUniqueId().toString());
            dataModel.setEntityData(Optional.of(data));
            return;
        }
    }
}
