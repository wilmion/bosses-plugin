package com.wilmion.alaractplugin.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerDifficulty implements Listener {
    @EventHandler
    public void OnMove(PlayerMoveEvent event) {
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
