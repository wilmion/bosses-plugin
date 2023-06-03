package com.wilmion.bossesplugin.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class EventUtils {
    public static double getHealthByDamage(double damage, double health) {
        return health - damage;
    }

    public static Boolean isDeadEntityOnDamage(Entity entity, double damage, EntityType MobType) {
        if(!(entity instanceof LivingEntity)) return false;

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
