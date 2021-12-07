/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.authlib.GameProfile
 *  com.mojang.util.UUIDTypeAdapter
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  org.apache.commons.io.IOUtils
 */
package me.earth.earthhack.impl.util.thread;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.io.IOUtils;

public class LookUpUtil
implements Globals {
    private static final BiMap<String, UUID> CACHE = HashBiMap.create();
    private static final JsonParser PARSER = new JsonParser();

    public static UUID getUUIDSimple(String name) {
        ArrayList infoMap;
        NetworkPlayerInfo profile;
        UUID cached = (UUID)CACHE.get((Object)name);
        if (cached != null) {
            return cached;
        }
        if (mc.getConnection() != null && (profile = (NetworkPlayerInfo)(infoMap = new ArrayList(mc.getConnection().getPlayerInfoMap())).stream().filter(info -> info.getGameProfile().getName().equalsIgnoreCase(name)).findFirst().orElse(null)) != null) {
            UUID result = profile.getGameProfile().getId();
            CACHE.forcePut((Object)name, (Object)result);
            return result;
        }
        return null;
    }

    public static UUID getUUID(String name) {
        String ids = LookUpUtil.requestIDs("[\"" + name + "\"]");
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        JsonElement element = PARSER.parse(ids);
        if (element.getAsJsonArray().size() == 0) {
            return null;
        }
        try {
            String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
            UUID result = UUIDTypeAdapter.fromString((String)id);
            CACHE.forcePut((Object)name, (Object)result);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNameSimple(UUID uuid) {
        String cached = (String)CACHE.inverse().get((Object)uuid);
        if (cached != null) {
            return cached;
        }
        if (mc.getConnection() != null) {
            ArrayList infoMap = new ArrayList(mc.getConnection().getPlayerInfoMap());
            for (NetworkPlayerInfo info : infoMap) {
                GameProfile gameProfile = info.getGameProfile();
                if (!gameProfile.getId().equals(uuid)) continue;
                String name = gameProfile.getName();
                CACHE.forcePut((Object)name, (Object)uuid);
                return name;
            }
        }
        return null;
    }

    public static String getName(UUID uuid) {
        String url = "https://api.mojang.com/user/profiles/" + LookUpUtil.uuidToString(uuid) + "/names";
        try {
            String name = IOUtils.toString((URL)new URL(url), (Charset)StandardCharsets.UTF_8);
            JsonArray array = (JsonArray)PARSER.parse(name);
            String player = array.get(array.size() - 1).toString();
            JsonObject object = (JsonObject)PARSER.parse(player);
            String result = object.get("name").toString();
            CACHE.inverse().put((Object)uuid, (Object)result);
            return result;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String requestIDs(String data) {
        try {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            String res = LookUpUtil.convertStreamToString(in);
            ((InputStream)in).close();
            conn.disconnect();
            return res;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Map<Date, String> getNameHistory(UUID id) {
        TreeMap<Date, String> result = new TreeMap<Date, String>(Collections.reverseOrder());
        try {
            JsonArray array = LookUpUtil.getResources(new URL("https://api.mojang.com/user/profiles/" + LookUpUtil.uuidToString(id) + "/names")).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject node = element.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                result.put(new Date(changedAt), name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static JsonElement getResources(URL url) throws Exception {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }
            scanner.close();
            String json = builder.toString();
            JsonElement jsonElement = PARSER.parse(json);
            return jsonElement;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String findNextPlayerName(String input) {
        ArrayList infoMap;
        NetworkPlayerInfo profile;
        if (mc.getConnection() != null && (profile = (NetworkPlayerInfo)(infoMap = new ArrayList(mc.getConnection().getPlayerInfoMap())).stream().filter(info -> TextUtil.startsWith(info.getGameProfile().getName(), input)).findFirst().orElse(null)) != null) {
            return profile.getGameProfile().getName();
        }
        for (String str : CACHE.keySet()) {
            if (!TextUtil.startsWith(str, input)) continue;
            return str;
        }
        return null;
    }

    public static String uuidToString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }
}

