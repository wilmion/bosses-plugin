package com.wilmion.alaractplugin.mobsDificulties.boss;

import com.wilmion.alaractplugin.models.BoosesModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.TreeMap;

public class MasterSkeleton extends BoosesModel {
    static Map<String, MasterSkeleton> bosses = new TreeMap<>();
    static double maxHealth = 100.0;
    static String idMetadata = "SKELETON_BOSS";

    public MasterSkeleton(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth, "SKELETON", idMetadata);
        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);
    }

    private Skeleton getBoos() {
        return (Skeleton) this.entity;
    }

    @Override
    protected void equipBoss() {
        ItemStack item = new ItemStack(Material.IRON_HELMET);

        item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);

        this.getBoos().getEquipment().setHelmet(item);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        return BoosesModel.handleDamageByEntity(event, BarColor.WHITE, maxHealth, idMetadata, "SKELETON");
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "SKELETON", BarColor.WHITE, maxHealth, idMetadata);
    }

}
