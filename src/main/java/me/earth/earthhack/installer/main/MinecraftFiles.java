/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.main;

import java.io.File;

public class MinecraftFiles {
    private String minecraft;
    private String libraries;
    private String versions;

    public String getMinecraft() {
        return this.minecraft;
    }

    public String getLibraries() {
        return this.libraries;
    }

    public String getVersions() {
        return this.versions;
    }

    public void findFiles(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nux")) {
            this.minecraft = System.getProperty("user.home") + "/.minecraft/";
        } else if (os.contains("darwin") || os.contains("mac")) {
            this.minecraft = System.getProperty("user.home") + "/Library/Application Support/minecraft/";
        } else if (os.contains("win")) {
            this.minecraft = System.getenv("APPDATA") + File.separator + ".minecraft" + File.separator;
        }
        if (this.minecraft != null) {
            this.libraries = this.minecraft + "libraries" + File.separator;
            this.versions = this.minecraft + "versions" + File.separator;
            return;
        }
        if (args.length < 3) {
            throw new IllegalStateException("Unknown OS, please specify minecraft, libraries and versions folders as main args!");
        }
        this.minecraft = args[0];
        this.libraries = args[1];
        this.versions = args[2];
    }
}

