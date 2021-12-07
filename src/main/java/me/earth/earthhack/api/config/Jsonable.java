package me.earth.earthhack.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;

public interface Jsonable {
    public static final JsonParser PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public void fromJson(JsonElement var1);

    public String toJson();

    public static JsonElement parse(String string) {
        return Jsonable.parse(string, true);
    }

    public static JsonElement parse(String string, boolean addQuotation) {
        JsonElement element = null;
        try (JsonReader reader = new JsonReader(new StringReader(addQuotation ? "\"" + string + "\"" : string));){
            reader.setLenient(true);
            element = PARSER.parse(reader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return element;
    }
}
