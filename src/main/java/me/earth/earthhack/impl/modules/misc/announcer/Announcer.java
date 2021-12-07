/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.announcer.AnnouncerData;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerDeath;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerDigging;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerDisconnect;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerEat;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerJoin;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerLeave;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerMotion;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerPlace;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerSpawn;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerTotems;
import me.earth.earthhack.impl.modules.misc.announcer.ListenerWorldClient;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

public class Announcer
extends Module {
    private static final Random RANDOM = new Random();
    protected final Setting<Double> delay = this.register(new NumberSetting<Double>("Delay", 5.0, 0.0, 60.0));
    protected final Setting<Boolean> distance = this.register(new BooleanSetting("Distance", true));
    protected final Setting<Boolean> mine = this.register(new BooleanSetting("Mine", true));
    protected final Setting<Boolean> place = this.register(new BooleanSetting("Place", true));
    protected final Setting<Boolean> eat = this.register(new BooleanSetting("Eat", true));
    protected final Setting<Boolean> join = this.register(new BooleanSetting("Join", true));
    protected final Setting<Boolean> leave = this.register(new BooleanSetting("Leave", true));
    protected final Setting<Boolean> totems = this.register(new BooleanSetting("Totems", true));
    protected final Setting<Boolean> autoEZ = this.register(new BooleanSetting("AutoEZ", true));
    protected final Setting<Boolean> miss = this.register(new BooleanSetting("ArrowMiss", false));
    protected final Setting<Boolean> friends = this.register(new BooleanSetting("Friends", false));
    protected final Setting<Boolean> antiKick = this.register(new BooleanSetting("AntiKick", false));
    protected final Setting<Boolean> green = this.register(new BooleanSetting("GreenText", false));
    protected final Setting<Boolean> refresh = this.register(new BooleanSetting("Refresh", false));
    protected final Setting<Boolean> random = this.register(new BooleanSetting("Random", false));
    protected final Setting<Double> minDist = this.register(new NumberSetting<Double>("MinDistance", 10.0, 1.0, 100.0));
    protected final Map<AnnouncementType, Announcement> announcements = new ConcurrentHashMap<AnnouncementType, Announcement>();
    protected final Map<AnnouncementType, List<String>> messages = new ConcurrentHashMap<AnnouncementType, List<String>>();
    protected final Set<AnnouncementType> types = new HashSet<AnnouncementType>();
    protected final Set<EntityPlayer> targets = new HashSet<EntityPlayer>();
    protected final StopWatch timer = new StopWatch();
    double travelled;
    protected final Map<Integer, EntityPlayer> arrowMap = new ConcurrentHashMap<Integer, EntityPlayer>();

    public Announcer() {
        super("Announcer", Category.Misc);
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerJoin(this));
        this.listeners.add(new ListenerLeave(this));
        this.listeners.add(new ListenerPlace(this));
        this.listeners.add(new ListenerTotems(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerEat(this));
        this.listeners.add(new ListenerSpawn(this));
        this.setData(new AnnouncerData(this));
    }

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    protected void onLoad() {
        this.loadFiles();
    }

    public void reset() {
        this.travelled = 0.0;
        this.announcements.clear();
        this.types.clear();
        this.targets.clear();
    }

    public void loadFiles() {
        this.reset();
        this.messages.clear();
        for (AnnouncementType type : AnnouncementType.values()) {
            List<String> list = FileUtil.readFile(type.getFile(), true, Lists.newArrayList((Object[])new String[]{type.getDefaultMessage()}));
            this.messages.put(type, list);
        }
    }

    String getNextMessage() {
        int dist;
        for (Map.Entry<AnnouncementType, Announcement> entry : this.announcements.entrySet()) {
            if (entry == null || entry.getValue() == null || entry.getKey() == null || entry.getKey() == AnnouncementType.Distance || this.types.contains((Object)entry.getKey()) || !this.shouldAnnounce(entry.getKey())) continue;
            Announcement announcement = entry.getValue();
            this.types.add(entry.getKey());
            this.announcements.remove((Object)entry.getKey());
            return this.convert(entry.getKey(), announcement);
        }
        if (!this.types.isEmpty()) {
            this.types.clear();
            return this.getNextMessage();
        }
        if (this.distance.getValue().booleanValue() && (double)(dist = (int)this.travelled) > this.minDist.getValue()) {
            this.travelled = 0.0;
            return this.convert(AnnouncementType.Distance, new Announcement("Block", dist));
        }
        return null;
    }

    Announcement addWordAndIncrement(AnnouncementType type, String word) {
        Announcement announcement = this.announcements.get((Object)type);
        if (announcement != null && announcement.getName().equals(word)) {
            announcement.setAmount(announcement.getAmount() + 1);
            return announcement;
        }
        announcement = new Announcement(word, 1);
        this.announcements.put(type, announcement);
        return announcement;
    }

    private String convert(AnnouncementType type, Announcement announcement) {
        List<String> list = this.messages.get((Object)type);
        String text = null;
        if (list != null && !list.isEmpty()) {
            text = this.random.getValue() != false ? list.get(RANDOM.nextInt(list.size())) : list.get(0);
        }
        if (text == null) {
            text = type.getDefaultMessage();
        }
        return (this.green.getValue() != false ? ">" : "") + text.replace("<NUMBER>", Integer.toString(announcement.getAmount())).replace("<NAME>", announcement.getName()) + (this.antiKick.getValue() != false ? " " + ChatUtil.generateRandomHexSuffix(2) : "");
    }

    private boolean shouldAnnounce(AnnouncementType type) {
        switch (type) {
            case Distance: {
                return this.distance.getValue();
            }
            case Mine: {
                return this.mine.getValue();
            }
            case Place: {
                return this.place.getValue();
            }
            case Eat: {
                return this.eat.getValue();
            }
            case Join: {
                return this.join.getValue();
            }
            case Leave: {
                return this.leave.getValue();
            }
            case Totems: {
                return this.totems.getValue();
            }
            case Death: {
                return this.autoEZ.getValue();
            }
            case Miss: {
                return this.miss.getValue();
            }
        }
        return false;
    }
}

