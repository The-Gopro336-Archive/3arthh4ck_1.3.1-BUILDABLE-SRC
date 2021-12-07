package me.earth.earthhack.impl.modules.misc.antipackets;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.gui.visibility.NumberPageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.misc.antipackets.AntiPacketData;
import me.earth.earthhack.impl.modules.misc.antipackets.ListenerCPacket;
import me.earth.earthhack.impl.modules.misc.antipackets.ListenerSPacket;
import me.earth.earthhack.impl.modules.misc.antipackets.util.Page;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.Packet;

public class AntiPackets
extends Module {
    private final Map<Class<? extends Packet<?>>, BooleanSetting> client = new HashMap();
    private final Map<Class<? extends Packet<?>>, BooleanSetting> server = new HashMap();
    private final Setting<Boolean> unknown = new BooleanSetting("Unknown", false);
    private int settings;

    public AntiPackets() {
        super("AntiPackets", Category.Misc);
        this.listeners.add(new ListenerCPacket(this));
        this.listeners.add(new ListenerSPacket(this));
        AntiPacketData data = new AntiPacketData(this);
        this.setData(data);
        for (Class<Packet<?>> clazz : PacketUtil.getAllPackets()) {
            if (clazz.getName().contains("$")) continue;
            String simpleName = MappingProvider.simpleName(clazz);
            boolean side = simpleName.startsWith("S");
            this.getMap(side).put(clazz, new BooleanSetting(this.formatPacketName(simpleName), false));
            for (Class<?> inner : clazz.getDeclaredClasses()) {
                if (inner.getSuperclass() != clazz) continue;
                String sin = MappingProvider.simpleName(inner);
                BooleanSetting s2 = new BooleanSetting(this.formatPacketName(simpleName) + "-" + sin, false);
                this.getMap(side).put(inner, s2);
            }
        }
        EnumSetting<Page> pageEnumSetting = this.register(new EnumSetting<Page>("Page", Page.CPackets));
        this.client.values().forEach(s -> Visibilities.VISIBILITY_MANAGER.registerVisibility((Setting<?>)s, () -> pageEnumSetting.getValue() == Page.CPackets));
        this.server.values().forEach(s -> Visibilities.VISIBILITY_MANAGER.registerVisibility((Setting<?>)s, () -> pageEnumSetting.getValue() == Page.SPackets));
        Iterable<BooleanSetting> sortedC = this.sorted(this.client.values());
        Iterable<BooleanSetting> sortedS = this.sorted(this.server.values());
        this.registerSettings(sortedC, data);
        this.registerSettings(sortedS, data);
        Setting sPacketPages = NumberPageBuilder.autoPage(this, "SPackets", 8, sortedS).withConversion(Visibilities::andComposer).reapplyConversion().setPagePositionAfter("Page").register(Visibilities.VISIBILITY_MANAGER).registerPageSetting().getPageSetting();
        Setting cPacketPages = NumberPageBuilder.autoPage(this, "CPackets", 8, sortedC).withConversion(Visibilities::andComposer).reapplyConversion().setPagePositionAfter("Page").register(Visibilities.VISIBILITY_MANAGER).registerPageSetting().getPageSetting();
        Visibilities.VISIBILITY_MANAGER.registerVisibility(sPacketPages, () -> pageEnumSetting.getValue() == Page.SPackets);
        Visibilities.VISIBILITY_MANAGER.registerVisibility(cPacketPages, () -> pageEnumSetting.getValue() == Page.CPackets);
        Function<Setting, Observer> f = s -> e -> {
            if (!((Boolean)e.getValue()).equals(s.getValue())) {
                this.settings = ((Boolean)e.getValue()).booleanValue() ? ++this.settings : --this.settings;
            }
        };
        this.register(this.unknown).addObserver(f.apply(this.unknown));
        this.client.values().forEach(s -> s.addObserver((Observer)f.apply((Setting)s)));
        this.server.values().forEach(s -> s.addObserver((Observer)f.apply((Setting)s)));
        data.register(this.unknown.getName(), "Cancels unknown packets.");
    }

    @Override
    public String getDisplayInfo() {
        return this.settings + "";
    }

    private Iterable<BooleanSetting> sorted(Collection<BooleanSetting> settings) {
        return settings.stream().sorted(Comparator.comparing(Setting::getName)).collect(Collectors.toList());
    }

    private void registerSettings(Iterable<BooleanSetting> settings, AntiPacketData data) {
        for (BooleanSetting s : settings) {
            this.register(s);
            data.register(s.getName(), "Cancels " + s.getName() + " packets.");
        }
    }

    protected void onPacket(PacketEvent<?> event, boolean receive) {
        BooleanSetting s;
        BooleanSetting booleanSetting = s = receive ? this.server.get(event.getPacket().getClass()) : this.client.get(event.getPacket().getClass());
        if (s == null) {
            Earthhack.getLogger().info("Unknown packet: " + event.getPacket().getClass().getName());
            if (this.unknown.getValue().booleanValue()) {
                event.setCancelled(true);
            }
        } else if (((Boolean)s.getValue()).booleanValue()) {
            event.setCancelled(true);
        }
    }

    private String formatPacketName(String name) {
        if (name.startsWith("SPacket") || name.startsWith("CPacket")) {
            return name.charAt(0) + name.substring(7);
        }
        return name;
    }

    private Map<Class<? extends Packet<?>>, BooleanSetting> getMap(boolean side) {
        return side ? this.server : this.client;
    }
}
