package com.wilmion.bossesplugin.objects.boss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BossModel {
    private List<BossDataModel> bosses;
}
