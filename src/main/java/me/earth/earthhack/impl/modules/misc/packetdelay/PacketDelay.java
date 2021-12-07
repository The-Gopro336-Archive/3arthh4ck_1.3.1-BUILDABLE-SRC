/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 */
package me.earth.earthhack.impl.modules.misc.packetdelay;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.network.Packet;

public class PacketDelay
extends RegisteringModule<Boolean, SimpleRemovingSetting> {
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 0, 0, 5000));
    public final Set<Packet<?>> packets = Collections.newSetFromMap(new ConcurrentHashMap());
    public final ScheduledExecutorService service = ThreadUtil.newDaemonScheduledExecutor("Packet-Delay");
    public Integer lastDelay = null;
    protected final List<String> packetNames = PacketUtil.getAllPackets().stream().map(MappingProvider::simpleName).collect(Collectors.toList());

    public PacketDelay() {
        super("PacketDelay", Category.Misc, "Add_Packet", "packet", SimpleRemovingSetting::new, s -> "Filter " + s.getName() + " packets.");
    }

    @Override
    public String getInput(String input, boolean add) {
        if (add) {
            String packet = this.getPacketStartingWith(input);
            if (packet != null) {
                return TextUtil.substring(packet, input.length());
            }
            return "";
        }
        return super.getInput(input, false);
    }

    private String getPacketStartingWith(String input) {
        for (String packet : this.packetNames) {
            if (!TextUtil.startsWith(packet, input)) continue;
            return packet;
        }
        return null;
    }

    @Override
    protected void onEnable() {
        this.lastDelay = 0;
    }

    @Override
    protected void onDisable() {
        this.lastDelay = 0;
    }

    public int getDelay() {
        return this.delay.getValue();
    }

    public boolean isPacketValid(String packet) {
        return this.isValid(packet);
    }
}

