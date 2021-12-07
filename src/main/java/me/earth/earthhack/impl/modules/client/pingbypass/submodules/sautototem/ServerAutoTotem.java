package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.module.impl.SimpleSubModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ListenerSetSlot;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ListenerTick;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotemData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

public class ServerAutoTotem
extends SimpleSubModule<PingBypass> {
    private int count = 0;

    public ServerAutoTotem(PingBypass pingBypass) {
        super(pingBypass, "S-AutoTotem", Category.Client);
        this.register(new NumberSetting<Float>("Health", Float.valueOf(14.5f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
        this.register(new NumberSetting<Float>("SafeHealth", Float.valueOf(3.5f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
        this.register(new BooleanSetting("XCarry", false));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.setData(new ServerAutoTotemData(this));
    }

    @Override
    public String getDisplayInfo() {
        return Integer.toString(this.count);
    }

    protected void onTick() {
        if (ServerAutoTotem.mc.player != null) {
            this.count = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING);
        }
    }
}
