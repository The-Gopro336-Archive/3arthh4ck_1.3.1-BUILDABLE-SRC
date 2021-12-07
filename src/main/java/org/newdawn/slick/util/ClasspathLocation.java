package org.newdawn.slick.util;

import java.io.InputStream;
import java.net.URL;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.util.ResourceLocation;

public class ClasspathLocation
implements ResourceLocation {
    @Override
    public URL getResource(String ref) {
        String cpRef = ref.replace('\\', '/');
        return ResourceLoader.class.getClassLoader().getResource(cpRef);
    }

    @Override
    public InputStream getResourceAsStream(String ref) {
        String cpRef = ref.replace('\\', '/');
        return ResourceLoader.class.getClassLoader().getResourceAsStream(cpRef);
    }
}
