package com.wilmion.bossesplugin.models;

import com.wilmion.bossesplugin.utils.Utils;

import com.google.gson.Gson;

public class UserDataLevel {
    private  int level = 0;
    private long exp;
    private long expToNextLevel = 150;
    private String nameUser;

    private final String pathPlugin = "plugins/bosses-plugin-data/exp-users";

    public UserDataLevel(String name) {
        boolean successRead = this.readUser(name);

        if(successRead) return;

        this.exp = 0;
        this.nameUser = name;

        this.upsertUser(name);
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

        this.upsertUser(this.nameUser);
    }

    public long getExp() {
        return this.exp;
    }

    public String getNameUser() {
        return this.nameUser;
    }

    public int getLevel() {
        return this.level;
    }

    public double getRemainPercentage() {
        double lastAddingExp = 150.0 + (level * 50.0);
        double lastLimitExp = expToNextLevel - lastAddingExp;

        double expToNextLevelDouble = expToNextLevel - lastLimitExp;
        double expDouble = exp - lastLimitExp;

        return expDouble / expToNextLevelDouble;
    }

    private boolean readUser(String name) {
        final Gson gson = new Gson();
        final String path = this.pathPlugin + name.trim() + ".json";

        String data = Utils.readFile(path);

        if(data == null) return false;

        UserDataLevel jsonData = gson.fromJson(data, UserDataLevel.class);

        this.nameUser = jsonData.nameUser;
        this.exp =  jsonData.exp;
        this.expToNextLevel =  jsonData.expToNextLevel;
        this.level = jsonData.level;

        return true;
    }

    private void upsertUser(String name) {
        Gson gson = new Gson();

        String path =  this.pathPlugin + name.trim() + ".json";

        String dataInJson = gson.toJson(this);

        Utils.writeFile(path, dataInJson);
    }
}
