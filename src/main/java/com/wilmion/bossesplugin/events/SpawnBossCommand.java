package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.mobsDificulties.boss.*;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
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
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        String subCommand = args[0];

        if(subCommand.equals("spawnboss")) return spawnBossCommand(player, args[1]);
        if(subCommand.equals("help")) return showHelp(player);

        return false;
    }

    private boolean showHelp(Player player) {
        player.sendMessage(Component.text(ChatColor.LIGHT_PURPLE + "Help Information"));
        player.sendMessage(Component.text(ChatColor.DARK_GREEN + "/bsspl spawnboss <name-of-boss> -> Generate a boss in your current location"));

        return true;
    }

    private boolean spawnBossCommand(Player player, String bossType) {
        String[] bosses = {"support-zombie", "master-skeleton", "soldier-spider", "queen-spider", "master-creeper"};

        Location location = player.getLocation().clone();

        if(bossType.equals(bosses[0])) new SupportZombie(player, location, plugin);
        if(bossType.equals(bosses[1])) new MasterSkeleton(player, location, plugin);
        if(bossType.equals(bosses[2])) new SoldierSpider(player, location, plugin);
        if(bossType.equals(bosses[3])) new QueenSpider(player, location, plugin);
        if(bossType.equals(bosses[4])) new MasterCreeper(player, location, plugin);

        return Arrays.stream(bosses).anyMatch(bossType::equals);
    }
}
