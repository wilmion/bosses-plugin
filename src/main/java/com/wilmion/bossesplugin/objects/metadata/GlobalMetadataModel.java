package com.wilmion.bossesplugin.objects.metadata;

import com.wilmion.bossesplugin.objects.LocationDataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class GlobalMetadataModel extends LocationDataModel {
    private List<Map<String, String>> metadata;
}
