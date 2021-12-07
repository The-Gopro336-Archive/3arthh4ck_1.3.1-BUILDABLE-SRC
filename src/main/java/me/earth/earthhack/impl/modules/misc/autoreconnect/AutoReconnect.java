/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDisconnected
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.ServerData
 */
package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.gui.util.IGuiDisconnected;
import me.earth.earthhack.impl.modules.misc.autoreconnect.AutoReconnectData;
import me.earth.earthhack.impl.modules.misc.autoreconnect.ListenerScreen;
import me.earth.earthhack.impl.modules.misc.autoreconnect.ListenerWorldClient;
import me.earth.earthhack.impl.modules.misc.autoreconnect.util.ReconnectScreen;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;

public class AutoReconnect
extends Module {
    private ServerData serverData;
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 5, 1, 60));

    public AutoReconnect() {
        super("AutoReconnect", Category.Misc);
        this.listeners.add(new ListenerScreen(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.setData(new AutoReconnectData(this));
    }

    protected void setServerData() {
        ServerData data = mc.getCurrentServerData();
        if (data != null) {
            this.serverData = data;
        }
    }

    protected void onGuiDisconnected(GuiDisconnected guiDisconnected) {
        this.setServerData();
        mc.displayGuiScreen((GuiScreen)new ReconnectScreen((IGuiDisconnected)guiDisconnected, this.serverData, this.delay.getValue() * 1000));
    }
}

