package com.wilmion.bossesplugin.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BossEnum {
    JORDI("support-zombie"),
    ANN("master-skeleton"),
    MIQUEL("soldier-spider"),
    AURORA("queen-spider"),
    JACK("master-creeper"),
    ZETANNA("master-wizard"),
    ATTICUS("expert-enderman"),
    MILENIA("queen-bee");

    private String key;

    BossEnum(String key) {
        this.key = key;
    }

    public static List<String> getKeys() {
        BossEnum[] allBosses = BossEnum.values();
        List<String> keys = Arrays.stream(allBosses).map(b -> b.key).collect(Collectors.toList());

        return keys;
    }
}