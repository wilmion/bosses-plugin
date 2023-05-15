package com.wilmion.bossesplugin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Type;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Resources {
    public static <T> T getJsonByData(String filename, Type classInstance) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create();

        ClassLoader classLoader = Resources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);

        return gson.fromJson(reader, classInstance);
    }

    @SneakyThrows
    public static List<String> getJsonFilesInDirectory(String path) {
        List<String> jsonFileNames = Files.walk(Path.of(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith(".json"))
                .map(File::getName)
                .collect(Collectors.toList());

        return jsonFileNames;
    }

    public static <T> T getJsonByLocalData(String filename, Type classInstance) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create();

        String data = Utils.readFile(filename);

        if(data == null) return null;

        return gson.fromJson(data, classInstance);
    }

    public static void writeFile(String path, Object obj) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create();

        Utils.writeFile(path, gson.toJson(obj));
    }
}
