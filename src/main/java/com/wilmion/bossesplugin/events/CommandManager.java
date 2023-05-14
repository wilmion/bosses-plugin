package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.commands.*;
import com.wilmion.bossesplugin.objects.CommandDataModel;
import com.wilmion.bossesplugin.utils.*;

import com.google.common.reflect.TypeToken;

import net.kyori.adventure.text.Component;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class CommandManager implements CommandExecutor {
    private Plugin plugin;

    private Type type = new TypeToken<Map<String, List<CommandDataModel>>>() {}.getType();

    private Map<String, List<CommandDataModel>> allHelp;

    /* === Commands === */
    private SpawnBossCommand spawnBossCommand;

    private BuildCommand buildCommand;

    private SaveCommand saveCommand;

    private MetadataBlockCommand metadataBlockCommand;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
        this.allHelp = Resources.getJsonByData("commands-help.json", type);
        this.spawnBossCommand = new SpawnBossCommand(plugin);
        this.buildCommand = new BuildCommand(plugin);
        this.saveCommand = new SaveCommand(plugin);
        this.metadataBlockCommand = new MetadataBlockCommand(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player) || args.length == 0) return false;

        Player player = (Player) sender;
        String subCommand = args[0];

        if(subCommand.equals("help")) return showHelp(player);
        if(subCommand.equals("spawnboss")) return spawnBossCommand(player, args);
        if(subCommand.equals("build")) return build(player, args);
        if(subCommand.equals("save")) return saveBuild(player, args);
        if(subCommand.equals("metadata-block")) return metadataBlockCmd(player, args);

        return false;
    }

    private Boolean metadataBlockCmd(Player player, String[] args) {
        List<String> errors = metadataBlockCommand.handleCommand(player, args);

        if (errors == null) return true;

        Function<String, String> modifyText = (text) -> {
            String result = "";
            for(String error: errors) result += error;
            return text.replace("[CONTENT]", result);
        };

        return printHelp(allHelp.get("metadata_error"), player, modifyText);
    }

    private boolean showHelp(Player player) {
        return printHelp(allHelp.get("general_help"), player);
    }

    private boolean spawnBossCommand(Player player, String[] args) {
        Function<String, String> modifyText = (text) -> {
            String result = "";
            for(String bossName: spawnBossCommand.getBossesName()) result += "\nKey: " + bossName;
            return text.replace("[BOSSES]", result);
        };

        Boolean spawnedBoss = spawnBossCommand.handleCommand(player, args);

        if (!spawnedBoss) return printHelp(allHelp.get("spawn_boss_error"), player, modifyText);

        return true;
    }

    private boolean build(Player player, String[] args) {
        Boolean built = buildCommand.handleCommand(player, args);

        Function<String, String> lambda = (text) -> {
            String result = "";
            for (String name: buildCommand.getBuildingsNames()) result += "\nKey: " + name;
            return text.replace("[BUILD_NAME]", result);
        };

        if(!built) return printHelp(allHelp.get("build_error"), player, lambda);

        return true;
    }

    private boolean saveBuild(Player player, String[] args) {
        Boolean buildSaved = saveCommand.handleCommand(player, args);

        if(!buildSaved) return printHelp(allHelp.get("save_error"), player);

        return true;
    }

    /* === Print Help === */

    private boolean printHelp(List<CommandDataModel> helpData, Player player, Function<String, String> modifyText) {
        helpData.forEach(help -> {
            ChatColor color = ChatColor.valueOf(help.getColor());
            String formatted = color + modifyText.apply(help.getText());

            player.sendMessage(Component.text(formatted));
        });

        return true;
    }

    private boolean printHelp(List<CommandDataModel> helpData, Player player) {
        return printHelp(helpData, player, (text) -> text);
    }

}
