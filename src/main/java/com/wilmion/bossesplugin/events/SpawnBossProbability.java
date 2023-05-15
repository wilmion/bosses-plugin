package com.wilmion.bossesplugin.events;

import com.wilmion.bossesplugin.mobsDificulties.boss.*;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.function.Function;


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
                this.delaySpawnBoss = 36000.0;
            }
        }
    }

    private void probabilityToSpawn(Player player)  {
        Random random = new Random();
        Location location = getRandomLocationNearlyPlayer(player);

        Class<?>[] objects  = { SupportZombie.class, MasterSkeleton.class, QueenSpider.class, SoldierSpider.class, MasterCreeper.class};

        int randomIndex = random.nextInt(objects.length);

        Constructor constructor =  objects[randomIndex].getConstructors()[0];

        try {
            constructor.newInstance(location, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
