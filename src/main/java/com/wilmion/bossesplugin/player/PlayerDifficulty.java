package com.wilmion.bossesplugin.player;

import com.wilmion.bossesplugin.models.MobDifficulty;

import com.wilmion.bossesplugin.models.metadata.BossesMetadata;
import com.wilmion.bossesplugin.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class PlayerDifficulty extends MobDifficulty {
    public PlayerDifficulty() {
        super();
    }

    public void onMovePlayerDifficultyEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Double health = player.getHealth();

        if(health <= 10.00 && health >= 5.00) {
            player.setWalkSpeed(0.18f);
            return;
        }

        if(health < 5.00) {
            player.setWalkSpeed(0.14f);
            return;
        }

        player.setWalkSpeed(0.22f);
    }

    public static void watchDifficulty() {
        Plugin plugin = PluginUtils.getPlugin();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Difficulty difficulty = Bukkit.getWorlds().get(0).getDifficulty();

            if(!difficulty.equals(Difficulty.PEACEFUL)) return;
        },0, 20);
    }
}
