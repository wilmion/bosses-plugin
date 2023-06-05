package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.commands.MetadataBlockCommand;
import com.wilmion.bossesplugin.enums.BossEnum;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleterManager implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!(sender instanceof Player) || !sender.isOp()) return null;

        String subCommand = args[0];
        Boolean existOne = args.length > 0;

        if(subCommand.equals("spawnboss") && args.length == 2) return BossEnum.getKeys();
        if(subCommand.equals("build") && existOne) return buildCommand(args);
        if(subCommand.equals("metadata-block") && existOne) return metadataBlock(args);

        if(args.length > 1) return new ArrayList<>();

        return Arrays.asList("help", "spawnboss", "build", "save", "metadata-block", "clean-range");
    }

    private List<String> buildCommand(String[] args) {
        BuildCommand buildCommand = new BuildCommand();

        if(args.length == 2) return buildCommand.buildingsNames;
        if(args.length == 3) return Arrays.asList("0deg", "90deg", "180deg", "270deg");

        return new ArrayList<>();
    }

    private List<String> metadataBlock(String[] args) {
        if(args.length <= 2) return Arrays.asList("spawn-entity", "boss", "show", "delete");

        String metadataOpt = args[1];
        MetadataBlockCommand command = new MetadataBlockCommand();

        if(metadataOpt.equals("spawn-entity") && args.length == 3) return command.specialEntitiesName;
        if(metadataOpt.equals("boss") && args.length == 3) return BossEnum.getKeys();

        return new ArrayList<>();
    }
}
