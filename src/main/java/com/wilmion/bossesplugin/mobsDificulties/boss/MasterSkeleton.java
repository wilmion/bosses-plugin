package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.TreeMap;

public class MasterSkeleton extends BoosesModel {
    static Map<String, MasterSkeleton> bosses = new TreeMap<>();
    static double maxHealth = 150.0;
    static String idMetadata = "SKELETON_BOSS";
    static String idMetadataMinion = "SKELETON_BOSS_MINION";
    private boolean useUltimate1 = false;

    public MasterSkeleton(Player player, Location location, Plugin plugin) {
        super(player, location, plugin, maxHealth, "SKELETON", idMetadata, "ANN LA MAESTRA");

        String entityID = String.valueOf(this.entity.getEntityId());
        bosses.put(entityID, this);

        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 120, 120);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useATQE1, 140, 100);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useATQE2, 200, 300);
    }
    private Skeleton getBoos() {
        return (Skeleton) this.entity;
    }

    @Override
    protected void equipBoss() {
        Skeleton boss = this.getBoos();

        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack bow = new ItemStack(Material.BOW);

        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 6);
        bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 3);

        boss.getEquipment().setHelmet(helmet);
        boss.getEquipment().setItemInMainHand(bow);
    }

    @Override
    public void deadFunctionality() {
        Location location = this.entity.getLocation();

        int probability = Utils.getRandomInPercentage();

        super.deadFunctionality();

        world.spawn(location, LightningStrike.class);
        world.createExplosion(location, 2F, false);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            world.spawn(location, LightningStrike.class);
            world.createExplosion(location, 2F, false);
        }, 30);

        if(probability <= 50) server.getScheduler().scheduleSyncDelayedTask(plugin, () -> Perk.generatePerk(2, location, plugin), 60);
    }

    private void useATQE1() {
        int probability = Utils.getRandomInPercentage();
        Player player = this.getTarget();

        if(player == null || !this.isAlive() || probability <= 40) return;

        final int range = 3;

        this.setTemporalInvunerability();

        ActionRangeBlocks action = (location) -> {
            server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                world.spawnArrow(location, new Vector(0,-1,0), 8.0f, 0);
            }, 10);
        };

        Utils.executeActionInARangeOfBlock(range, 30, player.getLocation(), action);
    }

    private void useATQE2() {
        Player player = this.getTarget();

        if(player == null || !this.isAlive()) return;

        this.useIlusion(3, -3, player);
        this.useIlusion(3, 3, player);
        this.useIlusion(-3, -3, player);
        this.useIlusion(-3, 3, player);
    }

    private void usePassive() {
        Skeleton boss = this.getBoos();

        if(!this.isAlive()) return;

        this.setTemporalInvunerability();

        this.lightingPassive(2, 0, boss);
        this.lightingPassive(-2, 0, boss);
        this.lightingPassive(0, 2, boss);
        this.lightingPassive(0, -2, boss);
    }

    public void useUltimate1() {
        BlockFace face = this.getBoos().getFacing();

        if(useUltimate1) return;

        useUltimate1 = true;
        
        this.setTemporalInvunerability();

        int modX = face.getModX();
        int modZ = face.getModZ();

        double greaterValOnX = 3 * modZ;
        double greaterValOnZ = 3 * modX;
        
        this.useJinet(greaterValOnX, greaterValOnZ);
        this.useJinet(greaterValOnX, greaterValOnZ);
        this.useJinet(greaterValOnX * -1, greaterValOnZ * -1);
        this.useJinet(greaterValOnX * -1, greaterValOnZ * -1);
    }

    public void useUltimate2(Arrow arrow) {
        int probability = Utils.getRandomInPercentage();

        PotionEffect potion = new PotionEffect(PotionEffectType.POISON, 160 , 2);
        PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 100, 2);
        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 200, 1);
        PotionEffect damage = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 1);

        PotionEffect effect;

        if(probability >= 0 && probability <= 25) effect = potion;
        else if(probability >= 26 && probability <= 50) effect = weakness;
        else if(probability >= 51 && probability <= 75) effect = slowness;
        else effect = damage;

        arrow.addCustomEffect(effect, true);
    }

    private Skeleton summonMinion(Location location) {
        Skeleton skeleton = world.spawn(location, Skeleton.class);

        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2000, 10);
        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40,12);

        skeleton.addPotionEffect(fireResistance);
        skeleton.addPotionEffect(damageResistance);

        skeleton.setMetadata(idMetadataMinion, new FixedMetadataValue(plugin, "true"));

        return skeleton;
    }

    private void summonMinionHorse(Location location) {
        SkeletonHorse horse = world.spawn(location, SkeletonHorse.class);

        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2000, 10);
        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80,12);

        horse.addPotionEffect(fireResistance);
        horse.addPotionEffect(damageResistance);

        horse.setMetadata(idMetadataMinion, new FixedMetadataValue(plugin, "true"));

        Skeleton skeleton = this.summonMinion(location);

        horse.addPassenger(skeleton);
        horse.setTamed(true);
    }


    private void useJinet(double x, double z) {
        Location location = getBoos().getLocation();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        this.summonMinionHorse(location);
        world.spawn(location, LightningStrike.class);
    }

    private void lightingPassive(int x, int z, Skeleton boss) {
        Location location = boss.getLocation();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        world.spawn(location, LightningStrike.class);
    }

    private void useIlusion(int x, int z, Player player) {
        Location location = player.getLocation();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        world.spawn(location, LightningStrike.class);
        Illusioner ilusioner = world.spawn(location, Illusioner.class);
        ilusioner.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> ilusioner.damage(100), 50);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda ultimateLambda = (health, bossID) -> {
            MasterSkeleton boss = bosses.get(bossID);

            if(health <= maxHealth / 2.0) {
                boss.useUltimate1();
            }
        };

        boolean isMinion = event.getEntity().hasMetadata(idMetadataMinion);
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, BarColor.WHITE, maxHealth, idMetadata, "SKELETON", ultimateLambda);

        return isMinion? false : continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, "SKELETON", BarColor.WHITE, maxHealth, idMetadata, null);
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, idMetadata, bosses);
    }

    public static void handleShoot(EntityShootBowEvent event) {
        Entity entity = event.getEntity();
        Entity projectile = event.getProjectile();

        String entityID = String.valueOf(entity.getEntityId());

        boolean isSkeleton = entity instanceof Skeleton;
        boolean isArrow = projectile instanceof Arrow;
        boolean isBoss = entity.hasMetadata(idMetadata);

        if(!isSkeleton || !isArrow || !isBoss) return;

        double health = ((Skeleton) entity).getHealth();
        Arrow arrow = (Arrow) projectile;
        MasterSkeleton boss = bosses.get(entityID);

        if(health <= maxHealth * 0.3) {
            boss.useUltimate2(arrow);
        }

    }

}
