package net.ali.modbot.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUtils {

    public static List<String> readLines(String path) {
        try {
            return Files.lines(Paths.get(path)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeLines(String path, List<String> data) {
        try {
            clearFile(path);
            Files.write(Paths.get(path), data, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map.Entry<String, String> getJsonEntry(String entry) {
        String[] split = entry.replaceAll("[{}]", "").split("=");
        if (split.length > 1) {
            String key = split[0];
            String value = split[1];
            return new AbstractMap.SimpleEntry<>(key, value);
        }
        return null;
    }

    private static void clearFile(String path) {
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
