package me.earth.earthhack.impl.modules.player.blocktweaks;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaksData;
import me.earth.earthhack.impl.modules.player.blocktweaks.ListenerPacket;
import me.earth.earthhack.impl.modules.player.blocktweaks.ListenerTick;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import org.lwjgl.input.Mouse;

public class BlockTweaks
extends RemovingItemAddingModule {
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("BreakDelay", 0, 0, 5));
    protected final Setting<Boolean> noBreakAnim = this.register(new BooleanSetting("NoBreakAnim", false));
    protected final Setting<Boolean> entityMine = this.register(new BooleanSetting("EntityMine", true));
    protected final Setting<Boolean> m1Attack = this.register(new BooleanSetting("RightAttack", false));
    protected final Setting<Boolean> ignoreFalling = this.register(new BooleanSetting("IgnoreFalling", false));
    protected final Setting<Boolean> newVerEntities = this.register(new BooleanSetting("1.13-Entities", false));

    public BlockTweaks() {
        super("BlockTweaks", Category.Player, s -> "Lets you mine through entities while holding " + s.getName());
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPacket(this));
        this.setData(new BlockTweaksData(this));
    }

    public boolean areNewVerEntitiesActive() {
        return this.isEnabled() && this.newVerEntities.getValue() != false;
    }

    public boolean isIgnoreFallingActive() {
        return this.isEnabled() && this.ignoreFalling.getValue() != false;
    }

    public boolean noMiningTrace() {
        return this.isEnabled() && this.entityMine.getValue() != false && this.isStackValid(BlockTweaks.mc.player.getHeldItemMainhand()) && (this.m1Attack.getValue() == false || !Mouse.isButtonDown(1));
    }
}
