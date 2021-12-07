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
import java.util.UUID;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.impl.managers.client.PlayerManager;
import me.earth.earthhack.impl.managers.config.helpers.AbstractConfigHelper;
import me.earth.earthhack.impl.managers.config.util.PlayerConfig;

public class PlayerManagerConfigHelper
extends AbstractConfigHelper<PlayerConfig> {
    private final PlayerManager manager;

    public PlayerManagerConfigHelper(String name, String path, PlayerManager manager) {
        super(name, path);
        this.manager = manager;
    }

    @Override
    protected PlayerConfig create(String name) {
        return PlayerConfig.fromManager(name, this.manager);
    }

    @Override
    protected JsonObject toJson(PlayerConfig config) {
        return config.getAsJsonObject();
    }

    @Override
    protected PlayerConfig readFile(InputStream stream, String name) {
        PlayerConfig config = new PlayerConfig(name, this.manager);
        JsonObject object = Jsonable.PARSER.parse((Reader)new InputStreamReader(stream)).getAsJsonObject();
        for (Map.Entry entry : object.entrySet()) {
            config.register((String)entry.getKey(), UUID.fromString(((JsonElement)entry.getValue()).getAsString()));
        }
        return config;
    }
}

