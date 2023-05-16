package com.wilmion.bossesplugin.mobsDificulties.special;

import com.wilmion.bossesplugin.objects.special_entity.SpecialEntityDataModel;
import com.wilmion.bossesplugin.objects.special_entity.SpecialEntityModel;
import com.wilmion.bossesplugin.utils.Resources;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class SpecialEntity {
    private LivingEntity living;

    public SpecialEntity(Location location, String type) {
        SpecialEntityDataModel entityData = getEntityData(type);
        Entity entity = location.getWorld().spawnEntity(location, EntityType.valueOf(entityData.getEntityType()));
        living = (LivingEntity) entity;

        AttributeInstance healthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance view = living.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        healthAttribute.setBaseValue(entityData.getHealth());

        living.setHealth(entityData.getHealth());
        living.setRemoveWhenFarAway(false);

        entity.setCustomName(entityData.getName());
        entity.setCustomNameVisible(false);

        if(entityData.getFollowRange().isPresent()) view.setBaseValue(entityData.getFollowRange().get());

        equipEntity(entityData.getEquipment());
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

    private SpecialEntityDataModel getEntityData(String type) {
        SpecialEntityModel file = Resources.getJsonByData("special-entities.json", SpecialEntityModel.class);

        return file.getEntities().stream().filter(e -> e.getKey().equals(type)).collect(Collectors.toList()).get(0);
    }
}
