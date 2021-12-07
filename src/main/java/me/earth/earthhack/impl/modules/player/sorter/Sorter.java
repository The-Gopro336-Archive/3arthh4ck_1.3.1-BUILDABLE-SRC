package me.earth.earthhack.impl.modules.player.sorter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.config.helpers.CurrentConfig;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;
import me.earth.earthhack.impl.modules.player.sorter.InventoryLayout;
import me.earth.earthhack.impl.modules.player.sorter.ListenerMotion;
import me.earth.earthhack.impl.util.helpers.addable.LoadableModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class Sorter
extends LoadableModule {
    public static final String PATH = "earthhack/sorter";
    public static final String JSON_PATH = "earthhack/sorter/Sorter.json";
    protected final Setting<Boolean> virtualHotbar = this.register(new BooleanSetting("VirtualHotbar", false));
    protected final Setting<Boolean> sort = this.register(new BooleanSetting("Sort", false));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 500, 0, 5000));
    protected final Setting<Integer> globalDelay = this.register(new NumberSetting<Integer>("Global-Delay", 500, 0, 5000));
    protected final Setting<Boolean> sortInLoot = this.register(new BooleanSetting("SortInLoot", false));
    protected final Setting<Boolean> sortInInv = this.register(new BooleanSetting("SortInInventory", false));
    protected final StopWatch timer = new StopWatch();
    protected final Map<String, InventoryLayout> layouts = new HashMap<String, InventoryLayout>();
    protected Map<Integer, Integer> reverse = null;
    protected Map<Integer, Integer> mapping = null;
    protected InventoryLayout current;
    protected String currentLayout;

    public Sorter() {
        super("Sorter", Category.Player, "Add_Layout", "layout");
        Bus.EVENT_BUS.register(new LambdaListener<ShutDownEvent>(ShutDownEvent.class, e -> this.saveConfig()));
        this.listeners.add(new ListenerMotion(this));
    }

    public boolean scroll(int direction) {
        if (!this.isEnabled() || !this.virtualHotbar.getValue().booleanValue()) {
            return false;
        }
        Map<Integer, Integer> mapping = this.mapping;
        Map<Integer, Integer> reverse = this.reverse;
        if (mapping == null || reverse == null) {
            return false;
        }
        if (direction > 0) {
            direction = 1;
        }
        if (direction < 0) {
            direction = -1;
        }
        int c = Sorter.mc.player.inventory.currentItem;
        int curr = reverse.getOrDefault(c, c);
        if ((curr -= direction) < 0) {
            curr = 8;
        } else if (curr > 8) {
            curr = 0;
        }
        Sorter.mc.player.inventory.currentItem = mapping.getOrDefault(curr, curr);
        return true;
    }

    public void updateMapping() {
        if (!this.isEnabled() || !this.virtualHotbar.getValue().booleanValue()) {
            return;
        }
        InventoryLayout current = this.current;
        if (current == null) {
            return;
        }
        HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>(14, 0.75f);
        HashMap<Integer, Integer> reverse = new HashMap<Integer, Integer>(14, 0.75f);
        ArrayList<Integer> empty = new ArrayList<Integer>();
        for (int i = 0; i < 9; ++i) {
            Item item = current.getItem(InventoryUtil.hotbarToInventory(i));
            if (item == Items.AIR) {
                empty.add(i);
                continue;
            }
            boolean notFound = true;
            for (int j = 0; j < 9; ++j) {
                if (mapping.containsValue(j) || item != Sorter.mc.player.inventory.getStackInSlot(j).getItem()) continue;
                mapping.put(i, j);
                reverse.put(j, i);
                notFound = false;
                break;
            }
            if (!notFound) continue;
            empty.add(i);
        }
        Iterator iterator = empty.iterator();
        block2: while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            for (int j = 0; j < 9; ++j) {
                if (mapping.containsValue(j)) continue;
                mapping.put(i, j);
                reverse.put(j, i);
                continue block2;
            }
        }
        this.reverse = reverse;
        this.mapping = mapping;
    }

    public int getReverseMapping(int slot) {
        return this.getMapping(slot, this.reverse);
    }

    public int getHotbarMapping(int slot) {
        return this.getMapping(slot, this.mapping);
    }

    private int getMapping(int slot, Map<Integer, Integer> mapping) {
        if (!this.isEnabled() || !this.virtualHotbar.getValue().booleanValue() || mapping == null) {
            return slot;
        }
        return mapping.getOrDefault(slot, slot);
    }

    @Override
    protected void onLoad() {
        this.loadConfig();
        this.currentLayout = CurrentConfig.getInstance().get("sorter");
        if (this.currentLayout == null) {
            this.currentLayout = this.layouts.keySet().stream().findFirst().orElse(null);
        }
        if (this.currentLayout != null) {
            this.current = this.layouts.get(this.currentLayout);
        }
    }

    @Override
    public void add(String string) {
        InventoryLayout layout = InventoryLayout.createFromMcPlayer();
        this.layouts.put(string.toLowerCase(), layout);
        this.load(string.toLowerCase());
    }

    @Override
    public void del(String string) {
        this.layouts.remove(string.toLowerCase());
        if (string.equalsIgnoreCase(this.currentLayout)) {
            this.currentLayout = this.layouts.keySet().stream().findFirst().orElse(null);
            this.load(this.currentLayout);
        }
    }

    @Override
    protected void load(String string, boolean noArgGiven) {
        if (noArgGiven) {
            ChatUtil.sendMessage("\u00a7cPlease specify a Layout to load!");
            return;
        }
        if (this.load(string)) {
            ChatUtil.sendMessage("\u00a7cCouldn't find layout \u00a7f" + string + "\u00a7c" + "!");
        } else {
            ChatUtil.sendMessage("\u00a7aLayout \u00a7f" + string + "\u00a7a" + " loaded successfully.");
        }
    }

    @Override
    protected String getLoadableStartingWith(String string) {
        string = string.toLowerCase();
        for (String s : this.layouts.keySet()) {
            if (!s.toLowerCase().startsWith(string.toLowerCase())) continue;
            return s;
        }
        return null;
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("add") && Sorter.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be in game to add a new Inventory Layout.");
            return true;
        }
        return super.execute(args);
    }

    @Override
    public String getInput(String input, boolean add) {
        String s = this.getLoadableStartingWith(input);
        if (s == null) {
            return "";
        }
        return TextUtil.substring(s, input.length());
    }

    public boolean load(String layout) {
        InventoryLayout l = this.layouts.get(layout);
        if (l == null) {
            return true;
        }
        this.currentLayout = layout;
        this.current = l;
        return false;
    }

    public void loadConfig() {
        this.layouts.clear();
        FileUtil.createDirectory(Paths.get(PATH, new String[0]));
        Path path = Paths.get(JSON_PATH, new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        try (InputStream stream = Files.newInputStream(path, new OpenOption[0]);){
            JsonObject object = Jsonable.PARSER.parse(new InputStreamReader(stream)).getAsJsonObject();
            for (Map.Entry entry : object.entrySet()) {
                InventoryLayout layout = new InventoryLayout();
                layout.fromJson((JsonElement)entry.getValue());
                this.layouts.put(((String)entry.getKey()).toLowerCase(), layout);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        if (this.currentLayout != null) {
            CurrentConfig.getInstance().set("sorter", this.currentLayout);
        }
        FileUtil.createDirectory(Paths.get(PATH, new String[0]));
        JsonObject object = new JsonObject();
        for (Map.Entry<String, InventoryLayout> entry : this.layouts.entrySet()) {
            object.add(entry.getKey().toLowerCase(), Jsonable.parse(entry.getValue().toJson(), false));
        }
        try {
            JsonPathWriter.write(Paths.get(JSON_PATH, new String[0]), object);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
