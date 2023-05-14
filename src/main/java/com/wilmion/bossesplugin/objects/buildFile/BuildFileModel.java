package com.wilmion.bossesplugin.objects.buildFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class BuildFileModel {
    private List<BuildFileDataModel> data;

    private String name;
}
