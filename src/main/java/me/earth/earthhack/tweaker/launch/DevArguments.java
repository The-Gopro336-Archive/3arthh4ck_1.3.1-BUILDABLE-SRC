package me.earth.earthhack.tweaker.launch;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.ArgumentManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevArguments
implements ArgumentManager {
    private static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");
    private static final DevArguments INSTANCE = new DevArguments();
    private static final String PATH = "earthhack/dev.json";
    private final Map<String, Argument<?>> arguments = new ConcurrentHashMap();

    private DevArguments() {
    }

    public static DevArguments getInstance() {
        return INSTANCE;
    }

    @Override
    public void loadArguments() {
        Path path = Paths.get(PATH, new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        try (InputStream stream = Files.newInputStream(path, new OpenOption[0]);){
            JsonObject object = Jsonable.PARSER.parse(new InputStreamReader(stream)).getAsJsonObject();
            for (Map.Entry entry : object.entrySet()) {
                Argument argument = this.getArgument((String)entry.getKey());
                if (argument == null) {
                    LOGGER.warn("Unknown DevArgument: " + (String)entry.getKey() + "!");
                    continue;
                }
                argument.fromJson((JsonElement)entry.getValue());
                LOGGER.info("Dev-Argument: " + (String)entry.getKey() + " : " + argument.getValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addArgument(String name, Argument<?> argument) {
        if (this.arguments.containsKey(name)) {
            throw new IllegalStateException("Argument with name: " + name + " already exists!");
        }
        this.arguments.put(name, argument);
    }

    @Override
    public <T> Argument<T> getArgument(String name) {
        return this.arguments.get(name);
    }
}
