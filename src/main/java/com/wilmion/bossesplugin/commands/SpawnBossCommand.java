package com.wilmion.bossesplugin.commands;

import com.wilmion.bossesplugin.enums.BossEnum;

import lombok.Getter;
import lombok.SneakyThrows;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.List;

@Getter
public class SpawnBossCommand {
    private List<String> bossesName = BossEnum.getKeys();

    public boolean handleCommand(Player player, String[] args) {
        if(args.length < 2) return false;

        String bossType = args[1];
        Location location = player.getLocation().clone();
        Boolean match = bossesName.stream().anyMatch(bossType::equals);

        if(!match) return false;

        spawnBoss(bossType, location);

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
    public static void spawnBoss(String bossType, Location location) {
        String constructorName = convertBossCommandToConstructorName(bossType);

        Class<?> ClassEntity = Class.forName("com.wilmion.bossesplugin.mobsDificulties.boss." + constructorName);
        Constructor<?> constructor = ClassEntity.getConstructor(Location.class);
        constructor.newInstance(location);
    }
}
