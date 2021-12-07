package me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.Serializer;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting.ListenerDisconnect;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting.ListenerSetting;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting.ListenerTick;
import net.minecraft.network.play.client.CPacketChatMessage;

public class SettingSerializer
extends SubscriberImpl
implements Globals,
Serializer<Setting<?>> {
    private static final Set<String> UNSERIALIZABLE = new HashSet<String>();
    private final Set<Module> modules = new HashSet<Module>();
    private final Set<Setting<?>> settings = new HashSet();
    private final Set<Setting<?>> changed = new LinkedHashSet();

    public SettingSerializer(Module ... modules) {
        this.init(new ListenerSetting(this), modules);
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerTick(this));
    }

    private void init(Observer observer, Module ... modules) {
        this.modules.addAll(Arrays.asList(modules));
        this.modules.forEach(module -> {
            if (module != null) {
                module.getSettings().forEach(setting -> {
                    if (this.isSettingSerializable((Setting<?>)setting)) {
                        setting.addObserver(observer);
                        this.settings.add((Setting<?>)setting);
                    }
                });
            }
        });
        this.clear();
    }

    public void onSettingChange(SettingEvent<?> event) {
        Setting<?> setting = event.getSetting();
        Scheduler.getInstance().schedule(() -> this.changed.add(setting));
    }

    protected void onTick() {
        Setting<?> setting;
        if (SettingSerializer.mc.player != null && mc.getConnection() != null && !this.changed.isEmpty() && (setting = this.pollSetting()) != null) {
            this.serializeAndSend(setting);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Set<Setting<?>> set = this.changed;
        synchronized (set) {
            this.changed.clear();
            this.changed.addAll(this.settings);
        }
    }

    private Setting<?> pollSetting() {
        if (!this.changed.isEmpty()) {
            Setting<?> setting = this.changed.iterator().next();
            this.changed.remove(setting);
            return setting;
        }
        return null;
    }

    @Override
    public void serializeAndSend(Setting<?> setting) {
        String name = null;
        if (setting.getContainer() instanceof Displayable) {
            name = ((Nameable)((Object)setting.getContainer())).getName();
        }
        String command = "@Server" + name + " " + setting.getName() + " " + setting.getValue().toString();
        Earthhack.getLogger().info(command);
        CPacketChatMessage packet = new CPacketChatMessage(command);
        Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
    }

    private boolean isSettingSerializable(Setting<?> setting) {
        return !UNSERIALIZABLE.contains(setting.getName());
    }

    static {
        UNSERIALIZABLE.add("Bind");
        UNSERIALIZABLE.add("Hidden");
        UNSERIALIZABLE.add("Name");
        UNSERIALIZABLE.add("IP");
        UNSERIALIZABLE.add("Port");
        UNSERIALIZABLE.add("Pings");
        UNSERIALIZABLE.add("Toggle");
    }
}
