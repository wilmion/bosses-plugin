package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.models.metadata.EntityScoreboard;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.utils.Utils;
import com.wilmion.bossesplugin.utils.WorldUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.*;

public class ExpertEnderman extends BoosesModel {
    private static String metadataMinion = "MINION-EXPERT-ENDERMAN";
    private Boolean usedUltimate = false;
    private Boolean usedUltimate2 = false;

    public ExpertEnderman(Location location, Plugin plugin) {
        super(location, plugin, 7);
    }

    private Enderman getEnderman() {
        return (Enderman) this.entity;
    }

    @Override
    public void useSchedulerEvents() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::revenge, 80, 80);
    }

    @Override
    public void deadFunctionality() {
        super.deadFunctionality();
        Location location = getEnderman().getLocation().clone();

        for(int i = 0; i < 8; i++) {
            Integer index = i;
            Runnable runnable = () -> {
                if(index == 0 || index == 5) world.spawn(location, LightningStrike.class);
                if(index == 6) Perk.generatePerk( 7, location, plugin);
                else world.playSound(location, Sound.ENTITY_ENDERMAN_HURT, 2, 0);
            };

            server.getScheduler().scheduleSyncDelayedTask(plugin, runnable, 30 * i);
        }
    }

    @Override
    protected void equipBoss() {
        PotionEffect damage = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999999, 1);
        PotionEffect reduceDamage = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 2);

        getEnderman().addPotionEffect(damage);
        getEnderman().addPotionEffect(reduceDamage);
    }

    public void cancelTeleportsThatNotAttacks(String cause) {
        String[] causes = { "LIGHTNING", "FIRE", "FIRE_TICK", "LAVA", "FALL" };
        Location location = getEnderman().getLocation().clone();

        if(!Arrays.stream(causes).anyMatch(c -> c.equals(cause))) return;

        getEnderman().setFireTicks(0);
        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> getEnderman().teleport(location), 5);
    }

    private void spawnMinion(Optional<String> displayName, Location location) {
        Enderman minion = world.spawn(location, Enderman.class);
        AttributeInstance health = minion.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        health.setBaseValue(10.0);
        minion.setHealth(10.0);
        minion.setTarget(getEnderman().getTarget());
        EntityScoreboard.upsertScoreboard(minion, metadataMinion, "true");

        if(displayName.isEmpty()) return;

        minion.setCustomNameVisible(true);
        minion.setCustomName(displayName.get());
    }

    /* === Passive Skills === */

    public void sweetOrTrick() {
        LivingEntity target = getEnderman().getTarget();

        if(!isAlive() || target == null) return;

        BlockFace face = target.getFacing().getOppositeFace();
        Location newLocation = target.getLocation().clone().add(face.getModX() * 2,0, face.getModZ() * 2);

        world.playSound(getEnderman().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);
        getEnderman().teleport(newLocation);
        getEnderman().setTarget(target);
        traceOfTheExpert();
    }

    public void traceOfTheExpert() {
        LivingEntity target = getEnderman().getTarget();
        Integer probability = Utils.getRandomInPercentage();

        if(!isAlive() || target == null || probability > 50) return;

        world.playSound(getEnderman().getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2, 0);
        spawnMinion(Optional.empty(), getEnderman().getLocation());
    }

    /* === Special Attack === */

    private Location generateRandomLocation(Location loc) {
        Location result = loc.clone();

        Random random = new Random();
        Integer randomXMultiplier = random.nextBoolean() ? 1 : -1; // Generate a random number that can be 1 or -1
        Integer randomX = random.nextInt(20 - 10 + 1) + 10; // Generate a random number within 10-20
        Integer randomZMultiplier = random.nextBoolean() ? 1 : -1; // Generate a random number that can be 1 or -1
        Integer randomZ = random.nextInt(20 - 10 + 1) + 10; // Generate a random number within 10-20

        result = result.add(randomX * randomXMultiplier, 0, randomZ * randomZMultiplier);
        result = WorldUtils.getLocationYInNearAir(result, 9999);

        return result;
    }

    private void revengeTNT(LivingEntity target) {
        BlockData tnt = Bukkit.createBlockData(Material.TNT);

        getEnderman().setCarriedBlock(tnt);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(!isAlive() || !target.isValid()) return;

            Location location = target.getLocation();

            world.playSound(getEnderman().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);
            world.createExplosion(location, 3f);
            getEnderman().teleport(location);
            getEnderman().setCarriedBlock(null);

            world.playSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 2 , 0);
            world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);
            getEnderman().teleport(generateRandomLocation(location));
            getEnderman().setTarget(target);
        }, 40);
    }

    private void revengePotion(LivingEntity target) {
        BlockData potionBlock = Bukkit.createBlockData(Material.COPPER_BLOCK);

        getEnderman().setCarriedBlock(potionBlock);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(!isAlive() || !target.isValid()) return;

            Location loc = target.getLocation();

            getEnderman().setCarriedBlock(null);
            getEnderman().teleport(loc);
            world.playSound(getEnderman().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);

            ThrownPotion potion = world.spawn(loc.clone().add(0, 4, 0), ThrownPotion.class);
            PotionData potionData = new PotionData(PotionType.WEAKNESS, false, false);
            PotionMeta metadata = potion.getPotionMeta();

            metadata.setBasePotionData(potionData);
            metadata.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), true);
            potion.setPotionMeta(metadata);

            world.playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, 2 , 0);
            world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 2 , 0);
            getEnderman().teleport(generateRandomLocation(loc));
            getEnderman().setTarget(target);
        }, 40);
    }

    private void revenge() {
        LivingEntity target = getEnderman().getTarget();
        Integer probability = Utils.getRandomInPercentage();

        if(!isAlive() || target == null || getEnderman().getHealth() > maxHealth * 0.8) return;
        if(probability > 90) return;

        Integer action = Utils.getRandomInPercentage();

        if(action < 30) revengeTNT(target);
        else revengePotion(target);
    }

    /* === Ultimate === */

    private void executeInPositions(Consumer<Location> consumer, List<Integer[]> posAvailable, Entity target) {
        Random random = new Random();

        for (int i = 0; i < posAvailable.size(); i++) {
            Integer index = random.nextInt(posAvailable.size());
            Integer[] dataPos = posAvailable.get(index);
            Location loc = target.getLocation().clone();

            loc.add(dataPos[0], 0, dataPos[1]);
            loc = WorldUtils.getLocationYInNearAir(loc, 9999);
            posAvailable.remove(index);

            Location finalLoc = loc;
            Runnable action = () -> {
                setTemporalInvunerability();
                getEnderman().teleport(finalLoc);
                world.playSound(finalLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);
                consumer.accept(finalLoc);
            };

            server.getScheduler().scheduleSyncDelayedTask(plugin, action, i * 10);
        }
    }

    public void shadows() {
        LivingEntity target = getEnderman().getTarget();

        if(!isAlive() || target == null || usedUltimate) return;
        usedUltimate = true;

        List<Integer[]> posAvailable = new ArrayList<>(Arrays.asList(
                new Integer[]{4,4},
                new Integer[]{0,4},
                new Integer[]{-4,4},
                new Integer[]{4,0},
                new Integer[]{-4,0},
                new Integer[]{4,-4},
                new Integer[]{0,-4},
                new Integer[]{-4,-4}
        ));
        Consumer<Location> consumer = (loc) -> spawnMinion(Optional.of(getEnderman().getCustomName()), loc);

        executeInPositions(consumer, posAvailable, target);
    }

    public void yourFinal() {
        LivingEntity target = getEnderman().getTarget();

        if(!isAlive() || target == null || usedUltimate2) return;
        usedUltimate2 = true;

        List<Integer[]> posAvailable = new ArrayList<>(Arrays.asList(new Integer[]{4,4}, new Integer[]{-4,4}, new Integer[]{4,-4}, new Integer[]{-4,-4}));
        Consumer<Location> consumer = (loc) -> {
            Fireball igniteCharge = getEnderman().launchProjectile(Fireball.class);
            Vector direction = target.getLocation().subtract(loc).toVector().normalize();

            world.playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, 2, 0);
            igniteCharge.setDirection(direction);
            igniteCharge.setYield(2.0f);
            igniteCharge.setIsIncendiary(true);

        };

        executeInPositions(consumer, posAvailable, target);
    }

    /* === Events === */

    public static void handleTeleport(EntityTeleportEvent event) {
        BossDataModel bossData = BoosesModel.getMetadata(7);
        Entity entity = event.getEntity();
        Location from = event.getFrom();
        Boolean isLiquid = from.getBlock().isLiquid();
        Boolean isBoss = EntityScoreboard.getScoreboard(entity, bossData.getMetadata()).isPresent();

        if(!isBoss || isLiquid) return;

        event.setCancelled(true);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda<ExpertEnderman> handleDamageActions = (health, boss) -> {
            boss.cancelTeleportsThatNotAttacks(event.getCause().name());

            if(Utils.getRandomInPercentage() <= 80) boss.sweetOrTrick();
            if(health <= boss.maxHealth * 0.5) boss.shadows();
            if(health <= boss.maxHealth * 0.2) boss.yourFinal();
        };

        boolean continueAlth = BoosesModel.handleDamageByEntity(event, 7, handleDamageActions);

        return continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        String[] causesForNotDamage = { "BLOCK_EXPLOSION", "ENTITY_EXPLOSION" };
        String causeName = event.getCause().name();
        Boolean isCancellable = Arrays.stream(causesForNotDamage).anyMatch(c -> c.equals(causeName));
        Boolean isMinion = EntityScoreboard.getScoreboard(event.getEntity(), metadataMinion).isPresent();

        Runnable action = () -> {
            Optional<ExpertEnderman> boss = BossesMetadata.getBoss(String.valueOf(event.getEntity().getUniqueId()));

            if(isCancellable) event.setCancelled(true);
            if(boss.isPresent()) boss.get().cancelTeleportsThatNotAttacks(event.getCause().name());
        };

        BoosesModel.handleDamage(event, 7, action);

        if(isMinion && isCancellable) event.setCancelled(true);
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, 7);
    }
}
