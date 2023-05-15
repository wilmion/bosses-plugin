package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class Utils {
    public static Optional<MetadataValue> getMetadataValue(String key, BlockState blockState) {
        List<MetadataValue> metadataList = blockState.getMetadata(key);
        MetadataValue result = null;

        if (metadataList != null && !metadataList.isEmpty()) {
            for (MetadataValue metadataValue : metadataList) {
                result = metadataValue;
            }
        }

        return Optional.ofNullable(result);
    }

    public static void setMetadataValue(String key, Object value, BlockState blockState, Plugin plugin) {
        blockState.setMetadata(key, new FixedMetadataValue(plugin, value));
    }
    public static double getRandom() {
        double range = Math.random() * 10 + 1;

        return range;
    }

    public static int getRandomIntNumber() {
        int value = (int) getRandom();

        return value;
    }

    public static int getRandomInPercentage() {
        Random random = new Random();

        int percentage = random.nextInt(101);

        return  percentage;
    }

    public static int getRandomNumberForSpace() {
        Random random = new Random();

        int randomNUmber = random.nextInt(3) - 1;

        return randomNUmber;
    }

    public static void executeActionInARangeOfBlock(int range, int modY, Location locationParam, ActionRangeBlocks lambda) {
        for (int x = range * -1; x <= range ; x++) {
            for (int z = range; z >= range * -1 ; z--) {
                Location location = locationParam.clone();

                location.setX(location.getX() + x);
                location.setZ(location.getZ() + z);
                location.setY(location.getY() + modY);

                lambda.action(location);
            }
        }
    }

    public static void executeActionInXOfBlocks(int rangeX, int rangeY, int rangeZ, Location location, ActionRangeBlocks actionRangeBlocks) {
        for (int x = 1; x <= rangeX ; x++) {
            for (int y = 1; y <= rangeY; y++) {
                for (int z = 1; z <= rangeZ; z++) {
                    Location loc = location.clone();

                    loc.setX(loc.getX() + x);
                    loc.setZ(loc.getZ() + z);
                    loc.setY(loc.getY() + y);

                    actionRangeBlocks.action(loc);
                }
            }
        }
    }

    public static void removeKnockback(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity mainer = event.getEntity();

        Vector velocity = damager.getLocation().getDirection().setY(0).normalize().multiply(-2);

        mainer.setVelocity(velocity);
    }

    public static void setTitleOnPlayer(Player player, String title, String subtitle) {
        Component titleComponent = Component.text(title);
        Component subtitleComponent = Component.text(subtitle);

        Title titleToShow = Title.title(titleComponent, subtitleComponent);

        player.showTitle(titleToShow);
    }

    public static String readFile(String path) {
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);

            String data = "";

            while (reader.hasNextLine()) {
                data += "\n" + reader.nextLine();
            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFile(String path, String content) {
        try {
            File file = new File(path);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getHealthByDamage(double damage, double health) {
        double currentHealth = health - damage;

        return currentHealth;
    }

    public static Boolean isDeadEntityOnDamage(Entity entity, double damage, EntityType MobType) {
        LivingEntity livingEnt = (LivingEntity) entity;

        double currentHealth = getHealthByDamage(damage, livingEnt.getHealth());
        Boolean isMob = entity.getType() == MobType;

        return isMob && currentHealth <= 0.00;
    }

    public static Boolean isDamageType(String causeName, String DAMAGE) {
        Boolean isValidCause = causeName.equals(DAMAGE);

        return isValidCause;
    }

    public static LivingEntity livingDamager(Entity damagerEntity) {
        LivingEntity result = null;

        Boolean isProjectile = damagerEntity instanceof Projectile;
        boolean isEntity = damagerEntity instanceof LivingEntity;

        if(isEntity) result = (LivingEntity) damagerEntity;

        if(isProjectile) {
            Projectile projectile = (Projectile) damagerEntity;

            isEntity = projectile.getShooter() instanceof LivingEntity;

            if(!isEntity) return null;

            damagerEntity = (LivingEntity) projectile.getShooter();
        }

        return result;
    }

    public static Player playerDamager(Entity damagerEntity) {
        Player result = null;

        Boolean isProjectile = damagerEntity instanceof Projectile;
        boolean isEntity = damagerEntity instanceof Player;

        if(isEntity) result = (Player) damagerEntity;

        if(isProjectile) {
            Projectile projectile = (Projectile) damagerEntity;

            isEntity = projectile.getShooter() instanceof Player;

            if(!isEntity) return null;

            damagerEntity = (Player) projectile.getShooter();
        }

        return result;
    }
}
