package com.wilmion.bossesplugin.enums;

public enum StructureEnum {
    JORDI_TOWER("jordi_tower"),
    ANN_TOWER("ann-tower"),
    SPIDER_BASE("spider-base"),
    SPIDER_ENTRY("spider-entry");

    private final String description;

    StructureEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}