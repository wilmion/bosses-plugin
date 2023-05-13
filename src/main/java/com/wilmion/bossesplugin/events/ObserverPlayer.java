package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.models.Perk.*;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
                Perk.usePerkFunctionality(plugin, player, Material.YELLOW_DYE, Arrays.asList(new PerkPotionItem("REGENERATION")));

                Perk.usePerkFunctionality(plugin, player, Material.WHITE_DYE, Arrays.asList(new PerkPotionItem("SPEED"), new PerkPotionItem("DAMAGE_RESISTANCE")));

                Perk.usePerkFunctionality(plugin, player, Material.BLACK_DYE, Arrays.asList(
                        new PerkPotionItem("REGENERATION"),
                        new PerkPotionItem("JUMP"),
                        new PerkPotionItem("LUCK")
                ));

                Perk.usePerkFunctionality(plugin, player, Material.AMETHYST_SHARD, Arrays.asList(
                        new PerkPotionItem("REGENERATION"),
                        new PerkPotionItem("DAMAGE_RESISTANCE", 3),
                        new PerkPotionItem("FIRE_RESISTANCE")
                ));

                Perk.usePerkFunctionality(plugin, player, Material.GREEN_DYE, Arrays.asList(
                        new PerkPotionItem("REGENERATION"),
                        new PerkPotionItem("INCREASE_DAMAGE"),
                        new PerkPotionItem("DAMAGE_RESISTANCE", 2),
                        new PerkPotionItem("JUMP"),
                        new PerkPotionItem("LUCK")
                ));

                Perk.usePerkFunctionality(plugin, player, Material.LIGHT_BLUE_DYE, Arrays.asList(
                        new PerkPotionItem("SPEED"),
                        new PerkPotionItem("DAMAGE_RESISTANCE", 2)
                ));
            }
        };

        server.getScheduler().scheduleSyncRepeatingTask(plugin, task, 80 , 80);
    }
}
