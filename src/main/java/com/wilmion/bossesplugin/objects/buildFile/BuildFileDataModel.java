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
    private String materialType;

    private String blockData;

    private Double alterX;

    private Double alterY;

    private Double alterZ;

    private Optional<String> entitySpawn = Optional.ofNullable(null);

    private Optional<String> quantitySpawn = Optional.ofNullable(null);

    private Optional<String> bossSpawn = Optional.ofNullable(null);
}
