package com.wilmion.bossesplugin.utils;

import org.bukkit.plugin.Plugin;

public class PluginUtils {
    private static Plugin plugin;

    public PluginUtils(Plugin pl) {
        plugin = pl;
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
