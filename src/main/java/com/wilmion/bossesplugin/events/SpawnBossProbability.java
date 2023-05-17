package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.commands.SpawnBossCommand;
import com.wilmion.bossesplugin.utils.Resources;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpawnBossProbability {
    private Plugin plugin;
    private Server server;
    private List<String> bossesName;
    public static double delaySpawnBoss = 36000.0;

    public SpawnBossProbability(Plugin plugin) {
        Map<String, Object> file = Resources.getJsonByData("commands-boss.json", Map.class);

        this.plugin = plugin;
        this.server = plugin.getServer();
        this.bossesName = (List<String>) file.get("bosses");

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::setSpawnObserver, 100, 100);
    }

    private void setSpawnObserver() {
        delaySpawnBoss -= 100.0;
        if(delaySpawnBoss > 0.0) return;

        for(Player player: server.getOnlinePlayers()) {
            int probability = Utils.getRandomInPercentage();

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
        SpawnBossCommand.spawnBoss(bossName, location, plugin);
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
        Integer modX = (Utils.getRandomInPercentage() + 200) * getRandomMultiplier();
        Integer modZ = (Utils.getRandomInPercentage() + 200) * getRandomMultiplier();
        Double modY = (double) player.getWorld().getHighestBlockYAt(location);

        location.setX(location.getX() + modX);
        location.setZ(location.getZ() + modZ);
        location.setY(modY);

        return location;
    }

    private static Integer getRandomMultiplier() {
        Integer multiplier = Utils.getRandomNumberForSpace();

        return multiplier == 0? 1 : multiplier;
    }
}
