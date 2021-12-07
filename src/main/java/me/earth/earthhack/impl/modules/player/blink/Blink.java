package me.earth.earthhack.impl.modules.player.blink;

import java.util.LinkedList;
import java.util.Queue;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.player.blink.ListenerPacket;
import me.earth.earthhack.impl.modules.player.blink.ListenerPosLook;
import me.earth.earthhack.impl.modules.player.blink.mode.PacketMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;

public class Blink
extends DisablingModule {
    protected final Setting<PacketMode> packetMode = this.register(new EnumSetting<PacketMode>("Packets", PacketMode.CPacketPlayer));
    protected final Setting<Boolean> lagDisable = this.register(new BooleanSetting("LagDisable", false));
    protected final Queue<Packet<?>> packets = new LinkedList();
    protected EntityOtherPlayerMP fakePlayer;
    protected boolean shouldSend;

    public Blink() {
        super("Blink", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerPacket(this));
        SimpleData data = new SimpleData(this, "Suppresses all movement packets send to the server. It will look like you don't move at all and then teleport when you disable this module.");
        data.register(this.packetMode, "-All cancels all packets. Will cause packet spam.\n-CPacketPlayer only cancels movement packets.\nFiltered leaves some packets through, still spammy.");
        data.register(this.lagDisable, "Disable this module when the server lags you back.");
        this.setData(data);
    }

    @Override
    protected void onEnable() {
        if (Blink.mc.player == null) {
            this.disable();
            return;
        }
        this.fakePlayer = PlayerUtil.createFakePlayerAndAddToWorld(Blink.mc.player.getGameProfile());
    }

    @Override
    protected void onDisable() {
        PlayerUtil.removeFakePlayer(this.fakePlayer);
        if (this.shouldSend && mc.getConnection() != null) {
            CollectionUtil.emptyQueue(this.packets, p -> mc.getConnection().sendPacket((Packet)p));
        } else {
            this.packets.clear();
        }
        this.shouldSend = true;
    }

    @Override
    public void onShutDown() {
        this.shouldSend = false;
        super.onShutDown();
    }

    @Override
    public void onDeath() {
        this.shouldSend = false;
        super.onShutDown();
    }

    @Override
    public void onDisconnect() {
        this.shouldSend = false;
        super.onShutDown();
    }
}
