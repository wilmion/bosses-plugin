package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Utils {
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

    public static Player playerDamager(Entity damagerEntity) {
        Player result = null;

        Boolean isProjectile = damagerEntity instanceof Projectile;
        boolean isPlayer = damagerEntity instanceof Player;

        if(isPlayer) result = (Player) damagerEntity;

        if(isProjectile) {
            Projectile projectile = (Projectile) damagerEntity;

            isPlayer = projectile.getShooter() instanceof Player;

            if(!isPlayer) return null;

            damagerEntity = (Player) projectile.getShooter();
        }

        return result;
    }
}
