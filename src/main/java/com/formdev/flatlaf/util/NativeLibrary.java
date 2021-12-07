/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.util;

import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

public class NativeLibrary {
    private static final String DELETE_SUFFIX = ".delete";
    private static boolean deletedTemporary;
    private final boolean loaded;

    public NativeLibrary(String libraryName, ClassLoader classLoader, boolean supported) {
        this.loaded = supported ? NativeLibrary.loadLibraryFromJar(libraryName, classLoader) : false;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    private static boolean loadLibraryFromJar(String libraryName, ClassLoader classLoader) {
        URL libraryUrl;
        libraryName = NativeLibrary.decorateLibraryName(libraryName);
        URL uRL = libraryUrl = classLoader != null ? classLoader.getResource(libraryName) : NativeLibrary.class.getResource("/" + libraryName);
        if (libraryUrl == null) {
            NativeLibrary.log("Library '" + libraryName + "' not found", null);
            return false;
        }
        File tempFile = null;
        try {
            File libraryFile;
            if ("file".equals(libraryUrl.getProtocol()) && (libraryFile = new File(libraryUrl.getPath())).isFile()) {
                System.load(libraryFile.getCanonicalPath());
                return true;
            }
            Path tempPath = NativeLibrary.createTempFile(libraryName);
            tempFile = tempPath.toFile();
            try (InputStream in = libraryUrl.openStream();){
                Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
            }
            System.load(tempFile.getCanonicalPath());
            NativeLibrary.deleteOrMarkForDeletion(tempFile);
            return true;
        }
        catch (Throwable ex) {
            NativeLibrary.log(null, ex);
            if (tempFile != null) {
                NativeLibrary.deleteOrMarkForDeletion(tempFile);
            }
            return false;
        }
    }

    private static String decorateLibraryName(String libraryName) {
        if (SystemInfo.isWindows) {
            return libraryName.concat(".dll");
        }
        String suffix = SystemInfo.isMacOS ? ".dylib" : ".so";
        int sep = libraryName.lastIndexOf(47);
        return sep >= 0 ? libraryName.substring(0, sep + 1) + "lib" + libraryName.substring(sep + 1) + suffix : "lib" + libraryName + suffix;
    }

    private static void log(String msg, Throwable thrown) {
        LoggingFacade.INSTANCE.logSevere(msg, thrown);
    }

    private static Path createTempFile(String libraryName) throws IOException {
        int sep = libraryName.lastIndexOf(47);
        String name = sep >= 0 ? libraryName.substring(sep + 1) : libraryName;
        int dot = name.lastIndexOf(46);
        String prefix = (dot >= 0 ? name.substring(0, dot) : name) + '-';
        String suffix = dot >= 0 ? name.substring(dot) : "";
        Path tempDir = NativeLibrary.getTempDir();
        long nanoTime = System.nanoTime();
        int i = 0;
        while (true) {
            String s = prefix + Long.toUnsignedString(nanoTime) + i + suffix;
            try {
                return Files.createFile(tempDir.resolve(s), new FileAttribute[0]);
            }
            catch (FileAlreadyExistsException fileAlreadyExistsException) {
                ++i;
                continue;
            }
            break;
        }
    }

    private static Path getTempDir() throws IOException {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (SystemInfo.isWindows) {
            tmpdir = tmpdir + "\\flatlaf.temp";
        }
        Path tempDir = Paths.get(tmpdir, new String[0]);
        Files.createDirectories(tempDir, new FileAttribute[0]);
        if (SystemInfo.isWindows) {
            NativeLibrary.deleteTemporaryFiles(tempDir);
        }
        return tempDir;
    }

    private static void deleteTemporaryFiles(Path tempDir) {
        if (deletedTemporary) {
            return;
        }
        deletedTemporary = true;
        File[] markerFiles = tempDir.toFile().listFiles((dir, name) -> name.endsWith(DELETE_SUFFIX));
        if (markerFiles == null) {
            return;
        }
        for (File markerFile : markerFiles) {
            File toDeleteFile = new File(markerFile.getParent(), StringUtils.removeTrailing(markerFile.getName(), DELETE_SUFFIX));
            if (toDeleteFile.exists() && !toDeleteFile.delete()) continue;
            markerFile.delete();
        }
    }

    private static void deleteOrMarkForDeletion(File file) {
        if (file.delete()) {
            return;
        }
        try {
            File markFile = new File(file.getParent(), file.getName() + DELETE_SUFFIX);
            markFile.createNewFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

