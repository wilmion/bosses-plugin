package com.wilmion.bossesplugin.objects.perk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PerkDataModel {
    private Integer id;

    private String name;

    private String material;

    private String colorName;

    private List<PerkEffectModel> effects;
}
