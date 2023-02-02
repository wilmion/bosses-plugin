package com.wilmion.alaractplugin.mobsDificulties.boss;

import com.wilmion.alaractplugin.interfaces.IUltimateLambda;
import com.wilmion.alaractplugin.models.BoosesModel;

import com.wilmion.alaractplugin.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.TreeMap;

public class SupportZombie extends BoosesModel {
    static Map<String, SupportZombie> bosses = new TreeMap<>();
    static final String idMetadata = "SUPPORT_ZOMBIE_BOSS";
    static final String idFollower = "PARENT-ID";
    static final  String idSpecialFollower = "IS-SPECIAL-FOLLOWER-FROM-ZOMBIE-SUPPORT";

    static final double maxHealth = 100.0;
    private int spawnedZombies = 0;
    private boolean useUltimate1 = false;
    private boolean useUltimate2 = false;
    public SupportZombie(Player player, Location location, Plugin plugin) {
       super(player, location, plugin, maxHealth, "ZOMBIE", idMetadata, "Escencia de los condenados", "JORDI EL IRRESISTIBLE");

       this.colorTextPerk = ChatColor.YELLOW;
       this.materialPerk = Material.YELLOW_DYE;

       plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this::usePassive, 50, 50);

       String entityID = String.valueOf(this.entity.getEntityId());

       bosses.put(entityID, this);
   }

    private Zombie getBoss() {
       return (Zombie) this.entity;
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
        Location location = this.entity.getLocation();
        world.spawn(location, TNTPrimed.class);
        world.spawn(location, TNTPrimed.class);
        world.spawn(location, TNTPrimed.class);

        world.playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE , 1, 0);

        final int probability = Utils.getRandomInPercentage();

        if (probability <= 50) {
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, super::deadFunctionality, 100);
        }
    }
    private void usePassive() {
       if(spawnedZombies >= 4 || !this.isAlive()) return;

       BlockFace face = entity.getFacing();
       Location location = entity.getLocation();
       String entityID = String.valueOf(this.entity.getEntityId());

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
       follower.setMetadata(idFollower, new FixedMetadataValue(this.plugin, entityID));

       spawnedZombies++;
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
       zombie.setMetadata(idSpecialFollower, new FixedMetadataValue(this.plugin, "YEAH"));
   }

    private void teleportZombie(Location location) {
       world.playSound(this.entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0);
       this.entity.teleport(location);
   }

    private int getRandomDirection() {
       int randomNumber = Utils.getRandomInPercentage();
       int modifyDirection = randomNumber % 2 == 0? 1 : -1;

       return  modifyDirection;
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

    public void removeSpawnedZombies(int quantity) {
        this.spawnedZombies -= quantity;
    }

    public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
       IUltimateLambda ultimate = (health, entityID) -> {
           SupportZombie boss = bosses.get(entityID);

           if(health <= maxHealth * 0.5) boss.playUltimate1();

           if(health <= maxHealth * 0.3) boss.playUltimate2();

       };

       Entity entity = event.getEntity();

       boolean isSupportedSpecialZombie = entity.hasMetadata(idSpecialFollower);
       boolean isFollower = entity.hasMetadata(idFollower);

       boolean continueAlth =  BoosesModel.handleDamageByEntity(event, BarColor.RED, maxHealth, idMetadata, "ZOMBIE", ultimate);

       return (isSupportedSpecialZombie || isFollower) ? false : continueAlth;
   }

    public static void handleDamage(EntityDamageEvent event) {
       BoosesModel.handleDamage(event, "ZOMBIE", BarColor.RED, maxHealth, idMetadata, null);
   }

    public static void handleDead(EntityDeathEvent event) {
       BoosesModel.handleDead(event, idMetadata, bosses);

       Entity entity = event.getEntity();

       boolean isFollower = entity.hasMetadata(idFollower);

       if(!isFollower) return;

       String idParent = (String) entity.getMetadata(idFollower).get(0).value();

       SupportZombie boss = bosses.get(idParent);
       boss.removeSpawnedZombies(1);
   }
}
