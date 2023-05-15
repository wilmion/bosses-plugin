package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.utils.Resources;

import lombok.Getter;
import lombok.SneakyThrows;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

@Getter
public class SpawnBossCommand {
    private Plugin plugin;

    private List<String> bossesName;

    public SpawnBossCommand(Plugin plugin) {
        Map<String, Object> file = Resources.getJsonByData("commands-boss.json", Map.class);

        this.plugin = plugin;
        this.bossesName = (List<String>) file.get("bosses");
    }


    public boolean handleCommand(Player player, String[] args) {
        if(args.length < 2) return false;

        String bossType = args[1];
        Location location = player.getLocation().clone();
        Boolean match = bossesName.stream().anyMatch(bossType::equals);

        if(!match) return false;

        spawnBoss(bossType, location, plugin);

        return true;
    }

    private static String convertBossCommandToConstructorName(String bossType) {
        StringBuilder sb = new StringBuilder();

        boolean capitalizeNext = true;

        for (char c : bossType.toCharArray()) {
            if (c == '-') capitalizeNext = true;
            else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else sb.append(c);
        }

        return sb.toString();
    }

    @SneakyThrows
    public static void spawnBoss(String bossType, Location location, Plugin plugin) {
        String constructorName = convertBossCommandToConstructorName(bossType);

        Class<?> ClassEntity = Class.forName("com.wilmion.bossesplugin.mobsDificulties.boss." + constructorName);
        Constructor<?> constructor = ClassEntity.getConstructor(Location.class, Plugin.class);
        constructor.newInstance(location, plugin);
    }
}
