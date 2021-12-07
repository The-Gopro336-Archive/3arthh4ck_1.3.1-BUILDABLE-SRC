package org.newdawn.slick.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.newdawn.slick.util.ResourceLocation;

public class FileSystemLocation
implements ResourceLocation {
    private File root;

    public FileSystemLocation(File root) {
        this.root = root;
    }

    @Override
    public URL getResource(String ref) {
        try {
            File file = new File(this.root, ref);
            if (!file.exists()) {
                file = new File(ref);
            }
            if (!file.exists()) {
                return null;
            }
            return file.toURI().toURL();
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public InputStream getResourceAsStream(String ref) {
        try {
            File file = new File(this.root, ref);
            if (!file.exists()) {
                file = new File(ref);
            }
            return new FileInputStream(file);
        }
        catch (IOException e) {
            return null;
        }
    }
}
