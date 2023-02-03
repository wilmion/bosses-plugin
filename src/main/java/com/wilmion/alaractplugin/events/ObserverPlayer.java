package com.wilmion.alaractplugin.events;

import com.wilmion.alaractplugin.models.Perk;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class ObserverPlayer {
    private final Plugin plugin;
    private final Server server;

    public ObserverPlayer(Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        this.observerEach4Seconds();
    }

    private void observerEach4Seconds() {
        Runnable task = () -> {
            for(Player player :server.getOnlinePlayers()) {
                Perk.usePerkFunctionality(plugin, player, Material.YELLOW_DYE, Arrays.asList("REGENERATION"));
                Perk.usePerkFunctionality(plugin, player, Material.WHITE_DYE, Arrays.asList("SPEED", "DAMAGE_RESISTANCE"));
                Perk.usePerkFunctionality(plugin, player, Material.BLACK_DYE, Arrays.asList("REGENERATION", "JUMP", "LUCK"));
                Perk.usePerkFunctionality(plugin, player, Material.GREEN_DYE, Arrays.asList("REGENERATION", "INCREASE_DAMAGE", "DAMAGE_RESISTANCE", "LUCK", "SPEED"));
            }
        };

        server.getScheduler().scheduleSyncRepeatingTask(plugin, task, 80 , 80);
    }
}
