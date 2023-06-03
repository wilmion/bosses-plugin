package com.wilmion.bossesplugin.utils.material;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Random;

public class EnchantmentUtils {
    public static ItemStack getRandomEnchantmentBook(Enchantment[] enchantmentsList) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        Enchantment enchantment = getRandomEnchantment(enchantmentsList);
        Integer level = getRandomLevel(enchantment);

        meta.addStoredEnchant(enchantment, level, true);
        book.setItemMeta(meta);

        return book;
    }

    public static ItemStack getRandomEnchantmentBook() {
        return getRandomEnchantmentBook(Enchantment.values());
    }

    private static Enchantment getRandomEnchantment(Enchantment[] enchantments) {
        Random random = new Random();
        Integer randomIndex = random.nextInt(enchantments.length);

        return enchantments[randomIndex];
    }

    private static Integer getRandomLevel(Enchantment enchantment) {
        Random random = new Random();
        Integer maxLevel = enchantment.getMaxLevel();

        return random.nextInt(maxLevel) + 1;
    }
}
