package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.metadata.BlockMetadata;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileDataModel;
import com.wilmion.bossesplugin.objects.buildFile.BuildFileModel;
import com.wilmion.bossesplugin.utils.ConstructionUtils;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;
import com.wilmion.bossesplugin.utils.entities.ArmorStandUtils;
import com.wilmion.bossesplugin.utils.entities.FrameUtils;

import lombok.AllArgsConstructor;

import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SaveCommand {
    private static List<String> savedEntitiesUUID = new ArrayList<>();

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
            Boolean hasMetadata = entitySpawn.isPresent() || quantitySpawn.isPresent() || bossSpawn.isPresent() || data.getED().isPresent();

            data.setL(ConstructionUtils.convertLocationToAlters(location, loc));
            data.setB(block.getBlockData().getAsString());

            if(entitySpawn.isPresent()) data.setES(Optional.of(entitySpawn.get()));
            if(quantitySpawn.isPresent()) data.setQS(Optional.of(quantitySpawn.get()));
            if(bossSpawn.isPresent()) data.setBS(Optional.of(bossSpawn.get()));

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
        Collection<Entity> nearbyEntities = location.getNearbyEntitiesByType(Entity.class, 1);

        for(Entity entity: nearbyEntities) {
            String UUID = entity.getUniqueId().toString();
            Boolean isExist = savedEntitiesUUID.stream().anyMatch(e -> e.equals(UUID));
            String name = entity.getType().name();
            Boolean isValidEntity = name.equals("ARMOR_STAND") || name.equals("ITEM_FRAME");

            if(isExist || !isValidEntity) continue;

            String data;

            if(name.equals("ARMOR_STAND")) data = ArmorStandUtils.saveArmorStand(entity, playerLoc);
            else data = FrameUtils.saveFrame(entity , playerLoc);

            dataModel.setED(Optional.of(data));
            savedEntitiesUUID.add(UUID);
        }
    }
}
