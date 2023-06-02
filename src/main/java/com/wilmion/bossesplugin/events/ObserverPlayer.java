package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.utils.PluginUtils;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ObserverPlayer {
    private final Plugin plugin;
    private final Server server;

    public ObserverPlayer() {
        this.plugin = PluginUtils.getPlugin();
        this.server = plugin.getServer();

        this.observerEach4Seconds();
    }

    private void observerEach4Seconds() {
        Runnable task = () -> {
            for(Player player: server.getOnlinePlayers()) Perk.usePerkFunctionality(player);
        };

        server.getScheduler().scheduleSyncRepeatingTask(plugin, task, 80 , 80);
    }
}
