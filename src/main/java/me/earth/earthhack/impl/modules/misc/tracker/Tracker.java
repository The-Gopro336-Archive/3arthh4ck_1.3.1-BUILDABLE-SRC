package me.earth.earthhack.impl.modules.misc.tracker;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.misc.tracker.ListenerChat;
import me.earth.earthhack.impl.modules.misc.tracker.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.misc.tracker.ListenerTick;
import me.earth.earthhack.impl.modules.misc.tracker.ListenerUseItem;
import me.earth.earthhack.impl.modules.misc.tracker.ListenerUseItemOnBlock;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class Tracker
extends DisablingModule
implements CustomCommandModule {
    protected final Setting<Boolean> autoEnable = this.register(new BooleanSetting("Auto-Enable", false));
    protected final Setting<Boolean> only1v1 = this.register(new BooleanSetting("1v1-Only", true));
    protected final Set<BlockPos> placed = new ConcurrentSet();
    protected final StopWatch timer = new StopWatch();
    protected final AtomicInteger awaitingExp = new AtomicInteger();
    protected final AtomicInteger crystals = new AtomicInteger();
    protected final AtomicInteger exp = new AtomicInteger();
    protected EntityPlayer trackedPlayer;
    protected boolean awaiting;
    protected int crystalStacks;
    protected int expStacks;

    public Tracker() {
        super("Tracker", Category.Misc);
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerUseItem(this));
        this.listeners.add(new ListenerUseItemOnBlock(this));
        Bus.EVENT_BUS.register(new ListenerChat(this));
        Bus.EVENT_BUS.register(new ListenerTick(this));
        SimpleData data = new SimpleData(this, "Tracks the items players use. Only recommended in a 1v1!");
        data.register(this.autoEnable, "Enables automatically when a duel starts.");
        data.register(this.only1v1, "Automatically disables when there's more than one player in render distance.");
        this.setData(data);
    }

    @Override
    protected void onEnable() {
        this.awaiting = false;
        this.trackedPlayer = null;
        this.awaitingExp.set(0);
        this.crystals.set(0);
        this.exp.set(0);
        this.crystalStacks = 0;
        this.expStacks = 0;
    }

    @Override
    public String getDisplayInfo() {
        return this.trackedPlayer == null ? null : this.trackedPlayer.getName();
    }

    @Override
    public boolean execute(String[] args) {
        EntityPlayer tracked;
        if (args.length == 1 && this.isEnabled() && (tracked = this.trackedPlayer) != null) {
            Scheduler.getInstance().schedule(() -> Scheduler.getInstance().schedule(() -> {
                int c = this.crystals.get();
                int e = this.exp.get();
                StringBuilder builder = new StringBuilder().append(tracked.getName()).append("\u00a7d").append(" has used ").append("\u00a7f").append(c).append("\u00a7d").append(" (").append("\u00a7f");
                if (c % 64 == 0) {
                    builder.append(c / 64);
                } else {
                    builder.append(MathUtil.round((double)c / 64.0, 1));
                }
                builder.append("\u00a7d").append(") crystals and ").append("\u00a7f").append(e).append("\u00a7d").append(" (").append("\u00a7f");
                if (e % 64 == 0) {
                    builder.append(e / 64);
                } else {
                    builder.append(MathUtil.round((double)e / 64.0, 1));
                }
                builder.append("\u00a7d").append(") bottles of experience.");
                ChatUtil.sendMessage(builder.toString());
            }));
        }
        return false;
    }
}
