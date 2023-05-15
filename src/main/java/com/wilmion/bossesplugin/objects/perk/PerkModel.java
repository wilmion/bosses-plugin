package com.wilmion.bossesplugin.objects.perk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PerkModel {
    private List<PerkDataModel> perks;
}
