package com.wilmion.bossesplugin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Resources {
    public static Gson gson = new GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create();

    public static <T> T getJsonByData(String filename, Type classInstance) {
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
        String data = readFile(filename);

        if(data == null) return null;

        return gson.fromJson(data, classInstance);
    }

    public static void writeFile(String path, Object obj) {
        writeFileA(path, gson.toJson(obj));
    }

    private static String readFile(String path) {
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            String data = "";

            while (reader.hasNextLine()) data += "\n" + reader.nextLine();

            return data;

        } catch (Exception e) {
            return null;
        }
    }

    private static boolean writeFileA(String path, String content) {
        try {
            File file = new File(path);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
