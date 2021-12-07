/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.helpers.AbstractConfigHelper;
import me.earth.earthhack.impl.managers.config.util.BindConfig;
import me.earth.earthhack.impl.managers.config.util.BindWrapper;

public class BindConfigHelper
extends AbstractConfigHelper<BindConfig> {
    public BindConfigHelper() {
        super("bind", "binds");
    }

    @Override
    protected BindConfig create(String name) {
        return BindConfig.create(name, Managers.MODULES);
    }

    @Override
    protected JsonObject toJson(BindConfig config) {
        JsonObject object = new JsonObject();
        for (BindWrapper wrapper : config.getBinds()) {
            String wrapped = Jsonable.GSON.toJson((Object)wrapper);
            object.add(wrapper.getModule() + "-" + wrapper.getName(), Jsonable.parse(wrapped, false));
        }
        return object;
    }

    @Override
    protected BindConfig readFile(InputStream stream, String name) {
        BindConfig config = new BindConfig(name, Managers.MODULES);
        JsonObject object = Jsonable.PARSER.parse((Reader)new InputStreamReader(stream)).getAsJsonObject();
        for (Map.Entry entry : object.entrySet()) {
            config.add((BindWrapper)Jsonable.GSON.fromJson((JsonElement)entry.getValue(), BindWrapper.class));
        }
        return config;
    }
}

