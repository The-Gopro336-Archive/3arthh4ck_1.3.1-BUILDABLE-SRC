package me.earth.earthhack.impl.modules.render.search;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.render.search.ListenerBlockChange;
import me.earth.earthhack.impl.modules.render.search.ListenerBlockRender;
import me.earth.earthhack.impl.modules.render.search.ListenerMultiBlockChange;
import me.earth.earthhack.impl.modules.render.search.ListenerRender;
import me.earth.earthhack.impl.modules.render.search.ListenerUnloadChunk;
import me.earth.earthhack.impl.modules.render.search.ListenerWorldClient;
import me.earth.earthhack.impl.modules.render.search.SearchData;
import me.earth.earthhack.impl.modules.render.search.SearchResult;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.intintmap.IntIntMap;
import me.earth.earthhack.impl.util.misc.intintmap.IntIntMapImpl;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.render.WorldRenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class Search
extends RemovingItemAddingModule {
    protected final Setting<Boolean> lines = this.register(new BooleanSetting("Lines", true));
    protected final Setting<Boolean> fill = this.register(new BooleanSetting("Fill", false));
    protected final Setting<Boolean> tracers = this.register(new BooleanSetting("Tracers", false));
    protected final Setting<Boolean> softReload = this.register(new BooleanSetting("SoftReload", false));
    protected final Setting<Integer> maxBlocks = this.register(new NumberSetting<Integer>("Max-Blocks", 10000, 0, 10000));
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("Range", 256.0, 0.0, 512.0));
    protected final Setting<Boolean> countInRange = this.register(new BooleanSetting("Count-Range", false));
    protected final Setting<Boolean> coloredTracers = this.register(new BooleanSetting("Colored-Tracers", false));
    protected final Setting<Boolean> noUnloaded = this.register(new BooleanSetting("NoUnloaded", false));
    protected final Setting<Boolean> remove = this.register(new BooleanSetting("Remove", false));
    protected final Map<BlockPos, SearchResult> toRender = new ConcurrentHashMap<BlockPos, SearchResult>();
    protected final IntIntMap colors = new IntIntMapImpl(35, 0.75f);
    protected final StopWatch timer = new StopWatch();
    protected int found;

    public Search() {
        super("Search", Category.Render, s -> "Searches for " + s.getName() + ".");
        this.listType.setValue(ListType.WhiteList);
        this.listType.addObserver(e -> e.setValue(ListType.WhiteList));
        this.unregister(this.listType);
        this.listeners.add(new ListenerBlockRender(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerUnloadChunk(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.colors.put(5, -1517671851);
        this.colors.put(10, -7141377);
        this.colors.put(11, -7141547);
        this.colors.put(14, -1869610923);
        this.colors.put(15, -2140123051);
        this.colors.put(16, 0x20202055);
        this.colors.put(17, -1517671851);
        this.colors.put(21, 3170389);
        this.colors.put(26, -16777131);
        this.colors.put(41, -1869610923);
        this.colors.put(42, -2140123051);
        this.colors.put(49, 1696715042);
        this.colors.put(52, 8051029);
        this.colors.put(56, 9480789);
        this.colors.put(57, 9480789);
        this.colors.put(73, 0x60000055);
        this.colors.put(74, 0x60000055);
        this.colors.put(90, 1696715076);
        this.colors.put(98, 9480789);
        this.colors.put(112, 16728862);
        this.colors.put(129, 8396885);
        this.colors.put(162, -1517671851);
        this.colors.put(354, 9480789);
        this.setData(new SearchData(this));
    }

    @Override
    protected void onEnable() {
        this.toRender.clear();
        Scheduler.getInstance().schedule(this::reloadRenders);
    }

    @Override
    public String getDisplayInfo() {
        return this.found + "";
    }

    @Override
    protected SimpleRemovingSetting addSetting(String string) {
        SimpleRemovingSetting s = (SimpleRemovingSetting)super.addSetting(string);
        if (s != null) {
            this.reloadRenders();
        }
        return s;
    }

    @Override
    public Setting<?> unregister(Setting<?> setting) {
        Setting<?> s = super.unregister(setting);
        if (s != null) {
            this.toRender.clear();
            this.reloadRenders();
        }
        return s;
    }

    public void reloadRenders() {
        if (Search.mc.world != null && Search.mc.renderGlobal != null && Search.mc.player != null) {
            WorldRenderUtil.reload(this.softReload.getValue());
        }
    }

    public int getColor(IBlockState state) {
        int id = Block.getIdFromBlock((Block)state.getBlock());
        int color = this.colors.get(id);
        if (color != 0) {
            return color;
        }
        int blue = state.func_185904_a().getMaterialMapColor().colorValue;
        int red = blue >> 16 & 0xFF;
        int green = blue >> 8 & 0xFF;
        return ColorUtil.toARGB(red, green, blue &= 0xFF, 100);
    }
}
