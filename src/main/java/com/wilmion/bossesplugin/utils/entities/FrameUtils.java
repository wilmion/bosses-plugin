package com.wilmion.bossesplugin.utils.entities;

import com.wilmion.bossesplugin.utils.LocationUtils;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Optional;

public class FrameUtils {
    public static String saveFrame(Entity entity, Location playerLoc) {
        ItemFrame itemFrame = (ItemFrame) entity;
        StringBuilder dataBuilder = new StringBuilder();
        String locString = LocationUtils.convertLocationAsString(itemFrame.getLocation(), playerLoc);

        dataBuilder.append("ItemFrame").append(";");
        dataBuilder.append(locString).append(";");
        dataBuilder.append(itemFrame.getRotation()).append(";");
        dataBuilder.append("Item:").append(ItemStackUtils.serializeItemStack(itemFrame.getItem())).append(";");

        return ChatColor.translateAlternateColorCodes('&', dataBuilder.toString());
    }

    public static Optional<ItemFrame> spawnItemFrame(String stringData, Location location) {
        String[] dataParts = ChatColor.stripColor(stringData).split(";");
        Boolean isItemFrame = dataParts[0].equals("ItemFrame");

        if(!isItemFrame) return Optional.empty();

        Location loc = LocationUtils.getLocationFromString(dataParts[1], location);
        ItemFrame itemFrame = loc.getWorld().spawn(loc, ItemFrame.class);

        itemFrame.setRotation(Rotation.valueOf(dataParts[2]));
        itemFrame.setItem(ItemStackUtils.parseItemStack(dataParts[3]));

        return Optional.of(itemFrame);
    }
}
