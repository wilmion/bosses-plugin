package com.wilmion.alaractplugin.mobsDificulties.boss;

import com.wilmion.alaractplugin.models.ProgressBar;
import com.wilmion.alaractplugin.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.TreeMap;

public class SupportZombie {
    static Map<String, SupportZombie> bosses = new TreeMap<>();
    static final int periodPassive = 100;
    static final String idMetadata = "support-boss-zombie";
    static final String idFollower = "PARENT-ID";
    static final  String idSpecialFollower = "IS-SPECIAL-FOLLOWER-FROM-ZOMBIE-SUPPORT";
    private int spawnedZombies = 0;
    private boolean useUltimate1 = false;
    private boolean useUltimate2 = false;
    Zombie entity;
    World world;
    Plugin plugin;
   public SupportZombie(Player player, Location location, Plugin plugin) {
       this.world = player.getWorld();
       this.plugin = plugin;

       world.spawn(location, LightningStrike.class);
       this.entity = world.spawn(location, Zombie.class);
       this.setTemporalInvunerability();

       AttributeInstance healthAttribute = this.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
       healthAttribute.setBaseValue(100.0);

       this.entity.setHealth(100.0);
       this.entity.setCustomName("JORDI EL IRRESISTIBLE");
       this.entity.setCustomNameVisible(true);
       this.equipZombie(this.entity);
       this.entity.setMetadata(idMetadata, new FixedMetadataValue(this.plugin, "true"));

       plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
             if(spawnedZombies < 4 && this.entity.getHealth() > 0.0) this.passiveHability();
       }, periodPassive, periodPassive);

       String entityID = String.valueOf(this.entity.getEntityId());

       bosses.put(entityID, this);
   }

   private void setTemporalInvunerability() {
       PotionEffect fireResistence = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 10);
       PotionEffect damageResistence = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 50,12);

       this.entity.addPotionEffect(fireResistence);
       this.entity.addPotionEffect(damageResistence);
   }

   private void passiveHability() {
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

   private void equipZombie(Zombie zombie) {
       zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
       zombie.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
       zombie.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
       zombie.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));

       zombie.getEquipment().setItemInMainHand(new ItemStack(Material.STICK));
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

   private void teleportZombie(Location location, Player player) {
       player.playSound(this.entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
       this.entity.teleport(location);
   }

   private int getRandomDirection() {
       int randomNumber = Utils.getRandomInPercentage();
       int modifyDirection = randomNumber % 2 == 0? 1 : -1;

       return  modifyDirection;
   }

   public void playUltimate1() {
       BlockFace face = entity.getFacing();
       Location location = entity.getLocation();
       Entity target = entity.getTarget();

       if(target == null || !(target instanceof Player) || useUltimate1) return;

       this.useUltimate1 = true;

       Player player = (Player) target;

       int modX = face.getModX();
       int modZ = face.getModZ();

       double greaterValOnX = 8 * modX * -1;
       double greaterValOnZ = 8 * modZ * -1;

       location.setX(location.getX() + greaterValOnX);
       location.setZ(location.getZ() + greaterValOnZ);

       this.teleportZombie(location, player);

       int newXGreaterVal = modX * 3;
       int newZGreaterVal = modZ * 3;

       for (int i = 0; i < 3; i++) {
           final int index = i;

           plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin , () -> {
               Location locationSpawn = entity.getLocation();

               locationSpawn.setX(locationSpawn.getX() + newXGreaterVal);
               locationSpawn.setZ(locationSpawn.getZ() + newZGreaterVal);

               Location locationSpawn2 = locationSpawn;

               int greaterCordinate = 2 * index;

               if (newXGreaterVal != 0) locationSpawn.setZ(locationSpawn.getZ() + greaterCordinate);
               else locationSpawn.setX(locationSpawn.getX() + greaterCordinate);

               this.generateSpecialFollower(locationSpawn);

               if(index != 0) {
                   if (newXGreaterVal != 0) locationSpawn2.setZ(locationSpawn2.getZ() - greaterCordinate);
                   else locationSpawn.setX(locationSpawn2.getX() - greaterCordinate);

                   this.generateSpecialFollower(locationSpawn2);
                   this.setTemporalInvunerability();
               }
           }, 20 * index);
       }
   }

   public void playUltimate2() {
       Entity target = entity.getTarget();

       if(target == null || !(target instanceof Player) || useUltimate2) return;

       final Player player = (Player) target;

       this.useUltimate2 = true;

       Runnable handleHability = () -> {
           Location location = player.getLocation();

           int xMultiplier = this.getRandomDirection() * 5;
           int zMultiplier = this.getRandomDirection() * 5;

           location.setX(location.getX() + xMultiplier);
           location.setZ(location.getZ() + zMultiplier);

           this.teleportZombie(location, player);
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

   public void deadFunctionalitie() {
       Location location = this.entity.getLocation();
       world.spawn(location, TNTPrimed.class);
       world.spawn(location, TNTPrimed.class);
       world.spawn(location, TNTPrimed.class);

       world.playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE , 1, 0);

       Runnable handleSpawnReward = () -> {
           ItemStack perk = new ItemStack(Material.YELLOW_DYE, 1, (short) 0);

           ItemMeta goldSwordMetadata = perk.getItemMeta();

           goldSwordMetadata.setDisplayName(ChatColor.YELLOW + "Esencia de los condenados"); //Deprecated function

           perk.setItemMeta(goldSwordMetadata);

           world.dropItem(location, perk);
       };

       final int probability = Utils.getRandomInPercentage();

       if (probability <= 50) {
           this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, handleSpawnReward, 100);
       }
   }

   public static void upsertHealthBar(Zombie zombie, Player player, double health) {
       String name = zombie.getCustomName();

       ProgressBar progressBar = new ProgressBar(idMetadata);
       progressBar.setTitle(name + ChatColor.DARK_RED);
       progressBar.setColor(BarColor.RED);
       progressBar.setProgress(health / 100.0);

       if(health > 0.0) progressBar.enableBar();

       if(player != null) progressBar.addPlayer(player);
   }

   public static void useUltimates(Zombie zombie, double health) {
        String entityID = String.valueOf(zombie.getEntityId());

        SupportZombie boss = bosses.get(entityID);

        if(health <= 50.0) {
            boss.playUltimate1();
        }

        if(health <= 30.00) {
            boss.playUltimate2();
        }
   }

   public static boolean handleDamageByEntity(EntityDamageByEntityEvent event) {
       Entity entity = event.getEntity();
       LivingEntity living = (LivingEntity) entity;

       boolean isZombie = entity.getType() == EntityType.ZOMBIE;
       boolean isSupportedZombie = entity.hasMetadata(idMetadata);
       boolean isSupportedSpecialZombie = entity.hasMetadata(idSpecialFollower);
       Player playerDamager = Utils.playerDamager(event.getDamager());

       boolean isFollower = entity.hasMetadata(idFollower);

       double health = Utils.getHealthByDamage(event.getFinalDamage(), living.getHealth());

       if(!isZombie) return true;

       if(isSupportedZombie) {
           Zombie bossEntity = (Zombie) entity;
           useUltimates(bossEntity, health);
           upsertHealthBar(bossEntity, playerDamager, health);
       }

       boolean continueAlth = health <= 0.0 && (isFollower || isSupportedZombie || isSupportedSpecialZombie);

       return !continueAlth;
   }

   public static void handleDamage(EntityDamageEvent event) {
       Entity entity = event.getEntity();

       boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getDamage(), EntityType.ZOMBIE);
       boolean isSupportedZombie = entity.hasMetadata(idMetadata);

       if(!isDead || !isSupportedZombie) return;
       Zombie bossEntity = (Zombie) entity;

       double health = Utils.getHealthByDamage(event.getFinalDamage(), bossEntity.getHealth());

       upsertHealthBar(bossEntity, null, health);
   }

   public static void handleDead(EntityDeathEvent event) {
       Entity entity = event.getEntity();
       String entityID = String.valueOf(entity.getEntityId());

       boolean isFollower = entity.hasMetadata(idFollower);
       boolean isSupportedZombie = entity.hasMetadata(idMetadata);

       if(isSupportedZombie) {
           ProgressBar progressBar = new ProgressBar(idMetadata);
           progressBar.disabledBar();
           progressBar.removeAllUsers();

           SupportZombie zombie = bosses.get(entityID);

           zombie.deadFunctionalitie();
           return;
       }

       if(!isFollower) return;

       String idParent = (String) entity.getMetadata(idFollower).get(0).value();

       SupportZombie boss = bosses.get(idParent);
       boss.removeSpawnedZombies(1);
   }
}
