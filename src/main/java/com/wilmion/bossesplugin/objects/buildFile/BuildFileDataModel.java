package com.wilmion.bossesplugin.objects.buildFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;


@AllArgsConstructor
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class BuildFileDataModel {
    private String blockData;

    private Double alterX;

    private Double alterY;

    private Double alterZ;

    private Optional<String> entityData = Optional.empty();

    private Optional<String> entitySpawn = Optional.empty();

    private Optional<String> quantitySpawn = Optional.empty();

    private Optional<String> bossSpawn = Optional.empty();
}
