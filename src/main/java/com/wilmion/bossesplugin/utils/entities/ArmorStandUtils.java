package com.wilmion.bossesplugin.utils.entities;

import com.wilmion.bossesplugin.utils.LocationUtils;
import lombok.AllArgsConstructor;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

@AllArgsConstructor
public class ArmorStandUtils {
    private ArmorStand armorStand;

    public String saveArmorStand(Location pivot) {
        StringBuilder dataBuilder = new StringBuilder();
        EntityEquipment equipment = armorStand.getEquipment();
        String locString = LocationUtils.convertLocationAsString(armorStand.getLocation(), pivot);

        dataBuilder.append("ArmorStand").append(";");
        dataBuilder.append(locString).append(";");

        dataBuilder.append("Helmet:").append(ItemStackUtils.serializeItemStack(equipment.getHelmet())).append(";");
        dataBuilder.append("Chestplate:").append(ItemStackUtils.serializeItemStack(equipment.getChestplate())).append(";");
        dataBuilder.append("Leggings:").append(ItemStackUtils.serializeItemStack(equipment.getLeggings())).append(";");
        dataBuilder.append("Boots:").append(ItemStackUtils.serializeItemStack(equipment.getBoots())).append(";");

        return ChatColor.translateAlternateColorCodes('&', dataBuilder.toString());
    }
    public static Optional<ArmorStand> spawnArmorStand(String stringData, Location pivot) {
        String[] dataParts = ChatColor.stripColor(stringData).split(";");
        Boolean isArmorStand = dataParts[0].equals("ArmorStand");

        if(!isArmorStand) return Optional.empty();

        Location location = LocationUtils.getLocationFromString(dataParts[1], pivot);
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        armorStand.getEquipment().setHelmet(ItemStackUtils.parseItemStack(dataParts[2]));
        armorStand.getEquipment().setChestplate(ItemStackUtils.parseItemStack(dataParts[3]));
        armorStand.getEquipment().setLeggings(ItemStackUtils.parseItemStack(dataParts[4]));
        armorStand.getEquipment().setBoots(ItemStackUtils.parseItemStack(dataParts[5]));

        return Optional.of(armorStand);
    }


}
