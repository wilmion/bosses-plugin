package com.wilmion.bossesplugin;

import com.wilmion.bossesplugin.events.CommandManager;
import com.wilmion.bossesplugin.events.EntityEvents;
import com.wilmion.bossesplugin.events.ObserverPlayer;
import com.wilmion.bossesplugin.events.SpawnBossProbability;

import com.wilmion.bossesplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.KeepMetadata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;
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
    }

    @Override
    public void onDisable() {
        KeepMetadata keepMetadata = new KeepMetadata(this);
        keepMetadata.saveBossMetadata(BoosesModel.bosses);
        // Plugin shutdown logic
    }
}
