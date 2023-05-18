package com.wilmion.bossesplugin.models.metadata;

import com.wilmion.bossesplugin.objects.metadata.MetadataModel;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityScoreboard {
    private static Pattern pattern = Pattern.compile("^\\[KEY=(.*?)\\|VALUE=(.*?)\\]$");

    public static List<MetadataModel> getAllScoreboard(Entity entity) {
        Set<String> scoreboards = entity.getScoreboardTags();

        if(scoreboards.stream().count() == 0) return new ArrayList<>();

        List<MetadataModel> result = new ArrayList<>();

        for(String scoreboard: scoreboards) {
            MetadataModel metadataModel = new MetadataModel();
            Matcher matcher = pattern.matcher(scoreboard);

            if(!matcher.find()) continue;

            metadataModel.setKey(matcher.group(1));
            metadataModel.setValue(matcher.group(2));
            result.add(metadataModel);
        }

        return result;
    }

    public static Optional<MetadataModel> getScoreboard(Entity entity, String key) {
        List<MetadataModel> data = getAllScoreboard(entity);

        if(data.stream().count() == 0) return Optional.ofNullable(null);

        return data.stream().filter(d -> d.getKey().equals(key)).findFirst();
    }

    public static void upsertScoreboard(Entity entity, String key, String value) {
        String scoreboardToSet = "[KEY=" + key + "|VALUE=" + value + "]";
        Optional<MetadataModel> data = getScoreboard(entity, key);

        if(data.isPresent()) deleteScoreboard(entity, key);

        entity.addScoreboardTag(scoreboardToSet);
    }

    public static void deleteScoreboard(Entity entity, String key) {
        Optional<MetadataModel> data = getScoreboard(entity, key);

        if(data.isEmpty()) return;

        String scoreboardToDelete = "[KEY=" + data.get().getKey() + "|VALUE=" + data.get().getValue() + "]";

        entity.removeScoreboardTag(scoreboardToDelete);
    }

}