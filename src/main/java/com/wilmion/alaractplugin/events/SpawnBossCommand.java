package com.wilmion.alaractplugin.events;

import com.wilmion.alaractplugin.mobsDificulties.boss.MasterSkeleton;
import com.wilmion.alaractplugin.mobsDificulties.boss.QueenSpider;
import com.wilmion.alaractplugin.mobsDificulties.boss.SoldierSpider;
import com.wilmion.alaractplugin.mobsDificulties.boss.SupportZombie;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SpawnBossCommand implements CommandExecutor {
    Plugin plugin;

    public SpawnBossCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String[] booses = {"support-zombie", "master-skeleton", "soldier-spider", "queen-spider"};

        if(!(sender instanceof Player)) return false;

        String bossType = args[0];
        Player player = (Player) sender;

        Location location = player.getLocation();

        if(bossType.equals(booses[0])) new SupportZombie(player, location, plugin);
        if(bossType.equals(booses[1])) new MasterSkeleton(player, location, plugin);
        if(bossType.equals(booses[2])) new SoldierSpider(player, location, plugin);
        if(bossType.equals(booses[3])) new QueenSpider(player, location, plugin);

        return Arrays.stream(booses).anyMatch(bossType::equals);
    }
}
