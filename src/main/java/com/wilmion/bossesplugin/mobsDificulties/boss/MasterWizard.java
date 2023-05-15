package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.*;

import java.util.Map;
import java.util.TreeMap;


public class MasterWizard extends BoosesModel {
    private static Map<String, MasterWizard> bosses = new TreeMap();
    static final String idMetadata = "WITCH-MASTER-BOSS";
    static final double maxHealth = 150.0;
    private boolean useUltimate1 = false;
    private boolean useUltimate2 = false;
    public MasterWizard(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth, "WITCH", idMetadata, "ZETANNA LA ERUDITA");

        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive1, 100, 100);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive2, 200, 200);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useDeserterPotion, 160, 160);
    }

    private Witch getWitch() {
        return (Witch) this.entity;
    }

    @Override
    public void deadFunctionality() {
        super.deadFunctionality();

        world.spawn(getWitch().getLocation(), LightningStrike.class);
        world.createExplosion(getWitch().getLocation(), 2f, false);

        int probability = Utils.getRandomInPercentage();

        if(probability > 50) return;

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> Perk.generatePerk(6, getWitch().getLocation(), plugin), 20);
    }

    /* === Passive Skills === */

    private void usePassive1() {
        Witch witch = getWitch();

        if(!isAlive()) return;

        Double incrementHealth = maxHealth * 0.12;
        Double newHealth = witch.getHealth() + incrementHealth;

        if(newHealth > maxHealth) newHealth = maxHealth;

        witch.setHealth(newHealth);
        world.playSound(witch.getLocation(), Sound.ENTITY_WITCH_DRINK, 2, 0);
    }

    private void usePassive2() {
        Witch witch = getWitch();

        if(witch.getHealth() < maxHealth * 0.5 || !isAlive()) return;

        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 20);

        witch.addPotionEffect(resistance);
        world.playSound(witch.getLocation(), Sound.ENTITY_WITCH_DRINK, 2, 0);
        world.playSound(witch.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 2, 0);
    }

    /* === Special Attack === */

    private void useDeserterPotion() {
        Entity target = getWitch().getTarget();

        if(target == null || !this.isAlive() ) return;

        Location location = target.getLocation().clone();

        Runnable consumer = () -> {
            world.spawn(location, LightningStrike.class);
            world.createExplosion(location, 1.5f, false);
        };

        server.getScheduler().scheduleSyncDelayedTask(plugin, consumer::run, 20);
    }

    /* === Ultimate === */

    public void useDangerRainUltimate() {
        Entity target = getWitch().getTarget();

        if(target == null || !this.isAlive() || useUltimate1) return;

        useUltimate1 = true;

        ActionRangeBlocks action = (location) -> {
            server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ThrownPotion potion = world.spawn(location, ThrownPotion.class);

                PotionData potionData = new PotionData(PotionType.POISON, false, true);

                PotionMeta metadata = potion.getPotionMeta();
                metadata.setBasePotionData(potionData);
                metadata.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 6000, 3), true);

                potion.setPotionMeta(metadata);
            }, 30);
        };

        Location location = target.getLocation().clone();

        Utils.executeActionInARangeOfBlock(4, 30, location, action);
    }

    public void useWizardGonnaUltimate() {
        Entity target = getWitch().getTarget();

        if(target == null || !this.isAlive() || useUltimate2) return;

        useUltimate2 = true;

        Runnable task = () -> {
            if(!this.isAlive()) return;

            Location location = target.getLocation();
            location.setY(location.getY() + 10);

            ThrownPotion potion = world.spawn(location, ThrownPotion.class);

            PotionData potionData = new PotionData(PotionType.INSTANT_DAMAGE, false, true);

            PotionMeta metadata = potion.getPotionMeta();
            metadata.setBasePotionData(potionData);
            metadata.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1), true);

            potion.setPotionMeta(metadata);
        };

        for(int i = 1; i <= 5; i++) {
            server.getScheduler().scheduleSyncDelayedTask(plugin, task::run, 30 * i);
        }
    }

    /* === Events === */

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda useUltimates = (health, entityID) -> {
            MasterWizard boss = bosses.get(entityID);

            if(health <= maxHealth * 0.5) boss.useDangerRainUltimate();
            if(health <= maxHealth * 0.25) boss.useWizardGonnaUltimate();
        };

        boolean continueAlth = BoosesModel.handleDamageByEntity(event, BarColor.WHITE, maxHealth, idMetadata, "WITCH", useUltimates);

        return continueAlth;
    }

    public static void handleEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        boolean isBoss = event.getEntity().hasMetadata(idMetadata);
        if(isBoss) event.setCancelled(true);
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "WITCH", BarColor.WHITE, maxHealth, idMetadata, () -> {});
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, idMetadata, bosses);
    }
}
