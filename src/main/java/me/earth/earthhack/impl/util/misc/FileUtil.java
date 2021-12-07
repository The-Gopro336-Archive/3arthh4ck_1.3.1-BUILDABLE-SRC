package me.earth.earthhack.impl.util.misc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.List;

public class FileUtil {
    public static Path getDirectory(Path parent, String ... paths) {
        if (paths.length < 1) {
            return parent;
        }
        Path dir = FileUtil.lookupPath(parent, paths);
        FileUtil.createDirectory(dir);
        return dir;
    }

    public static Path lookupPath(Path root, String ... paths) {
        return Paths.get(root.toString(), paths);
    }

    public static boolean createDirectory(File file) {
        boolean created = true;
        if (!file.exists()) {
            created = file.mkdir();
        }
        return created;
    }

    public static void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir, new LinkOption[0])) {
                if (Files.exists(dir, new LinkOption[0])) {
                    Files.delete(dir);
                }
                Files.createDirectories(dir, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String file, boolean write, Iterable<String> data) {
        try {
            Path path = Paths.get(file, new String[0]);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            if (write) {
                FileUtil.writeFile(file, data);
            }
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void writeFile(String file, Iterable<String> data) {
        Path path = Paths.get(file, new String[0]);
        try {
            Files.write(path, data, StandardCharsets.UTF_8, Files.exists(path, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static Path getPath(String name, String ... more) throws IOException {
        Path path = Paths.get(name, more);
        if (!Files.exists(path, new LinkOption[0])) {
            Files.createFile(path, new FileAttribute[0]);
        }
        return path;
    }

    public static void openWebLink(URI url) throws Throwable {
        Class<?> clazz = Class.forName("java.awt.Desktop");
        Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
        clazz.getMethod("browse", URI.class).invoke(object, url);
    }
}
