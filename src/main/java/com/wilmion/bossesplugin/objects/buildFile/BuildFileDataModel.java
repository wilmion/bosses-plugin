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
    private String b; // Block Data

    private String l; // l=alterX:alterY:alterZ

    private Optional<String> eD = Optional.empty(); //entityData

    private Optional<String> eS = Optional.empty(); // entitySpawn

    private Optional<String> qS = Optional.empty(); // quantitySpawn

    private Optional<String> bS = Optional.empty(); // bossSpawn
}
