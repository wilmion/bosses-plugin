package com.wilmion.bossesplugin.objects.metadata.block;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BlockMetadataModel {
    private List<BlockMetadataDataModel> data;
}

