package me.earth.earthhack.impl.modules.misc.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.misc.chat.ChatData;
import me.earth.earthhack.impl.modules.misc.chat.ListenerChat;
import me.earth.earthhack.impl.modules.misc.chat.ListenerChatLog;
import me.earth.earthhack.impl.modules.misc.chat.ListenerGameLoop;
import me.earth.earthhack.impl.modules.misc.chat.ListenerLogout;
import me.earth.earthhack.impl.modules.misc.chat.ListenerPacket;
import me.earth.earthhack.impl.modules.misc.chat.ListenerTick;
import me.earth.earthhack.impl.modules.misc.chat.util.LoggerMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.gui.ChatLine;

public class Chat
extends Module {
    protected final Setting<Boolean> noScroll = this.register(new BooleanSetting("AntiScroll", true));
    protected final Setting<Boolean> timeStamps = this.register(new BooleanSetting("TimeStamps", false));
    public final Setting<Boolean> animated = this.register(new BooleanSetting("Animated", false));
    public final Setting<Integer> time = this.register(new NumberSetting<Integer>("AnimationTime", 200, 1, 500));
    protected final Setting<Boolean> autoQMain = this.register(new BooleanSetting("AutoQMain", false));
    protected final Setting<Integer> qDelay = this.register(new NumberSetting<Integer>("Q-Delay", 5000, 1, 10000));
    protected final Setting<String> message = this.register(new StringSetting("Q-Message", "/queue main"));
    protected final Setting<LoggerMode> log = this.register(new EnumSetting<LoggerMode>("Log", LoggerMode.Normal));
    protected final Queue<ChatEvent.Send> events = new ConcurrentLinkedQueue<ChatEvent.Send>();
    public final Map<ChatLine, TimeAnimation> animationMap = new HashMap<ChatLine, TimeAnimation>();
    protected final StopWatch timer = new StopWatch();
    protected boolean cleared;

    public Chat() {
        super("Chat", Category.Misc);
        this.register(new BooleanSetting("Clean", false));
        this.register(new BooleanSetting("Infinite", false));
        this.listeners.add(new ListenerPacket(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerChat(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerChatLog(this));
        this.noScroll.addObserver(event -> {
            if (!((Boolean)event.getValue()).booleanValue()) {
                Scheduler.getInstance().schedule(this::clearNoScroll);
            }
        });
        this.register(new BooleanSetting("Clear", false)).addObserver(e -> {
            e.setCancelled(true);
            if (Chat.mc.ingameGUI != null) {
                Chat.mc.ingameGUI.getChatGUI().clearChatMessages(true);
            }
        });
        this.setData(new ChatData(this));
    }

    @Override
    public void onDisable() {
        this.clearNoScroll();
    }

    public void clearNoScroll() {
        if (Chat.mc.ingameGUI != null) {
            CollectionUtil.emptyQueue(this.events, ChatEvent.Send::invoke);
        } else {
            this.events.clear();
        }
        this.cleared = true;
    }
}
