package com.wilmion.bossesplugin;

import com.wilmion.bossesplugin.events.CommandManager;
import com.wilmion.bossesplugin.events.EntityEvents;
import com.wilmion.bossesplugin.events.ObserverPlayer;
import com.wilmion.bossesplugin.events.SpawnBossProbability;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BossesPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Use plugin manager for register all events
        pluginManager.registerEvents(new EntityEvents(this), this);

        // Create my custom commands
        getCommand("bsspl").setExecutor(new CommandManager(this));

        new SpawnBossProbability(this);
        new ObserverPlayer(this);
        new BossesMetadata(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BossesMetadata.saveData();
    }
}
