package com.wilmion.alaractplugin.mobsDificulties;

import com.wilmion.alaractplugin.mobsDificulties.boss.SupportZombie;
import com.wilmion.alaractplugin.utils.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class ZombieDifficulty implements Listener  {
    Plugin plugin;
    World world;
    Server server;

    // Use for specials zombies
    String MINI_ZOMBIE_BOSS = "miniZombieBoss";

    public ZombieDifficulty(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDieEntity(EntityDeathEvent event) {
       SupportZombie.handleDead(event);
    }

    @EventHandler()
    public void OnDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        world = entity.getWorld();
        server = entity.getServer();

        Boolean isDead = Utils.isDeadEntityOnDamage(entity, event.getFinalDamage(), EntityType.ZOMBIE);
        Boolean isAttack = Utils.isDamageType(event.getCause().name(), "ENTITY_ATTACK");

        boolean continueToAlgthSZ = SupportZombie.handleDamageByEntity(event);

        if(!isDead || !isAttack || !continueToAlgthSZ) return;

        Zombie originalZombie = (Zombie) entity;

        Boolean isSpecialMiniZombie = entity.hasMetadata(MINI_ZOMBIE_BOSS);

        Location location = entity.getLocation();

        if(isSpecialMiniZombie) {
            giveRewardForKillZombies(location);
            return;
        }

        if(!originalZombie.isAdult()) return;

        int probability = Utils.getRandomInPercentage();


        if(probability <= 70) {
            new SupportZombie((Player) event.getDamager(), location, this.plugin);
            return;
        }

        if(probability <= 15) {
            spawnZombie(location);
            spawnZombie(location);
        }
    }

    @EventHandler()
    public void OnDamage(EntityDamageEvent event) {
        SupportZombie.handleDamage(event);
    }

    private void spawnZombie(Location location) {
        Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
        zombie.setBaby();

        zombie.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

        zombie.setMetadata(MINI_ZOMBIE_BOSS, new FixedMetadataValue(plugin, "yes!"));

    }

    private void giveRewardForKillZombies(Location location) {
        ItemStack GoldSword = new ItemStack(Material.GOLDEN_SWORD, 1, (short) 0);

        GoldSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 30);
        GoldSword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);

        ItemMeta goldSwordMetadata = GoldSword.getItemMeta();

        goldSwordMetadata.setDisplayName("Espada de los Valientes");//Deprecated function

        GoldSword.setItemMeta(goldSwordMetadata);

        world.dropItem(location, GoldSword);
    }

}
