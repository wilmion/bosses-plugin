package com.wilmion.bossesplugin.mobsDificulties.boss;

import com.wilmion.bossesplugin.interfaces.IUltimateLambda;
import com.wilmion.bossesplugin.models.BoosesModel;
import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.models.metadata.EntityScoreboard;
import com.wilmion.bossesplugin.models.Perk;
import com.wilmion.bossesplugin.objects.metadata.MetadataModel;
import com.wilmion.bossesplugin.utils.Utils;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class SupportZombie extends BoosesModel {
    static final String idFollower = "PARENT-ID";
    static final String idSpecialFollower = "IS-SPECIAL-FOLLOWER-FROM-ZOMBIE-SUPPORT";
    private int spawnedZombies = 0;
    private boolean useUltimate1 = false;
    private boolean useUltimate2 = false;

    public SupportZombie(Location location, Plugin plugin) {
       super(location, plugin, 1);
   }

    private Zombie getBoss() {
       return (Zombie) this.entity;
   }

    @Override
    public void useSchedulerEvents() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this::usePassive, 50, 50);
    }

    @Override
    protected void equipBoss() {
        Zombie zombie = getBoss();

        zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.STICK));
    }

    @Override
    public void deadFunctionality() {
        Location location = entity.getLocation();

        super.deadFunctionality();

        world.spawn(location, TNTPrimed.class);
        world.spawn(location, TNTPrimed.class);
        world.spawn(location, TNTPrimed.class);

        if (Utils.getRandomInPercentage() <= 50) {
            server.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Perk.generatePerk(1, location, plugin), 100);
        }
    }

    private void usePassive() {
       if(spawnedZombies >= 4 || !this.isAlive()) return;

       BlockFace face = entity.getFacing();
       Location location = entity.getLocation();
       String entityID = String.valueOf(entity.getUniqueId());

       int modX = face.getModX() == 0 ? 1 : face.getModX();
       int modZ = face.getModZ() == 0 ? 1 : face.getModZ();

       double greaterValOnX = 5.0 * modX;
       double greaterValOnZ = 5.0 * modZ;

       location.setX(location.getX() + greaterValOnX);
       location.setZ(location.getZ() + greaterValOnZ);

       world.spawn(location, LightningStrike.class);
       Zombie follower = world.spawn(location, Zombie.class);

       PotionEffect fireResistence = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2000, 10);
       PotionEffect damageResistence = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40,12);

       follower.addPotionEffect(fireResistence);
       follower.addPotionEffect(damageResistence);
       follower.setRemoveWhenFarAway(false);
       follower.setTarget(getBoss().getTarget());

       EntityScoreboard.upsertScoreboard(follower, idFollower, entityID);

       spawnedZombies++;
   }

    public void playUltimate1() {
       Zombie boss = getBoss();

       BlockFace face = boss.getFacing();
       Location location = boss.getLocation();
       Entity target = boss.getTarget();

       if(target == null || !(target instanceof LivingEntity) || useUltimate1) return;

       this.useUltimate1 = true;

       int modX = face.getModX();
       int modZ = face.getModZ();

       double greaterValOnX = 8 * modX * -1;
       double greaterValOnZ = 8 * modZ * -1;

       location.setX(location.getX() + greaterValOnX);
       location.setZ(location.getZ() + greaterValOnZ);

       this.teleportZombie(location);

       int newXGreaterVal = modX * 3;
       int newZGreaterVal = modZ * 3;

       for (int i = 0; i < 3; i++) {
           final int index = i;

           Runnable task = () -> {
               Location locationSpawn = entity.getLocation();

               locationSpawn.setX(locationSpawn.getX() + newXGreaterVal);
               locationSpawn.setZ(locationSpawn.getZ() + newZGreaterVal);

               Location locationSpawn2 = locationSpawn;

               int greaterCordinate = 2 * index;

               if (newXGreaterVal != 0) locationSpawn.setZ(locationSpawn.getZ() + greaterCordinate);
               else locationSpawn.setX(locationSpawn.getX() + greaterCordinate);

               this.generateSpecialFollower(locationSpawn);

               if(index != 0) return;

               if (newXGreaterVal != 0) locationSpawn2.setZ(locationSpawn2.getZ() - greaterCordinate);
               else locationSpawn.setX(locationSpawn2.getX() - greaterCordinate);

               this.generateSpecialFollower(locationSpawn2);
               this.setTemporalInvunerability();
           };

           plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin , task , 20 * index);
       }
   }

    public void playUltimate2() {
       Zombie boss = getBoss();
       Entity target = boss.getTarget();

       if(target == null || !(target instanceof LivingEntity) || useUltimate2) return;

       this.useUltimate2 = true;

       Runnable handleHability = () -> {
           Location location = target.getLocation();

           int xMultiplier = this.getRandomDirection() * 5;
           int zMultiplier = this.getRandomDirection() * 5;

           location.setX(location.getX() + xMultiplier);
           location.setZ(location.getZ() + zMultiplier);

           this.teleportZombie(location);
           this.setTemporalInvunerability();

           Runnable handleSpawnSpecial = () -> {
               this.generateSpecialFollower(location);
           };

           plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, handleSpawnSpecial, 10);
       };

       for (int i = 0; i < 5; i++) {
           plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, handleHability, i * 20);
       }
   }

    private void generateSpecialFollower(Location location) {
        world.spawn(location, LightningStrike.class);
        Zombie zombie = world.spawn(location, Zombie.class);

        zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_AXE));

        PotionEffect fireResistence = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2000, 10);
        PotionEffect damageResistence = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40,12);

        zombie.addPotionEffect(fireResistence);
        zombie.addPotionEffect(damageResistence);
        zombie.setTarget(getBoss().getTarget());

        EntityScoreboard.upsertScoreboard(entity, idSpecialFollower, "YEAH");
    }

    private void teleportZombie(Location location) {
        world.playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);
        entity.teleport(location);
    }

    private int getRandomDirection() {
        int randomNumber = Utils.getRandomInPercentage();
        return randomNumber % 2 == 0? 1 : -1;
    }

    public void removeSpawnedZombies(int quantity) {
        this.spawnedZombies -= quantity;
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
       IUltimateLambda<SupportZombie> ultimate = (health, boss) -> {
           if(health <= boss.maxHealth * 0.5) boss.playUltimate1();
           if(health <= boss.maxHealth * 0.3) boss.playUltimate2();
       };

       Entity entity = event.getEntity();

       boolean isSupportedSpecialZombie = EntityScoreboard.getScoreboard(entity, idSpecialFollower).isPresent();
       boolean isFollower = EntityScoreboard.getScoreboard(entity, idFollower).isPresent();

       boolean continueAlth =  BoosesModel.handleDamageByEntity(event, 1, ultimate);

       return (isSupportedSpecialZombie || isFollower) ? false : continueAlth;
   }

    public static void handleDamage(EntityDamageEvent event) {
       BoosesModel.handleDamage(event, 1, () -> {});
   }

    public static void handleDead(EntityDeathEvent event) {
       BoosesModel.handleDead(event, 1);

       Entity entity = event.getEntity();

       Optional<MetadataModel> idParent = EntityScoreboard.getScoreboard(entity, idFollower);

       if(idParent.isEmpty()) return;

       Optional<SupportZombie> boss = BossesMetadata.getBoss(idParent.get().getValue());

       if(boss.isPresent()) boss.get().removeSpawnedZombies(1);
   }
}
