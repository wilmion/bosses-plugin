package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.models.metadata.EntityScoreboard;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.objects.metadata.MetadataModel;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.BiConsumer;

public class QueenBee extends BoosesModel {
    private static String scoreboardMinion = "SOLDIER-OF-QUEEN-SPIDER";
    protected Integer minions = 0;
    private boolean usedUltimate1 = false;

    public QueenBee(Location location, Plugin plugin) {
        super(location, plugin, 8);
    }

    private Bee getBee() {
        return (Bee) this.entity;
    }

    private void resetAgeAndStung() {
        getBee().setAge(0);
        getBee().setAgeLock(true);
        getBee().setHasNectar(false);
    }

    @Override
    protected void equipBoss() {
        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999,1);
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 9999999,4);
        PotionEffect damageIncrease = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999,2);

        getBee().addPotionEffect(damageResistance);
        getBee().addPotionEffect(damageIncrease);
        getBee().addPotionEffect(speed);
    }

    @Override
    public void useSchedulerEvents() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::resetAgeAndStung, 20, 20);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::supportToTheQueen, 40, 40);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::slowdown, 100, 100);
    }

    @Override
    public void deadFunctionality() {
        Location location = getBee().getLocation().clone();

        super.deadFunctionality();

        for (int i = 0; i < 4; i++) {
            Integer index = i;
            Runnable action = () -> {
                if(index == 3) Perk.generatePerk(8, getBee().getLocation(), plugin);
                else world.spawn(location, LightningStrike.class);
            };

            server.getScheduler().scheduleSyncDelayedTask(plugin, action, i * 15);
        }
    }

    /* === Passive Skills === */

    private void supportToTheQueen() {
        LivingEntity target = getBee().getTarget();
        Integer maxMinions = 6;

        if(!isAlive() || target == null || minions >= maxMinions) return;

        BlockFace face = getBee().getFacing();
        Location locToSpawn = getBee().getLocation().clone().add(face.getModX(), 0, face.getModZ());

        locToSpawn = WorldUtils.getLocationYInNearAir(locToSpawn, 9999);

        Bee minion = world.spawn(locToSpawn, Bee.class);
        AttributeInstance healthAttribute = minion.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        healthAttribute.setBaseValue(50.0);
        minion.setHealth(50.0);
        minion.setAge(0);
        minion.setAgeLock(true);
        minion.setTarget(target);
        EntityScoreboard.upsertScoreboard(minion, scoreboardMinion, getBee().getUniqueId().toString());
        minions++;
    }

    private void slowdown() {
        LivingEntity target = getBee().getTarget();

        if(!isAlive() || target == null) return;

        BlockData blockData = Bukkit.createBlockData(Material.HONEY_BLOCK);
        Location loc = target.getLocation().clone().add(0,-1,0);

        loc.getBlock().setBlockData(blockData);
    }

    /* === Special Attack === */

    public void fatalAttack(LivingEntity target) {
        Integer probability = Utils.getRandomInPercentage();

        if(probability > 30 || getBee().getHealth() > maxHealth * 0.8) return;

        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 200,2);
        PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 200,3);

        target.damage(10);
        target.addPotionEffect(slowness);
        target.addPotionEffect(poison);
    }

    /* === Ultimate === */

    private void spawnWardMinion(Location location) {
        Bee ward = world.spawn(location, Bee.class);
        AttributeInstance healthAttribute = ward.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        healthAttribute.setBaseValue(100.0);
        ward.setHealth(100.0);
        ward.setAge(0);
        ward.setAgeLock(true);
        EntityScoreboard.upsertScoreboard(ward, scoreboardMinion, "NONE");
        ward.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
    }

    public void theWards() {
        LivingEntity target = getBee().getTarget();

        if(!isAlive() || target == null || usedUltimate1) return;
        usedUltimate1 = true;

        List<Integer[]> posAvailable = new ArrayList<>(Arrays.asList(
                new Integer[]{3,3},
                new Integer[]{0,3},
                new Integer[]{-3,3},
                new Integer[]{3,0},
                new Integer[]{-3,0},
                new Integer[]{3,-3},
                new Integer[]{0,-3},
                new Integer[]{-3,-3}
        ));

        BiConsumer<Location, Integer> consumer = (loc, index) -> {
            server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                world.playSound(loc, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 2, 0);
                spawnWardMinion(loc);
            }, index * 10);
        };

        Utils.executeActionInPosition(posAvailable, target.getLocation(), consumer);
    }

    public void gonna(LivingEntity target) {
        if(getBee().getHealth() > maxHealth * 0.3) return;

        String[] potionEffects = { "POISON", "SLOW", "WEAKNESS", "HUNGER", "BLINDNESS", "CONFUSION", "HARM" };
        Random random = new Random();
        Integer indexPotion = random.nextInt(potionEffects.length);
        String potionName = potionEffects[indexPotion];
        PotionEffect potion = new PotionEffect(PotionEffectType.getByName(potionName), 80,1);

        target.damage(3);
        target.addPotionEffect(potion);
    }

    /* === Events === */

    private static void handleDamageByMinion(EntityDamageByEntityEvent event) {
        Boolean isMinion = EntityScoreboard.getScoreboard(event.getDamager(), scoreboardMinion).isPresent();
        String[] potionEffects = { "DAMAGE" , "POISON", "SLOW", "WEAKNESS", "HUNGER"};
        Random random = new Random();
        Entity entity = event.getEntity();
        Plugin plugin = BossesMetadata.plugin;

        if(!isMinion || !(entity instanceof LivingEntity)) return;

        String potionName = potionEffects[random.nextInt(potionEffects.length)];
        LivingEntity target = (LivingEntity) entity;
        Bee minion = (Bee) event.getDamager();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            minion.setTarget(target);
            minion.setHasStung(false);
            minion.setAge(0);
            minion.setAgeLock(true);
        }, 10);

        if(event.getFinalDamage() == 0.0) return;

        if(!potionName.equals("DAMAGE")) {
            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(potionName), 60,0);
            target.addPotionEffect(effect);
        } else target.damage(6);
    }
    private static void handleDamageByBoss(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Plugin plugin = BossesMetadata.plugin;

        if(!(entity instanceof LivingEntity) || !(event.getDamager() instanceof Bee)) return;

        Optional<QueenBee> boss = BossesMetadata.getBoss(event.getDamager().getUniqueId().toString());

        if(boss.isEmpty()) return;

        LivingEntity target = (LivingEntity) entity;
        Bee bossEntity = (Bee) event.getDamager();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            bossEntity.setTarget(target);
            bossEntity.setHasStung(false);
        }, 10);

        if(event.getFinalDamage() == 0.0) return;

        boss.get().fatalAttack(target);
        boss.get().gonna(target);
    }

    /* == NATIVE ==*/

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        handleDamageByMinion(event);
        handleDamageByBoss(event);
        IUltimateLambda<QueenBee> useUltimates = (health, boss) -> {
            if(health <= boss.maxHealth * 0.5) boss.theWards();
        };

        boolean continueAlth = BoosesModel.handleDamageByEntity(event, 8, useUltimates);

        return continueAlth;
    }

    public static void handleEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        BossDataModel bossData = getMetadata(8);
        boolean isBoss = event.getEntity().hasMetadata(bossData.getMetadata());
        if(isBoss) event.setCancelled(true);
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, 8, () -> {});
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, 8);

        Optional<MetadataModel> minionMtd = EntityScoreboard.getScoreboard(event.getEntity(), scoreboardMinion);

        if(minionMtd.isEmpty()) return;

        Optional<QueenBee> boss = BossesMetadata.getBoss(minionMtd.get().getValue());

        if(boss.isPresent()) boss.get().minions--;
    }
}
