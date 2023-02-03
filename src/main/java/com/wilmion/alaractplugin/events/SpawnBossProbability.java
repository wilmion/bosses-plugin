package com.wilmion.alaractplugin.events;

import com.wilmion.alaractplugin.mobsDificulties.boss.MasterSkeleton;
import com.wilmion.alaractplugin.mobsDificulties.boss.QueenSpider;
import com.wilmion.alaractplugin.mobsDificulties.boss.SoldierSpider;
import com.wilmion.alaractplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.alaractplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class SpawnBossProbability {
    private Plugin plugin;
    private Server server;
    private double delaySpawnBoss = 36000.0;
    public SpawnBossProbability(Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::setSpawnObserver, 100, 100);
    }

    private void setSpawnObserver() {
        this.delaySpawnBoss -= 100.0;

        if(delaySpawnBoss > 0.0) return;

        for(Player player: server.getOnlinePlayers()) {
            int probability = Utils.getRandomInPercentage();

            if(probability <= 1) {
                this.probabilityToSpawn(player);
                this.delaySpawnBoss = 36000;
            }
        }
    }

    private void probabilityToSpawn(Player player) {
        int probability = Utils.getRandomInPercentage();

        Location location = getRandomLocationNearlyPlayer(player);

        if(probability >= 0 && probability <= 25) new SupportZombie(player, location, plugin);
        if(probability > 25 && probability <= 50) new MasterSkeleton(player, location, plugin);
        if(probability > 50 && probability <= 75) new QueenSpider(player, location, plugin);
        if(probability > 75) new SoldierSpider(player, location, plugin);
    };

    private int getRandomMultiplier() {
        int multiplier = Utils.getRandomNumberForSpace();

        return multiplier == 0? 1 : multiplier;
    }

    private Location getRandomLocationNearlyPlayer(Player player) {
           Location location = player.getLocation().clone();

           int modX = (Utils.getRandomInPercentage() + 200) * this.getRandomMultiplier();
           int modZ = (Utils.getRandomInPercentage() + 200) * this.getRandomMultiplier();

           location.setX(location.getX() + modX);
           location.setZ(location.getZ() + modZ);

           double modY = player.getWorld().getHighestBlockYAt(location);

           location.setY(modY);

           String messageCord = "X: "+location.getX()+" Y: "+location.getY() +"Z: " + location.getZ();

           server.broadcastMessage(ChatColor.GREEN + "Ayudenme! Dare recompensa, Busquenme aqui => " + messageCord);

           return location;
    }
}
