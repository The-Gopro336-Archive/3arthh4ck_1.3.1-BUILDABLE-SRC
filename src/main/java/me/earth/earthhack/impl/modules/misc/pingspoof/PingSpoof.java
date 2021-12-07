package me.earth.earthhack.impl.modules.misc.pingspoof;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.modules.misc.pingspoof.ListenerClickWindow;
import me.earth.earthhack.impl.modules.misc.pingspoof.ListenerKeepAlive;
import me.earth.earthhack.impl.modules.misc.pingspoof.ListenerLogout;
import me.earth.earthhack.impl.modules.misc.pingspoof.ListenerResources;
import me.earth.earthhack.impl.modules.misc.pingspoof.ListenerTransaction;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoofData;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.network.Packet;

public class PingSpoof
extends Module {
    private final ScheduledExecutorService service;
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 100, 1, 5000));
    protected final Setting<Boolean> keepAlive = this.register(new BooleanSetting("KeepAlive", true));
    protected final Setting<Boolean> transactions = this.register(new BooleanSetting("Transactions", false));
    protected final Setting<Boolean> resources = this.register(new BooleanSetting("Resources", false));
    protected final Queue<Packet<?>> packets = new ConcurrentLinkedQueue();
    protected final Set<Short> transactionIDs = new HashSet<Short>();

    public PingSpoof() {
        super("PingSpoof", Category.Misc);
        this.service = ThreadUtil.newDaemonScheduledExecutor("PingSpoof");
        Bus.EVENT_BUS.register(new EventListener<ShutDownEvent>(ShutDownEvent.class){

            @Override
            public void invoke(ShutDownEvent event) {
                PingSpoof.this.service.shutdown();
            }
        });
        this.listeners.add(new ListenerKeepAlive(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerTransaction(this));
        this.listeners.add(new ListenerClickWindow(this));
        this.listeners.add(new ListenerResources(this));
        this.setData(new PingSpoofData(this));
    }

    @Override
    protected void onDisable() {
        this.clearPackets(true);
    }

    public int getDelay() {
        return this.delay.getValue();
    }

    protected void clearPackets(boolean send) {
        this.transactionIDs.clear();
        CollectionUtil.emptyQueue(this.packets, packet -> {
            if (send) {
                NetworkUtil.sendPacketNoEvent(packet);
            }
        });
    }

    protected void onPacket(Packet<?> packet) {
        this.packets.add(packet);
        this.service.schedule(() -> {
            Packet<?> p;
            if (PingSpoof.mc.player != null && (p = this.packets.poll()) != null) {
                NetworkUtil.sendPacketNoEvent(p);
            }
        }, (long)this.delay.getValue().intValue(), TimeUnit.MILLISECONDS);
    }
}
