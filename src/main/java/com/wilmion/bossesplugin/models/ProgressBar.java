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
        this.id = id;

        if(oldBarData == null) {
            this.bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
            this.bar.setVisible(false);
            bars.put(id, this.bar);
            return;
        }

        this.bar = oldBarData;
    }

    public void setColor(BarColor color) {
        this.bar.setColor(color);
        bars.put(id, this.bar);
    }

    public void enableBar() {
        this.bar.setVisible(true);
        bars.put(id, this.bar);
    }

    public void disabledBar() {
        this.bar.setVisible(false);
        bars.put(id, this.bar);
    }

    public void removeAllUsers() {
        this.bar.removeAll();
        bars.put(id, this.bar);
    }

    public void addPlayer(Player player) {
        this.bar.addPlayer(player);
        bars.put(id, this.bar);
    }

    public void setTitle(String title) {
        this.bar.setTitle(title);
        bars.put(id, this.bar);
    }

    public BossBar getBar() {
        return this.bar;
    }

    public void setProgress(double progress) {
        this.bar.setProgress(progress);
        bars.put(id, this.bar);
    }

}
