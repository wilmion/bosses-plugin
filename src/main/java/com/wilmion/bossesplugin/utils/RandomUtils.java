package com.wilmion.bossesplugin.utils;

import java.util.Random;

public class RandomUtils {
    public static int getRandomInPercentage() {
        Random random = new Random();

        int percentage = random.nextInt(101);

        return  percentage;
    }

    public static int getRandomNumberForSpace() {
        Random random = new Random();

        return random.nextInt(3) - 1;
    }
}
