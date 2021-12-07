/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.client.network.NetHandlerPlayClient
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLogData;
import me.earth.earthhack.impl.modules.misc.autolog.ListenerScreen;
import me.earth.earthhack.impl.modules.misc.autolog.ListenerTick;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;

public class AutoLog
extends Module {
    protected final Setting<Float> health = this.register(new NumberSetting<Float>("Health", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(19.5f)));
    protected final Setting<Integer> totems = this.register(new NumberSetting<Integer>("Totems", 0, 0, 10));
    protected final Setting<Float> enemy = this.register(new NumberSetting<Float>("Enemy", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> absorption = this.register(new BooleanSetting("Absorption", false));
    protected ServerData serverData;
    protected String message;
    protected boolean awaitScreen;

    public AutoLog() {
        super("AutoLog", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerScreen(this));
        this.setData(new AutoLogData(this));
    }

    @Override
    protected void onDisable() {
        this.awaitScreen = false;
    }

    public void disconnect(float health, EntityPlayer closest, int totems) {
        this.message = "AutoLogged with " + MathUtil.round(health, 1) + " health and " + totems + " Totem" + (totems == 1 ? "" : "s") + " remaining." + (closest == null ? "" : " Closest Enemy: " + closest.getName() + ".");
        this.serverData = mc.getCurrentServerData();
        this.awaitScreen = true;
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection == null) {
            AutoLog.mc.world.sendQuittingDisconnectingPacket();
        } else {
            ServerUtil.disconnectFromMC(this.message);
        }
    }
}

