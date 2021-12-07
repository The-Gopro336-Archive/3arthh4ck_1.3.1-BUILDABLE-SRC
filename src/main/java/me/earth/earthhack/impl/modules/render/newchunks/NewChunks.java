package me.earth.earthhack.impl.modules.render.newchunks;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.render.newchunks.ListenerChunkData;
import me.earth.earthhack.impl.modules.render.newchunks.ListenerRender;
import me.earth.earthhack.impl.modules.render.newchunks.util.ChunkData;

public class NewChunks
extends Module {
    final Setting<Boolean> unload = this.register(new BooleanSetting("Unload", false));
    final Set<ChunkData> data = new ConcurrentSet();

    public NewChunks() {
        super("NewChunks", Category.Render);
        this.listeners.add(new ListenerChunkData(this));
        this.listeners.add(new ListenerRender(this));
    }

    @Override
    protected void onDisable() {
        this.data.clear();
    }
}
