/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import org.newdawn.slick.util.ClasspathLocation;
import org.newdawn.slick.util.FileSystemLocation;
import org.newdawn.slick.util.ResourceLocation;

public class ResourceLoader {
    private static ArrayList locations = new ArrayList();

    public static void addResourceLocation(ResourceLocation location) {
        locations.add(location);
    }

    public static void removeResourceLocation(ResourceLocation location) {
        locations.remove(location);
    }

    public static void removeAllResourceLocations() {
        locations.clear();
    }

    public static InputStream getResourceAsStream(String ref) {
        ResourceLocation location;
        InputStream in = null;
        for (int i = 0; i < locations.size() && (in = (location = (ResourceLocation)locations.get(i)).getResourceAsStream(ref)) == null; ++i) {
        }
        if (in == null) {
            throw new RuntimeException("Resource not found: " + ref);
        }
        return new BufferedInputStream(in);
    }

    public static boolean resourceExists(String ref) {
        URL url = null;
        for (int i = 0; i < locations.size(); ++i) {
            ResourceLocation location = (ResourceLocation)locations.get(i);
            url = location.getResource(ref);
            if (url == null) continue;
            return true;
        }
        return false;
    }

    public static URL getResource(String ref) {
        ResourceLocation location;
        URL url = null;
        for (int i = 0; i < locations.size() && (url = (location = (ResourceLocation)locations.get(i)).getResource(ref)) == null; ++i) {
        }
        if (url == null) {
            throw new RuntimeException("Resource not found: " + ref);
        }
        return url;
    }

    static {
        locations.add(new ClasspathLocation());
        locations.add(new FileSystemLocation(new File(".")));
    }
}

