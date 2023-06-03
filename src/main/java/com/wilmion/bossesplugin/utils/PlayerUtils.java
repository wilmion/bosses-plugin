package com.wilmion.bossesplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import org.bukkit.entity.Player;

public class PlayerUtils {
    public static void setTitleOnPlayer(Player player, String title, String subtitle) {
        Component titleComponent = Component.text(title);
        Component subtitleComponent = Component.text(subtitle);
        Title titleToShow = Title.title(titleComponent, subtitleComponent);

        player.showTitle(titleToShow);
    }
}
