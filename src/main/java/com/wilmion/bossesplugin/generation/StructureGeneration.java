package com.wilmion.bossesplugin.generation;

import com.google.common.reflect.TypeToken;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.objects.LocationDataModel;
import com.wilmion.bossesplugin.utils.*;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

public class StructureGeneration {
    private static String path = "plugins/bosses-plugin-data/game-data/structures.json";
    private static List<String> textToShow = new ArrayList<>();
    public static Integer[] posGeneration = {0,0,0,0};

    public StructureGeneration() {
        Plugin plugin = PluginUtils.getPlugin();
        Logger logger = plugin.getLogger();

        logger.info("Checking all buildings of bosses... \uD83D\uDD28");

        new JordiStructureGeneration();
        new AnnStructureGeneration();
        new QueenSpiderStructureGeneration();

        logger.info("All builds finish! \uD83D\uDC77");
    }

    public static void sendPlayerMessageLocation(Player player) {
        textToShow.forEach(t -> player.sendMessage(Component.text(ChatColor.BOLD + t)));
    }

    public static Location getLocationStructure(Integer modifierY) {
        World world = Bukkit.getWorlds().get(0);
        Random random = new Random();
        Integer seaLevel = world.getSeaLevel() - modifierY;
        Integer maxX = posGeneration[1];
        Integer maxZ = posGeneration[3];
        Integer minX = posGeneration[0];
        Integer minZ = posGeneration[2];
        Integer randomX = random.nextInt(maxX - minX + 1) + minX;
        Integer randomZ = random.nextInt(maxZ - minZ + 1) + minZ;

        return new Location(world, randomX, seaLevel, randomZ);
    }

    public static Location getLocationStructure() {
        return getLocationStructure(1);
    }

    public static void generateBuild(Location location, String name, BuildCommand buildCommand) {
        location.setY(location.getY() - 1);
        location.setX(location.getX() + 10);
        location.setZ(location.getZ() + 10);

        buildCommand.buildStructure(location, name, "0deg");
        saveStructure(name, location);
    }

    public static Optional<Location> getLocationStructureWhenIsBuilt(String name) {
        Type type = new TypeToken<Map<String, LocationDataModel>>() {}.getType();
        Map<String, LocationDataModel> file = Resources.getJsonByLocalData(path, type);
        Boolean exist = file != null && file.get(name) != null;

        if(!exist) return Optional.empty();

        Location loc = WorldUtils.getLocationByData(file.get(name));

        return Optional.of(loc);
    }

    public static void addTextLocationToShow(String aperture, Location location) {
        Integer X = (int) location.getX();
        Integer Y = (int) location.getY();
        Integer Z = (int) location.getZ();

        String messageCord = "X: " + X +" Y: "+ Y + " Z: " + Z;
        String msg = aperture + "\n" + messageCord;

        textToShow.add(msg);
    }

    private static void saveStructure(String name, Location location) {
        Type type = new TypeToken<Map<String, LocationDataModel>>() {}.getType();
        Map<String, LocationDataModel> file = Resources.getJsonByLocalData(path, type);
        LocationDataModel locationDataModel = new LocationDataModel();

        if(file == null) file = new TreeMap<>();

        locationDataModel.setWorldId(location.getWorld().getUID().toString());
        locationDataModel.setX(location.getX());
        locationDataModel.setY(location.getY());
        locationDataModel.setZ(location.getZ());

        file.put(name, locationDataModel);

        Resources.writeFile(path, file);
    }
}
