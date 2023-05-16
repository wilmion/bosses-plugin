package com.wilmion.bossesplugin.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class LocationDataModel {
    private String worldId;
    private Double x;
    private Double y;
    private Double z;
}
