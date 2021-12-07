/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import me.earth.earthhack.impl.util.misc.StreamUtil;
import me.earth.earthhack.installer.srg2notch.ASMRemapper;
import me.earth.earthhack.installer.srg2notch.Mapping;

public class Srg2NotchService {
    private final ASMRemapper remapper = new ASMRemapper();

    public void remap(URL from, URL to) throws IOException {
        Mapping mapping = Mapping.fromResource("mappings/mappings.csv");
        JarFile jar = new JarFile(from.getFile());
        try (FileOutputStream fos = new FileOutputStream(to.getFile());
             JarOutputStream jos = new JarOutputStream(fos);){
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry next = e.nextElement();
                this.handleEntry(next, jos, jar, mapping);
            }
        }
    }

    protected void handleEntry(JarEntry entry, JarOutputStream jos, JarFile jar, Mapping mapping) throws IOException {
        try (InputStream is = jar.getInputStream(entry);){
            jos.putNextEntry(new JarEntry(entry.getName()));
            if (entry.getName().endsWith(".class")) {
                byte[] bytes = StreamUtil.toByteArray(is);
                jos.write(this.remapper.transform(bytes, mapping));
            } else {
                StreamUtil.copy(is, jos);
            }
            jos.flush();
            jos.closeEntry();
        }
    }
}

