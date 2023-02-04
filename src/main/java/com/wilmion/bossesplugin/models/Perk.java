package com.wilmion.bossesplugin.models;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Perk {
    private static final String PERK_METADATA = "PERK_BOOSTS_PLUGIN";

    public static class PerkPotionItem {
        private String name;
        private int level = 1;

        public PerkPotionItem(String name) {
            this.name = name;
        }

        public PerkPotionItem(String name, int level) {
            this.name = name;
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }
    }

    public static void generatePerk(String name, Material material, Location location, ChatColor color,  World world, Plugin plugin) {
        ItemStack perk = new ItemStack(material, 1, (short) 0);

        ItemMeta perkMetadata = perk.getItemMeta();
        PersistentDataContainer perkDataContainer = perkMetadata.getPersistentDataContainer();

        Component displayName = Component.text(color + name);

        perkMetadata.displayName(displayName);

        perkDataContainer.set(new NamespacedKey(plugin, PERK_METADATA), PersistentDataType.STRING, "true");

        perk.setItemMeta(perkMetadata);

        world.dropItem(location, perk);
    }

    public static void usePerkFunctionality(Plugin plugin, Player player, Material material, List<PerkPotionItem> effects) {
        PlayerInventory inventory = player.getInventory();

        boolean isPerkItem = isPerk(inventory.getItemInMainHand(), inventory.getItemInOffHand(), plugin, material);

        if(isPerkItem) useAbility(player, effects);
    }

    private static boolean isPerk(ItemStack mainItem, ItemStack offItem, Plugin plugin, Material material) {
        ItemStack perk = null;

        if(mainItem.getType().equals(material)) perk = mainItem;
        if(offItem.getType().equals(material)) perk = offItem;

        if(perk == null) return false;

        PersistentDataContainer perkMeta = perk.getItemMeta().getPersistentDataContainer();

        boolean isPerkMetadata = perkMeta.has(new NamespacedKey(plugin, PERK_METADATA));

        return isPerkMetadata;
    }

    private static void useAbility(Player player, List<PerkPotionItem> effects) {
        Function<PerkPotionItem, PotionEffect> mapper = (effect) -> new PotionEffect(PotionEffectType.getByName(effect.getName()), 200, effect.getLevel());

        Collection<PotionEffect> potionEffects = effects.stream().map(mapper).collect(Collectors.toList());

        player.addPotionEffects(potionEffects);
    }
}
