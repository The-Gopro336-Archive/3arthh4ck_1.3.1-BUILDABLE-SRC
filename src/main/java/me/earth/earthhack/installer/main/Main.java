/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.main;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import me.earth.earthhack.impl.modules.client.server.main.ClientMain;
import me.earth.earthhack.impl.modules.client.server.main.ServerMain;
import me.earth.earthhack.installer.main.LibraryClassLoader;

public class Main {
    public static void main(String[] args) throws Throwable {
        URL[] urls;
        ClassLoader bootCl;
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("server")) {
                ServerMain.main(args);
                return;
            }
            if (args[0].equalsIgnoreCase("client")) {
                ClientMain.main(args);
                return;
            }
        }
        if ((bootCl = Main.class.getClassLoader()) instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader)bootCl;
            urls = ucl.getURLs();
        } else {
            urls = new URL[]{Main.class.getProtectionDomain().getCodeSource().getLocation()};
        }
        LibraryClassLoader cl = new LibraryClassLoader(bootCl, urls);
        Thread.currentThread().setContextClassLoader(cl);
        String installer = "me.earth.earthhack.installer.EarthhackInstaller";
        Class<?> c = cl.findClass_public(installer);
        Method m = c.getMethod("launch", cl.getClass(), String[].class);
        Object o = c.newInstance();
        m.invoke(o, cl, args);
    }
}

