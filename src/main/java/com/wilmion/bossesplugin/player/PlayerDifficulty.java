package com.wilmion.bossesplugin.player;

import com.wilmion.bossesplugin.models.MobDifficulty;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class PlayerDifficulty extends MobDifficulty {

    public PlayerDifficulty(Plugin plugin) {
        super(plugin);
    }

    public void onMovePlayerDifficultyEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        double health = player.getHealth();

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
}
