package com.danielkkrafft.wilddungeons.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

public class FileUtil {

    private static Path gamePath;
    private static Path worldPath;
    private static Path dungeonsPath;

    public static void deleteDirectoryContents(Path dir, boolean includeRoot) {

        if (!Files.exists(dir))
            return;

        try {
            Files.walk(dir).sorted(Comparator.reverseOrder()).filter(path -> !path.equals(dir)).forEach(FileUtil::deletePath);

            if (includeRoot)
                Files.delete(dir);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void copyDirectoryContents(Path source, Path destination, boolean cleanDestination) {

        try {
            if (Files.notExists(destination))
                Files.createDirectory(destination);


            if (cleanDestination)
                deleteDirectoryContents(destination, false);


            Files.walk(source).forEach(srcPath -> {
                String relativePortion = srcPath.toString().substring(source.toString().length());
                Path destPath = destination.resolve(relativePortion.startsWith("\\") ? relativePortion.substring(1) : relativePortion).toAbsolutePath();

                try  {
                    if (Files.isDirectory(srcPath)) {

                        if (Files.notExists(destPath)) {
                            Files.createDirectories(destPath);
                        }

                    } else {
                        Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }
            });

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void createDirectory(Path target) {

        try {
            Files.createDirectories(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJson(Object object, File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(file)) {

            gson.toJson(object, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T readJson(Class<T> objClass, File file) {
        Gson gson = new Gson();

        if (file.exists()) {
            try (Reader reader = Files.newBufferedReader(Paths.get(file.toString()))) {
                return gson.fromJson(reader, objClass);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return null;
    }

    public static void deletePath(Path path) {
        try {
            Files.delete(path);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void writeNbt(CompoundTag nbt, File file) {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            NbtIo.writeCompressed(nbt, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CompoundTag readNbt(File file) {
        try {
            if (!file.exists()) return new CompoundTag();
            FileInputStream inputStream = new FileInputStream(file);
            return NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Path getWorldPath() {return worldPath;}
    public static void setWorldPath(Path path) {worldPath = path;}

    public static Path getGamePath() {return gamePath;}
    public static void setGamePath(Path path) {gamePath = path;}

    public static Path getDungeonsPath() { return dungeonsPath; }
    public static void setDungeonsPath(Path path) {
        dungeonsPath = path;
        File dungeonsFolder = dungeonsPath.toFile();

        if (!dungeonsFolder.exists())
            dungeonsFolder.mkdir();

    }
}

