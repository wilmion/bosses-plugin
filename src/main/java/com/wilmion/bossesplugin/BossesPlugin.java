package com.wilmion.bossesplugin;

import com.wilmion.bossesplugin.events.*;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.utils.PluginUtils;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BossesPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        new PluginUtils(this);

        // Use plugin manager for register all events
        pluginManager.registerEvents(new EntityEvents(), this);

        // Create my custom commands
        getCommand("bsspl").setExecutor(new CommandManager());
        getCommand("bsspl").setTabCompleter(new TabCompleterManager());

        new SpawnBossProbability();
        new ObserverPlayer();
        new BossesMetadata();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BossesMetadata.saveData();
    }
}
