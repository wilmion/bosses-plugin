package com.wilmion.bossesplugin.objects.special_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class SpecialEntityDataModel {
    private String key;

    private String entityType;

    private String name;

    private Double health;

    private Optional<Double> followRange = Optional.empty();

    private Optional<String> passenger = Optional.empty();

    private Map<String, Object> equipment;
}
