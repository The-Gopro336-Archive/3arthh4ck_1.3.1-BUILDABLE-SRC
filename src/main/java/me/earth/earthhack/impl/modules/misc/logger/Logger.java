package me.earth.earthhack.impl.modules.misc.logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.misc.logger.ListenerChatLog;
import me.earth.earthhack.impl.modules.misc.logger.ListenerReceive;
import me.earth.earthhack.impl.modules.misc.logger.ListenerSend;
import me.earth.earthhack.impl.modules.misc.logger.LoggerData;
import me.earth.earthhack.impl.modules.misc.logger.util.LoggerMode;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.Packet;

public class Logger
extends RegisteringModule<Boolean, SimpleRemovingSetting> {
    protected final Setting<LoggerMode> mode = this.register(new EnumSetting<LoggerMode>("Mode", LoggerMode.Normal));
    protected final Setting<Boolean> incoming = this.register(new BooleanSetting("Incoming", true));
    protected final Setting<Boolean> outgoing = this.register(new BooleanSetting("Outgoing", true));
    protected final Setting<Boolean> info = this.register(new BooleanSetting("Info", true));
    protected final Setting<Boolean> chat = this.register(new BooleanSetting("Chat", false));
    protected final Setting<Boolean> deobfuscate = this.register(new BooleanSetting("Deobfuscate", true));
    protected final Setting<Boolean> stackTrace = this.register(new BooleanSetting("StackTrace", false));
    protected final Setting<Boolean> statics = this.register(new BooleanSetting("Static", false));
    protected final Setting<Boolean> filter = this.registerBefore(new BooleanSetting("Filter", false), this.listType);
    protected final List<String> packetNames = PacketUtil.getAllPackets().stream().map(MappingProvider::simpleName).collect(Collectors.toList());
    protected boolean cancel;

    public Logger() {
        super("Logger", Category.Misc, "Add_Packet", "packet", SimpleRemovingSetting::new, s -> "Filter " + s.getName() + " packets.");
        this.listeners.add(new ListenerChatLog(this));
        this.listeners.add(new ListenerReceive(this));
        this.listeners.add(new ListenerSend(this));
        this.setData(new LoggerData(this));
    }

    @Override
    protected void onEnable() {
        this.cancel = false;
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

    public void logPacket(Packet<?> packet, String message, boolean cancelled) {
        String simpleName = MappingProvider.simpleName(packet.getClass());
        if (this.filter.getValue().booleanValue() && !this.isValid(simpleName)) {
            return;
        }
        StringBuilder outPut = new StringBuilder(message).append(simpleName).append(", cancelled : ").append(cancelled).append("\n");
        if (this.info.getValue().booleanValue()) {
            try {
                for (Class<?> clazz = packet.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (field == null || Modifier.isStatic(field.getModifiers()) && !this.statics.getValue().booleanValue()) continue;
                        field.setAccessible(true);
                        outPut.append("     ").append(this.getName(clazz, field)).append(" : ").append(field.get(packet)).append("\n");
                    }
                }
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String s = outPut.toString();
        if (this.chat.getValue().booleanValue()) {
            mc.addScheduledTask(() -> {
                this.cancel = true;
                try {
                    ChatUtil.sendMessage(s);
                }
                finally {
                    this.cancel = false;
                }
            });
        }
        Earthhack.getLogger().info(s);
        if (this.stackTrace.getValue().booleanValue()) {
            Thread.dumpStack();
        }
    }

    private String getName(Class<?> c, Field field) {
        String name;
        if (this.deobfuscate.getValue().booleanValue() && (name = MappingProvider.field(c, field.getName())) != null) {
            return name;
        }
        return field.getName();
    }

    public LoggerMode getMode() {
        return this.mode.getValue();
    }
}
