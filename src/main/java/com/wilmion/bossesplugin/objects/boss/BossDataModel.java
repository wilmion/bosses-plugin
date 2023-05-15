package com.wilmion.bossesplugin.objects.boss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BossDataModel {
    private Integer id;

    private String name;

    private String metadata;

    private String type;

    private Double health;

    private String barColor;
}