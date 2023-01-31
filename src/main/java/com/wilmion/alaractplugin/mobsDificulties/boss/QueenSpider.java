package com.wilmion.alaractplugin.mobsDificulties.boss;

import com.wilmion.alaractplugin.interfaces.IUltimateLambda;
import com.wilmion.alaractplugin.models.BoosesModel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.TreeMap;

public class QueenSpider extends BoosesModel {
    private static Map<String, QueenSpider> bosses = new TreeMap<>();
    private static String idMetadata = "QUEEN_SPIDER_BOSS";
    private static double maxHealth = 310.0;
    public QueenSpider(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth, "SPIDER", idMetadata, "Cristal de Hueso", "AURORA LA REINA");

        this.colorTextPerk = ChatColor.BLACK;
        this.materialPerk = Material.BLACK_DYE;

        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, BarColor.PURPLE, maxHealth, idMetadata, "SPIDER", null);

        return continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "SPIDER", BarColor.PURPLE, maxHealth, idMetadata);
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, idMetadata, bosses);
    }

}
