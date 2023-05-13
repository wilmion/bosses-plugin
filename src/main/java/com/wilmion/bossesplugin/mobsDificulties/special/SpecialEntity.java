package com.wilmion.bossesplugin.mobsDificulties.special;

import com.google.gson.Gson;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecialEntity {
    private final String path = "special-entities.json";

    private LivingEntity living;

    public SpecialEntity(World world, Location location, String type) {
        Map<String, Object> entityData = getEntityData(type);

        Double health = (Double) entityData.get("health");
        String name = (String) entityData.get("name");
        String entityType = (String) entityData.get("entityType");

        Entity entity = world.spawnEntity(location, EntityType.valueOf(entityType));

        living = (LivingEntity) entity;

        AttributeInstance healthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(health);

        AttributeInstance view = living.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

        if(entityData.get("followRange") != null) view.setBaseValue((Double) entityData.get("followRange"));
        // if(entityData.get("noAI") != null) living.setAI(false);

        living.setHealth(health);
        living.setRemoveWhenFarAway(false);

        entity.setCustomName(name);
        entity.setCustomNameVisible(false);

        if(entityData.get("equipment") != null) equipEntity((Map<String, Object>) entityData.get("equipment"));
    }

    public void equipEntity(Map<String, Object> data) {
        EntityEquipment equipment = living.getEquipment();

        if(data.get("helmet") != null) {
            ItemStack helmet = convertEquipmentDataToItemStack((Map<String, Object>) data.get("helmet"));
            equipment.setHelmet(helmet);
        }
        if(data.get("chestplate") != null) {
            ItemStack chestplate = convertEquipmentDataToItemStack((Map<String, Object>) data.get("chestplate"));
            equipment.setChestplate(chestplate);
        }
        if(data.get("leggings") != null) {
            ItemStack leggings = convertEquipmentDataToItemStack((Map<String, Object>) data.get("leggings"));
            equipment.setLeggings(leggings);
        }
        if(data.get("boots") != null) {
            ItemStack boots = convertEquipmentDataToItemStack((Map<String, Object>) data.get("boots"));
            equipment.setBoots(boots);
        }
        if(data.get("sword") != null) {
            ItemStack sword = convertEquipmentDataToItemStack((Map<String, Object>) data.get("sword"));
            equipment.setItemInMainHand(sword);
        }
    }

    private ItemStack convertEquipmentDataToItemStack(Map<String, Object> mtd) {
        ItemStack item  = new ItemStack(Material.valueOf((String) mtd.get("type")));
        ItemMeta meta = item.getItemMeta();

        if(mtd.get("unbreakable") != null) meta.setUnbreakable(true);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        item.setDurability(Short.MAX_VALUE);

        return item;
    }

    private Map<String, Object> getJson() {
        Gson gson = new Gson();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        InputStreamReader reader = new InputStreamReader(inputStream);

        return gson.fromJson(reader, Map.class);
    }

    private Map<String, Object> getEntityData(String type) {
        Map<String, Object> json = getJson();
        List<Map<String, Object>> entities = (List<Map<String, Object>>) json.get("entities");
        Map<String, Object> entity = entities.stream().filter(data -> data.get("key").equals(type)).collect(Collectors.toList()).get(0);

        return entity;
    }
}
