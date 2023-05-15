package com.wilmion.bossesplugin.objects.special_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SpecialEntityModel {
    private List<SpecialEntityDataModel> entities;
}
