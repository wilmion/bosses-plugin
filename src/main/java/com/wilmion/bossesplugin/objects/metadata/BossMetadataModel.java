package com.wilmion.bossesplugin.objects.metadata;

import com.wilmion.bossesplugin.objects.LocationDataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BossMetadataModel extends LocationDataModel {
    private String nameOfClass;
    private String classData;
}
