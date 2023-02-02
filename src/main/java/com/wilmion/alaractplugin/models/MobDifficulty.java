package com.wilmion.alaractplugin.models;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class MobDifficulty {
    protected Plugin plugin;
    protected World world;
    protected Server server;

    public MobDifficulty(Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }
}
