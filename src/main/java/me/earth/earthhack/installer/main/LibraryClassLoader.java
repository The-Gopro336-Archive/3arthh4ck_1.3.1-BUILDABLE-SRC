/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import me.earth.earthhack.impl.util.misc.StreamUtil;
import me.earth.earthhack.installer.main.Library;

public class LibraryClassLoader
extends URLClassLoader {
    public LibraryClassLoader(ClassLoader parent, URL ... urls) {
        super(urls, parent);
    }

    public void installLibrary(Library library) throws Exception {
        if (library.needsDownload()) {
            new File(library.getUrl().getFile()).getParentFile().mkdirs();
            try (ReadableByteChannel rbc = Channels.newChannel(library.getWeb().openStream());
                 FileOutputStream fos = new FileOutputStream(library.getUrl().getFile());){
                fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            }
        }
        this.addURL(library.getUrl());
    }

    public Class<?> findClass_public(String name) throws ClassNotFoundException {
        return this.findClass(name);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.")) return super.loadClass(name, resolve);
        if (name.startsWith("javax.")) return super.loadClass(name, resolve);
        if (name.startsWith("sun.")) return super.loadClass(name, resolve);
        if (name.startsWith("me.earth.earthhack.installer.main")) return super.loadClass(name, resolve);
        if (name.startsWith("jdk.")) {
            return super.loadClass(name, resolve);
        }
        Class<?> alreadyLoaded = this.findLoadedClass(name);
        if (alreadyLoaded != null) {
            return alreadyLoaded;
        }
        try (InputStream is = this.getResourceAsStream(name.replaceAll("\\.", "/") + ".class");){
            if (is == null) {
                throw new ClassNotFoundException("Could not find " + name);
            }
            byte[] bytes = StreamUtil.toByteArray(is);
            Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
            if (resolve) {
                this.resolveClass(clazz);
            }
            Class<?> class_ = clazz;
            return class_;
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Could not load " + name, e);
        }
    }
}

