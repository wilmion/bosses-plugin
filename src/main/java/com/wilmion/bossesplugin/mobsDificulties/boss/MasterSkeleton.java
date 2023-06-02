package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.interfaces.utils.ActionRangeBlocks;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.models.metadata.EntityScoreboard;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.objects.boss.BossDataModel;
import com.wilmion.bossesplugin.utils.Utils;

import com.wilmion.bossesplugin.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Optional;

public class MasterSkeleton extends BoosesModel {
    static String idMetadataMinion = "SKELETON_BOSS_MINION";
    private boolean useUltimate1 = false;

    public MasterSkeleton(Location location) {
        super(location, 2);
    }

    private Skeleton getBoos() {
        return (Skeleton) this.entity;
    }

    @Override
    public void useSchedulerEvents() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::usePassive, 120, 120);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useATQE1, 140, 100);
        server.getScheduler().scheduleSyncRepeatingTask(plugin, this::useATQE2, 200, 300);
    }

    @Override
    protected void equipBoss() {
        Skeleton boss = getBoos();

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
        super.deadFunctionality();
        Location location = entity.getLocation();

        Integer probability = Utils.getRandomInPercentage();

        world.spawn(location, LightningStrike.class);
        world.createExplosion(location, 2F, false);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            world.spawn(location, LightningStrike.class);
            world.createExplosion(location, 2F, false);
        }, 30);

        if(probability <= 50) server.getScheduler().scheduleSyncDelayedTask(plugin, () -> Perk.generatePerk(2, location), 60);
    }

    private void useATQE1() {
        Entity target = getBoos().getTarget();
        Integer probability = Utils.getRandomInPercentage();

        if(target == null || !this.isAlive() || probability <= 40) return;

        ActionRangeBlocks action = (location) -> {
            server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                world.spawnArrow(location, new Vector(0,-1,0), 8.0f, 0);
            }, 10);

            return true;
        };

        setTemporalInvunerability();
        Utils.executeActionInARangeOfBlock(3, 30, target.getLocation(), action);
    }

    private void useATQE2() {
        LivingEntity target = getBoos().getTarget();

        if(target == null || !this.isAlive()) return;

        useIlusion(3, -3, target);
        useIlusion(3, 3, target);
        useIlusion(-3, -3, target);
        useIlusion(-3, 3, target);
    }

    private void usePassive() {
        Entity target = getBoos().getTarget();

        if(!isAlive() || target == null) return;

        setTemporalInvunerability();
        lightingPassive(2, 0);
        lightingPassive(-2, 0);
        lightingPassive(0, 2);
        lightingPassive(0, -2);
    }

    public void useUltimate1() {
        BlockFace face = getBoos().getFacing();

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

        EntityScoreboard.upsertScoreboard(skeleton, idMetadataMinion, "true");

        return skeleton;
    }

    private void summonMinionHorse(Location location) {
        SkeletonHorse horse = world.spawn(location, SkeletonHorse.class);

        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2000, 10);
        PotionEffect damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80,12);

        horse.addPotionEffect(fireResistance);
        horse.addPotionEffect(damageResistance);

        Skeleton skeleton = this.summonMinion(location);

        horse.addPassenger(skeleton);
        horse.setTamed(true);

        EntityScoreboard.upsertScoreboard(horse, idMetadataMinion, "true");
    }

    private void useJinet(double x, double z) {
        Location location = getBoos().getLocation().clone();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);
        location = WorldUtils.getLocationYInNearAir(location, 1000);

        world.spawn(location, LightningStrike.class);

        summonMinionHorse(location);
    }

    private void lightingPassive(int x, int z) {
        Location location = entity.getLocation();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        world.spawn(location, LightningStrike.class);
    }

    private void useIlusion(int x, int z, LivingEntity entity) {
        Location location = entity.getLocation();

        location.setX(location.getX() + x);
        location.setZ(location.getZ() + z);

        world.spawn(location, LightningStrike.class);
        Illusioner ilusioner = world.spawn(location, Illusioner.class);
        ilusioner.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        ilusioner.setTarget(entity);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> ilusioner.damage(100), 50);
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
        IUltimateLambda<MasterSkeleton> ultimateLambda = (health, boss) -> {
            if(health <= boss.maxHealth / 2.0) boss.useUltimate1();
        };

        boolean isMinion = EntityScoreboard.getScoreboard(event.getEntity(), idMetadataMinion).isPresent();
        boolean continueAlth = BoosesModel.handleDamageByEntity(event, 2, ultimateLambda);

        return isMinion? false : continueAlth;
    }

    public static void handleDamage(EntityDamageEvent event) {
        BoosesModel.handleDamage(event, 2, () -> {});
    }

    public static void handleDead(EntityDeathEvent event) {
        BoosesModel.handleDead(event, 2);
    }

    public static void handleShoot(EntityShootBowEvent event) {
        BossDataModel bossData = BoosesModel.getMetadata(2);
        Entity entity = event.getEntity();
        Entity projectile = event.getProjectile();
        String entityID = String.valueOf(entity.getUniqueId());

        boolean isSkeleton = entity instanceof Skeleton;
        boolean isArrow = projectile instanceof Arrow;
        boolean isBoss = EntityScoreboard.getScoreboard(entity, bossData.getMetadata()).isPresent();

        if(!isSkeleton || !isArrow || !isBoss) return;

        Double health = ((Skeleton) entity).getHealth();
        Arrow arrow = (Arrow) projectile;
        Optional<MasterSkeleton> boss = BossesMetadata.getBoss(entityID);

        if(health <= bossData.getHealth() * 0.3 && boss.isPresent()) boss.get().useUltimate2(arrow);
    }

}
