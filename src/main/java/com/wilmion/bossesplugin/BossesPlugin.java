package com.wilmion.bossesplugin;

import com.wilmion.bossesplugin.events.EntityEvents;
import com.wilmion.bossesplugin.events.ObserverPlayer;
import com.wilmion.bossesplugin.events.SpawnBossCommand;
import com.wilmion.bossesplugin.events.SpawnBossProbability;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BossesPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Use plugin manager for register all events
        pluginManager.registerEvents(new EntityEvents(this), this);

        // Create my custom commands
        getCommand("spawnboss").setExecutor(new SpawnBossCommand(this));

        new SpawnBossProbability(this);
        new ObserverPlayer(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
