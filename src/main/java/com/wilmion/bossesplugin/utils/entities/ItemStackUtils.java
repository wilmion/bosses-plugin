package com.wilmion.bossesplugin.utils.entities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtils {
    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return "";

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) return itemStack.getType().toString();

        String displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "";

        return itemStack.getType() + ":" + displayName;
    }

    public static ItemStack parseItemStack(String itemData) {
        if (itemData.isEmpty()) return new ItemStack(Material.AIR);

        String[] itemParts = itemData.split(":");
        Material itemType = Material.valueOf(itemParts[1]);
        String displayName = itemParts.length > 2 ? itemParts[2] : null;

        ItemStack itemStack = new ItemStack(itemType);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
