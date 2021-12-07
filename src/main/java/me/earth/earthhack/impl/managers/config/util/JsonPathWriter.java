/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.config.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import me.earth.earthhack.api.config.Jsonable;

public class JsonPathWriter {
    public static void write(Path path, JsonObject object) throws IOException {
        String json = Jsonable.GSON.toJson((JsonElement)object);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(path, new OpenOption[0])));){
            writer.write(json);
        }
    }
}

