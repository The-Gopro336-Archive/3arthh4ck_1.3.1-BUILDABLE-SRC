/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;
import me.earth.earthhack.impl.util.misc.StreamUtil;
import me.earth.earthhack.installer.InstallerGlobals;
import me.earth.earthhack.installer.main.Main;
import me.earth.earthhack.installer.main.MinecraftFiles;
import me.earth.earthhack.installer.service.InstallerUtil;
import me.earth.earthhack.installer.service.Srg2NotchService;
import me.earth.earthhack.installer.version.Version;
import me.earth.earthhack.installer.version.VersionUtil;

public class InstallerService {
    public static final String VANILLA = "earthhack/vanilla/1.12.2/";
    public static final String FORGE = "earthhack/forge/1.12.2/";
    public static final String JAR = "forge-1.12.2.jar";
    public static final String VJAR = "vanilla-1.12.2.jar";
    private final Srg2NotchService remapper = new Srg2NotchService();

    public void install(MinecraftFiles files, Version version) throws IOException {
        boolean hasForge = VersionUtil.hasForge(version);
        boolean hasEarth = VersionUtil.hasEarthhack(version);
        this.update(files, hasForge);
        InstallerUtil.installLibs(version.getJson());
        if (!hasEarth) {
            InstallerUtil.installEarthhack(version.getJson(), hasForge);
            this.write(version);
        }
    }

    public void update(MinecraftFiles files, boolean forge) throws IOException {
        String libPath = forge ? files.getLibraries() + FORGE + JAR : files.getLibraries() + VANILLA + VJAR;
        URL us = Main.class.getProtectionDomain().getCodeSource().getLocation();
        URL target = new URL("file:/" + libPath);
        new File(target.getFile()).getParentFile().mkdirs();
        if (forge) {
            if (!InstallerGlobals.hasInstalledForge()) {
                StreamUtil.copy(us, target);
                InstallerGlobals.setForge(true);
            }
        } else if (!InstallerGlobals.hasInstalledVanilla()) {
            this.remapper.remap(us, target);
            InstallerGlobals.setVanilla(true);
        }
    }

    public void uninstall(Version version) throws IOException {
        InstallerUtil.uninstallEarthhack(version.getJson());
        InstallerUtil.uninstallLibs(version.getJson());
        this.write(version);
    }

    public void write(Version version) throws IOException {
        JsonPathWriter.write(version.getFile().toPath(), version.getJson());
    }
}

