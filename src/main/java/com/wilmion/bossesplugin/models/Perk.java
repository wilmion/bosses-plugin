package com.wilmion.bossesplugin.models;

import com.wilmion.bossesplugin.objects.perk.PerkDataModel;
import com.wilmion.bossesplugin.objects.perk.PerkModel;
import com.wilmion.bossesplugin.utils.PluginUtils;
import com.wilmion.bossesplugin.utils.Resources;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Perk {
    private static final String PERK_METADATA = "PERK_BOOSTS_PLUGIN";

    private static List<PerkDataModel> perksData;

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

    public static void generatePerk(Integer id, Location location) {
        getPerksData();
        PerkDataModel perkMtd = perksData.stream().filter(p -> p.getId().equals(id)).collect(Collectors.toList()).get(0);

        Material material = Material.getMaterial(perkMtd.getMaterial());
        ItemStack perk = new ItemStack(material, 1, (short) 0);
        String name = ChatColor.valueOf(perkMtd.getColorName()) + perkMtd.getName();
        Component displayName = Component.text(name);
        ItemMeta perkMetadata = perk.getItemMeta();
        PersistentDataContainer perkDataContainer = perkMetadata.getPersistentDataContainer();

        perkMetadata.displayName(displayName);
        perkDataContainer.set(new NamespacedKey(PluginUtils.getPlugin(), PERK_METADATA), PersistentDataType.STRING, "true");

        perk.setItemMeta(perkMetadata);

        location.getWorld().dropItem(location, perk);
    }

    public static void usePerkFunctionality(Player player) {
        getPerksData();
        perksData.forEach(perk -> {
            Material material = Material.getMaterial(perk.getMaterial());
            PlayerInventory inventory = player.getInventory();
            List<PerkPotionItem> effects = perk.getEffects().stream().map(p -> new PerkPotionItem(p.getName(), p.getLevel() - 1)).collect(Collectors.toList());

            boolean isPerkItem = isPerk(inventory.getItemInMainHand(), inventory.getItemInOffHand(), material);

            if(isPerkItem) useAbility(player, effects);
        });
    }

    private static boolean isPerk(ItemStack mainItem, ItemStack offItem, Material material) {
        ItemStack perk = null;

        if(mainItem.getType().equals(material)) perk = mainItem;
        if(offItem.getType().equals(material)) perk = offItem;

        if(perk == null) return false;

        PersistentDataContainer perkMeta = perk.getItemMeta().getPersistentDataContainer();

        boolean isPerkMetadata = perkMeta.has(new NamespacedKey(PluginUtils.getPlugin(), PERK_METADATA));

        return isPerkMetadata;
    }

    private static void useAbility(Player player, List<PerkPotionItem> effects) {
        Function<PerkPotionItem, PotionEffect> mapper = (effect) -> new PotionEffect(PotionEffectType.getByName(effect.getName()), 200, effect.getLevel());

        Collection<PotionEffect> potionEffects = effects.stream().map(mapper).collect(Collectors.toList());

        player.addPotionEffects(potionEffects);
    }

    private static void getPerksData() {
        PerkModel perkFile = Resources.getJsonByData("perks.json", PerkModel.class);
        perksData = perkFile.getPerks();
    }
}
