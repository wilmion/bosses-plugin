package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.commands.SpawnBossCommand;
import com.wilmion.bossesplugin.enums.BossEnum;
import com.wilmion.bossesplugin.utils.PluginUtils;
import com.wilmion.bossesplugin.utils.RandomUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

public class SpawnBossProbability {
    private Plugin plugin;
    private Server server;
    private List<String> bossesName = BossEnum.getKeys();
    public static double delaySpawnBoss = 36000.0;

    public SpawnBossProbability() {
        this.plugin = PluginUtils.getPlugin();
        this.server = plugin.getServer();

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::setSpawnObserver, 100, 100);
    }

    private void setSpawnObserver() {
        delaySpawnBoss -= 100.0;
        if(delaySpawnBoss > 0.0) return;

        for(Player player: server.getOnlinePlayers()) {
            int probability = RandomUtils.getRandomInPercentage();

            if(probability <= 1) {
                spawnBoss(player);
                return;
            }
        }
    }

    private void spawnBoss(Player player)  {
        Random random = new Random();
        Location location = getRandomLocationNearlyPlayer(player);
        Integer randomIndex = random.nextInt(bossesName.size());
        String bossName = bossesName.get(randomIndex);
        String content = "¿Te crees lo suficiente bueno para los retos?\nVen búscame aquí si te crees bueno y competitivo\n[LOCATION]";

        showMsg(location, content, ChatColor.GRAY);
        SpawnBossCommand.spawnBoss(bossName, location);
        delaySpawnBoss = 36000.0;
    }

    public void showMsg(Location location, String text, ChatColor color) {
        Integer X = (int) location.getX();
        Integer Y = (int) location.getY();
        Integer Z = (int) location.getZ();

        String messageCord = "X: " + X +" Y: "+ Y + " Z: " + Z;
        String content = text.replace("[LOCATION]", messageCord);

        plugin.getServer().broadcastMessage( color + content);
    }
    public static Location getRandomLocationNearlyPlayer(Player player) {
        Location location = player.getLocation().clone();
        Integer modX = (RandomUtils.getRandomInPercentage() + 200) * getRandomMultiplier();
        Integer modZ = (RandomUtils.getRandomInPercentage() + 200) * getRandomMultiplier();
        Double modY = (double) player.getWorld().getHighestBlockYAt(location);

        location.setX(location.getX() + modX);
        location.setZ(location.getZ() + modZ);
        location.setY(modY);

        return location;
    }

    private static Integer getRandomMultiplier() {
        Integer multiplier = RandomUtils.getRandomNumberForSpace();

        return multiplier == 0? 1 : multiplier;
    }
}
