package com.wilmion.alaractplugin;

import com.wilmion.alaractplugin.models.UserDataLevel;

public class Test {
    public static void main(String[] args) {
        String name = "TupapiWilmion";

        UserDataLevel user = new UserDataLevel(name);

        user.addExp(30, null);

        System.out.println("Name: " + user.getNameUser());
        System.out.println("Exp: " + user.getExp());
    }
}
