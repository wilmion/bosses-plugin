package com.wilmion.bossesplugin.objects.metadata;

import com.wilmion.bossesplugin.objects.metadata.MetadataModel;
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
    private List<MetadataModel> data;
}

