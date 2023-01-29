package com.wilmion.alaractplugin;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wilmion.alaractplugin.player.PlayerDifficulty;
import com.wilmion.alaractplugin.player.PlayerExp;

import com.wilmion.alaractplugin.mobsDificulties.CreeperDifficulty;
import com.wilmion.alaractplugin.mobsDificulties.SkeletonDifficulty;
import com.wilmion.alaractplugin.mobsDificulties.ZombieDifficulty;

public final class AlaractPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().warning("Inicializando el modo Alaraco GAAA!");

        // PLugins declaration
        SkeletonDifficulty skeletonPlugin = new SkeletonDifficulty(this);
        ZombieDifficulty zombiePlugin = new ZombieDifficulty(this);

        // We get a plugin manager
        PluginManager pluginManager = getServer().getPluginManager();

        // Use plugin manager for register all events
        pluginManager.registerEvents(new PlayerExp(this), this);
        pluginManager.registerEvents(new PlayerDifficulty(), this);
        pluginManager.registerEvents(new CreeperDifficulty(), this);
        pluginManager.registerEvents(skeletonPlugin, this);
        pluginManager.registerEvents(zombiePlugin, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
