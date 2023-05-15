package com.wilmion.bossesplugin.models;

import com.wilmion.bossesplugin.utils.Resources;

import lombok.Getter;

@Getter
public class UserDataLevel {
    private final String path = "plugins/bosses-plugin-data/exp-users/";
    private String nameUser;
    private Integer level = 0;
    private Long exp;
    private Long expToNextLevel = 150l;

    public UserDataLevel(String name) {
        if(readUser(name)) return;

        this.exp = 0l;
        this.nameUser = name;

        upsertUser(name);
    }

    public void addExp(int exp, Runnable onLevelUp) {
        this.exp += exp;

        if(this.exp >= this.expToNextLevel) {
            this.level += 1;
            this.expToNextLevel += 150 + (level * 50);

            onLevelUp.run();
        }

        this.upsertUser(this.nameUser);
    }

    public void lessExp(int exp) {
        this.exp -= exp;

        upsertUser(nameUser);
    }

    public double getRemainPercentage() {
        Double lastAddingExp = 150.0 + (level * 50.0);
        Double lastLimitExp = expToNextLevel - lastAddingExp;
        Double expToNextLevelDouble = expToNextLevel - lastLimitExp;
        Double expDouble = exp - lastLimitExp;

        return expDouble / expToNextLevelDouble;
    }

    private boolean readUser(String name) {
        String filename = path + name.trim() + ".json";
        UserDataLevel file = Resources.getJsonByLocalData(filename, UserDataLevel.class);

        if(file == null) return false;

        this.nameUser = file.getNameUser();
        this.exp = file.getExp();
        this.expToNextLevel = file.getExpToNextLevel();
        this.level = file.getLevel();

        return true;
    }

    private void upsertUser(String name) {
        String filename = path + name.trim() + ".json";
        Resources.writeFile(filename, this);
    }
}
