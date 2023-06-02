package com.wilmion.bossesplugin.models;

import com.wilmion.bossesplugin.utils.PluginUtils;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class MobDifficulty {
    protected Plugin plugin;
    protected World world;
    protected Server server;

    public MobDifficulty() {
        this.plugin = PluginUtils.getPlugin();
        this.server = plugin.getServer();
    }
}
