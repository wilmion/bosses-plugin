package com.wilmion.bossesplugin.objects.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BossMetadataModel {
    private String nameOfClass;
    private String classData;
}
