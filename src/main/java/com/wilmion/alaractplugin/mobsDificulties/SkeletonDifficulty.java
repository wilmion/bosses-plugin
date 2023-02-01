package com.wilmion.alaractplugin.mobsDificulties;

import com.wilmion.alaractplugin.utils.Utils;
import com.wilmion.alaractplugin.mobsDificulties.boss.MasterSkeleton;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SkeletonDifficulty implements Listener {
    Plugin plugin;
    World world;
    Server server;

    public SkeletonDifficulty(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void OnShootByBow(EntityShootBowEvent event) {
        MasterSkeleton.handleShoot(event);
    }

    @EventHandler()
    public void OnDead(EntityDeathEvent event) {
        MasterSkeleton.handleDead(event);
    }

    @EventHandler()
    public void OnDamage(EntityDamageEvent event) {
        MasterSkeleton.handleDamage(event);
    }

    @EventHandler()
    public void OnDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        world = entity.getWorld();
        server = entity.getServer();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.SKELETON);
        Boolean isAttack = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        Boolean isContinueBoss = MasterSkeleton.handleDamageByEntity(event);

        if(!isDead || !isAttack || !isContinueBoss) return;

        int probability = Utils.getRandomInPercentage();

        Location location = entity.getLocation();

        if(probability <= 5) {
            new MasterSkeleton((Player) event.getDamager(), location, this.plugin);
            return;
        }

        if(probability <= 15) {
            int nSkeletons = Utils.getRandomIntNumber();
            int nWhiterSkeletons = Utils.getRandomIntNumber();

            spawnSkeletons(nSkeletons, location);
            spawnWhitersSkeletons(nWhiterSkeletons, location);
            server.broadcast("Osas retar a los venecos >:v", "all");
        }
    }

    private void spawnLigthing(Location location) {
        if(world == null) return;

        world.spawnEntity(location, EntityType.LIGHTNING);
    }

    private void spawnSkeletons(int numbersToSpawn, Location location) {
        Runnable runnable = () -> {
            Skeleton skeleton = world.spawn(location, Skeleton.class);

            PotionEffect fireResistence = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2000, 10);
            PotionEffect damageResistence = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200,12);

            skeleton.addPotionEffect(fireResistence, true);
            skeleton.addPotionEffect(damageResistence, true);
        };

        for(int i = 0; i <= numbersToSpawn; i++) {
            int delay = 40 * i;
            server.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        }

    }
    private  void spawnWhitersSkeletons(int numbersToSpawn, Location location) {
        Runnable runnable = () -> {
            spawnLigthing(location);
            world.spawnEntity(location, EntityType.WITHER_SKELETON);
        };

        for(int i = 0; i <= numbersToSpawn; i++) {
            int delay = 40 * i;
            server.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        }

    }
}
