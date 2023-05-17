package com.wilmion.bossesplugin.models;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;

public class ProgressBar {
    public static Map<String, BossBar> bars = new TreeMap<>();
    private BossBar bar;
    private String id;

    public ProgressBar(String id) {
        BossBar oldBarData = bars.get(id);

        if(oldBarData == null) {
            this.bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
            this.bar.setVisible(false);

            bars.put(id, bar);
            return;
        }

        this.id = id;
        this.bar = oldBarData;
    }

    public void setColor(BarColor color) {
        bar.setColor(color);
    }

    public void enableBar() {
        bar.setVisible(true);
    }

    public void disabledBar() {
        bar.setVisible(false);
    }

    public void removeAllUsers() {
        bar.removeAll();
    }

    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    public void setTitle(String title) {
        bar.setTitle(title);
    }

    public void setProgress(double progress) {
        if(progress < 0.0) progress = 0.0;
        if(progress > 1.0) progress = 1.0;

        bar.setProgress(progress);
    }

}
