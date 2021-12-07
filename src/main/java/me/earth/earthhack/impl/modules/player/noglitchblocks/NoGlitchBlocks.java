package me.earth.earthhack.impl.modules.player.noglitchblocks;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.player.noglitchblocks.ListenerBlockDestroy;
import me.earth.earthhack.impl.util.client.SimpleData;

public class NoGlitchBlocks
extends Module {
    protected final Setting<Boolean> place = this.register(new BooleanSetting("Place", true));
    protected final Setting<Boolean> crack = this.register(new BooleanSetting("Break", true));
    protected final Setting<Boolean> ground = this.register(new BooleanSetting("Ground", false));

    public NoGlitchBlocks() {
        super("NoGlitchBlocks", Category.Player);
        this.listeners.add(new ListenerBlockDestroy(this));
        SimpleData data = new SimpleData(this, "Tries to prevent Glitchblocks.");
        data.register(this.place, "Prevents Glitchblocks when placing.");
        data.register(this.crack, "Prevents Glitchblocks when breaking blocks.");
        data.register(this.ground, "Always check for GlitchBlocks, not only when on the ground.");
        this.setData(data);
    }

    public boolean noPlace() {
        return this.isEnabled() && this.place.getValue() != false && (this.ground.getValue() != false || NoGlitchBlocks.mc.player.onGround);
    }

    public boolean noBreak() {
        return this.isEnabled() && this.crack.getValue() != false;
    }
}
