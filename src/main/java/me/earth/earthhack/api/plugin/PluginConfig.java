/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package me.earth.earthhack.api.plugin;

import com.google.gson.annotations.SerializedName;

public final class PluginConfig {
    @SerializedName(value="name")
    private String name;
    @SerializedName(value="mainClass")
    private String mainClass;
    @SerializedName(value="mixinConfig")
    private String mixinConfig;

    public String getName() {
        return this.name;
    }

    public String getMainClass() {
        return this.mainClass;
    }

    public String getMixinConfig() {
        return this.mixinConfig;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PluginConfig) {
            return this.name != null && this.name.equals(((PluginConfig)o).name);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

