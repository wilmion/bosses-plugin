package com.wilmion.bossesplugin.objects.special_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SpecialEntityDataModel {
    private String key;

    private String entityType;

    private String name;

    private Double health;

    private Optional<Double> followRange;

    private Map<String, Object> equipment;
}
