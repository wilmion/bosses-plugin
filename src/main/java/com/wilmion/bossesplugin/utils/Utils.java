package com.wilmion.bossesplugin.utils;

import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.KeepMetadata;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
        KeepMetadata.addBlockWithMetadata(blockState.getBlock(), key);
    }

    public static int getRandomInPercentage() {
        Random random = new Random();

        int percentage = random.nextInt(101);

        return  percentage;
    }

    public static int getRandomNumberForSpace() {
        Random random = new Random();

        return random.nextInt(3) - 1;
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

    public static void setTitleOnPlayer(Player player, String title, String subtitle) {
        Component titleComponent = Component.text(title);
        Component subtitleComponent = Component.text(subtitle);
        Title titleToShow = Title.title(titleComponent, subtitleComponent);

        player.showTitle(titleToShow);
    }

    public static double getHealthByDamage(double damage, double health) {
        return health - damage;
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

    public static <T> T livingDamager(Entity damagerEntity, Class<T> clazz) {
        T result = null;

        Boolean isProjectile = damagerEntity instanceof Projectile;
        boolean isEntity = clazz.isInstance(damagerEntity);

        if(isEntity) result = clazz.cast(damagerEntity);

        if(isProjectile) {
            Projectile projectile = (Projectile) damagerEntity;
            isEntity = clazz.isInstance(projectile.getShooter());

            if(!isEntity) return null;

            result = clazz.cast(projectile.getShooter());
        }

        return result;
    }

    public static LivingEntity livingDamager(Entity damagerEntity) {
        return livingDamager(damagerEntity, LivingEntity.class);
    }
}
