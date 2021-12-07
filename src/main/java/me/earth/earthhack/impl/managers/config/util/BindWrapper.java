/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package me.earth.earthhack.impl.managers.config.util;

import com.google.gson.annotations.SerializedName;

public class BindWrapper {
    @SerializedName(value="name")
    private String name;
    @SerializedName(value="module")
    private String module;
    @SerializedName(value="value")
    private String value;

    public BindWrapper(String name, String module, String value) {
        this.name = name;
        this.module = module;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getModule() {
        return this.module;
    }

    public String getValue() {
        return this.value;
    }
}

