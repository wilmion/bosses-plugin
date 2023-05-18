package com.wilmion.bossesplugin.generation;

import com.google.common.reflect.TypeToken;

import com.wilmion.bossesplugin.commands.BuildCommand;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class StructureGeneration {
    private static Location locationStructure;
    private static String path = "plugins/bosses-plugin-data/game-data/structures.json";

    private BuildCommand buildCommand;
    private Boolean isJordiBuilt = false;
    private Integer XPosGeneration = 0;
    private Integer ZPossGeneration = 0;

    public StructureGeneration(Plugin plugin) {
        Type type = new TypeToken<Map<String, LocationDataModel>>() {}.getType();
        Map<String, LocationDataModel> file = Resources.getJsonByLocalData(path, type);
        Boolean isJordiBuilt = file != null && file.get("JORDI_BUILT") != null;

        this.buildCommand = new BuildCommand(plugin);
        this.isJordiBuilt = isJordiBuilt;
        this.generateStructure();

        if(!isJordiBuilt) return;

        locationStructure = WorldUtils.getLocationByData(file.get("JORDI_BUILT"), plugin);
    }

    public void generateStructure() {
        if(isJordiBuilt) return;

        locationStructure = getLocationIteration();

        if(!verifyRange(locationStructure.clone())) locationStructure = getLocationIteration();

        generateBuild(locationStructure);
        isJordiBuilt = true;
    }

    private Boolean verifyRange(Location location) {
        AtomicReference<Boolean> isValid = new AtomicReference<>(true);
        location.setY(location.getY() - 2);

        ActionRangeBlocks rangeBlocks = (loc) -> {
            Boolean isAir = loc.getBlock().getType().toString().equals("AIR");

            if(isAir) isValid.set(false);
            return isAir;
        };

        Utils.executeActionInXOfBlocks(120, 1, 120, location, rangeBlocks);

        return isValid.get();
    }

    private void generateBuild(Location location) {
        Map<String, LocationDataModel> file = new TreeMap<>();

        WorldUtils.cleanInRange(location.clone(), 120, 120);

        location.setY(location.getY() - 1);
        location.setX(location.getX() + 10);
        location.setZ(location.getZ() + 10);

        LocationDataModel locationDataModel = new LocationDataModel();
        locationDataModel.setWorldId(location.getWorld().getUID().toString());
        locationDataModel.setX(location.getX());
        locationDataModel.setY(location.getY());
        locationDataModel.setZ(location.getZ());

        file.put("JORDI_BUILT", locationDataModel);

        Resources.writeFile(path, file);
        buildCommand.buildStructure(location, "jordi_tower", "0deg");
    }

    private Location getLocationIteration() {
        World world = Bukkit.getWorlds().get(0);
        Integer seaLevel = world.getSeaLevel() - 1;

        return new Location(world, XPosGeneration++, seaLevel, ZPossGeneration++);
    }

    public static void sendPlayerMessageLocation(Player player) {
        String content = "Mi imperio es enormemente vasto, no hay rival que se le compare, ¡Absolutamente nadie!\nMi imperio se extiende por:\n[LOCATION]";

        Integer X = (int) locationStructure.getX();
        Integer Y = (int) locationStructure.getY();
        Integer Z = (int) locationStructure.getZ();

        String messageCord = "X: " + X +" Y: "+ Y + " Z: " + Z;
        String msg = content.replace("[LOCATION]", messageCord);

        player.sendMessage(Component.text(ChatColor.BOLD + msg));
    }
}
