package com.wilmion.bossesplugin.models.metadata;

import com.google.common.reflect.TypeToken;

import com.wilmion.bossesplugin.objects.metadata.MetadataModel;
import com.wilmion.bossesplugin.objects.metadata.BlockMetadataModel;
import com.wilmion.bossesplugin.utils.Resources;

import org.bukkit.block.Block;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class BlockMetadata {
    private static String pathBlocks = "plugins/bosses-plugin-data/game-data/blocks.json";
    private static Map<String, BlockMetadataModel> blocksWithMetadata = new TreeMap<>();

    public static Optional<BlockMetadataModel> getBlockData(Block block) {
        String id = block.getLocation().toString();
        BlockMetadataModel blockMetadata = blocksWithMetadata.get(id);

        return Optional.ofNullable(blockMetadata);
    }

    public static List<MetadataModel> getAllBlockMetadata(Block block) {
        Optional<BlockMetadataModel> blockMetadata = getBlockData(block);

        if(blockMetadata.isEmpty()) return new ArrayList<>();
        return blockMetadata.get().getData();
    }

    public static Optional<String> getBlockMetadata(Block block, String key) {
        List<MetadataModel> data = getAllBlockMetadata(block);

        if(data.stream().count() == 0) return Optional.ofNullable(null);

        Optional<MetadataModel> dataFiltered = data.stream().filter(d -> d.getKey().equals(key)).findFirst();

        return dataFiltered.isEmpty() ? Optional.ofNullable(null) : Optional.of(dataFiltered.get().getValue());
    }

    public static void upsertBlockMetadata(Block block, String key, String value) {
        String id = block.getLocation().toString();
        Optional<BlockMetadataModel> blockData = getBlockData(block);
        List<MetadataModel> blockMetadata = new ArrayList<>();

        if(blockData.isPresent()) blockMetadata = blockData.get().getData();
        else blockData = Optional.of(new BlockMetadataModel());

        Optional<MetadataModel> currentValue = Optional.ofNullable(null);

        if(blockMetadata.stream().count() != 0) currentValue = blockMetadata.stream().filter(m -> m.getKey().equals(key)).findFirst();

        if(currentValue.isPresent()) currentValue.get().setValue(value);
        else blockMetadata.add(new MetadataModel(key, value));

        blockData.get().setData(blockMetadata);
        blocksWithMetadata.put(id, blockData.get());

        saveData();
    }

    public static void deleteMetadataOfBlock(Block block, String key) {
        String id = block.getLocation().toString();
        Optional<BlockMetadataModel> blockData = getBlockData(block);

        if(blockData.isEmpty()) return;

        List<MetadataModel> blockMetadata = blockData.get().getData();
        blockMetadata = blockMetadata.stream().filter(m -> !m.getKey().equals(key)).collect(Collectors.toList());

        blockData.get().setData(blockMetadata);

        blocksWithMetadata.put(id, blockData.get());
        saveData();
    }

    public static void deleteBlock(Block block) {
        String id = block.getLocation().toString();

        blocksWithMetadata.remove(id);
        saveData();
    }

    public static void getData() {
        Type blockFileType = new TypeToken<Map<String, BlockMetadataModel>>() {}.getType();
        Map<String, BlockMetadataModel> file = Resources.getJsonByLocalData(pathBlocks, blockFileType);

        if(file != null) blocksWithMetadata = file;
        else saveData();
    }

    private static void saveData() {
        Resources.writeFile(pathBlocks, blocksWithMetadata);
    }
}
