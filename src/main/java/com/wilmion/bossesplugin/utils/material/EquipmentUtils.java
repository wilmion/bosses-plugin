package com.wilmion.bossesplugin.utils.material;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipmentUtils {
    public static ItemStack enchantmentToItemStack(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        List<Enchantment> enchantmentsList = Arrays.stream(Enchantment.values()).filter(e -> e.canEnchantItem(item)).collect(Collectors.toList());
        ItemStack book = EnchantmentUtils.getRandomEnchantmentBook(enchantmentsList.toArray(new Enchantment[0]));
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
        Map<Enchantment, Integer> enchantments = bookMeta.getStoredEnchants();

        if(enchantments.isEmpty()) return item;

        Enchantment enchantment = enchantments.keySet().iterator().next();
        Integer level = enchantments.get(enchantment);

        itemMeta.addEnchant(enchantment, level, true);
        item.setItemMeta(itemMeta);

        return item;
    }
}
