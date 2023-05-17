package com.wilmion.bossesplugin.objects.metadata.block;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BlockMetadataDataModel {
    private String key;
    private String value;
}
