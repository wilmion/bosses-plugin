package com.wilmion.bossesplugin.objects.perk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PerkEffectModel {
    private String name;

    private Integer level;
}
