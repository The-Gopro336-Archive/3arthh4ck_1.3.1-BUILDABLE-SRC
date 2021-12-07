package me.earth.earthhack.impl.modules.combat.offhand;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.offhand.ListenerGameLoop;
import me.earth.earthhack.impl.modules.combat.offhand.ListenerKeyboard;
import me.earth.earthhack.impl.modules.combat.offhand.ListenerRightClick;
import me.earth.earthhack.impl.modules.combat.offhand.ListenerSetSlot;
import me.earth.earthhack.impl.modules.combat.offhand.ListenerTotem;
import me.earth.earthhack.impl.modules.combat.offhand.OffhandData;
import me.earth.earthhack.impl.modules.combat.offhand.modes.HUDMode;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import org.lwjgl.input.Mouse;

public class Offhand
extends Module {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private static final ModuleCache<ServerAutoTotem> AUTOTOTEM = Caches.getModule(ServerAutoTotem.class);
    private static final ModuleCache<AutoArmor> AUTO_ARMOR = Caches.getModule(AutoArmor.class);
    private static final ModuleCache<XCarry> XCARRY = Caches.getModule(XCarry.class);
    private static final ModuleCache<Suicide> SUICIDE = Caches.getModule(Suicide.class);
    protected final Setting<Float> health = this.register(new NumberSetting<Float>("Health", Float.valueOf(14.5f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Float> safeH = this.register(new NumberSetting<Float>("SafeHealth", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Bind> gappleBind = this.register(new BindSetting("GappleBind", Bind.none()));
    protected final Setting<Bind> crystalBind = this.register(new BindSetting("CrystalBind", Bind.none()));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 25, 0, 500));
    protected final Setting<Boolean> cToTotem = this.register(new BooleanSetting("Crystal-Totem", true));
    protected final Setting<Boolean> swordGap = this.register(new BooleanSetting("Sword-Gapple", false));
    protected final Setting<Boolean> recover = this.register(new BooleanSetting("RecoverSwitch", true));
    protected final Setting<Boolean> noOGC = this.register(new BooleanSetting("AntiPlace", true));
    protected final Setting<Boolean> hotbarFill = this.register(new BooleanSetting("Totem-Hotbar", false));
    protected final Setting<HUDMode> hudMode = this.register(new EnumSetting<HUDMode>("HUDMode", HUDMode.Info));
    protected final Setting<Integer> timeOut = this.register(new NumberSetting<Integer>("TimeOut", 600, 0, 1000));
    protected final Setting<Boolean> crystalsIfNoTotem = this.register(new BooleanSetting("CrystalsIfNoTotem", false));
    protected final Setting<Boolean> async = this.register(new BooleanSetting("Async-Totem", false));
    protected final Setting<Integer> asyncCheck = this.register(new NumberSetting<Integer>("Async-Check", 100, 0, 1000));
    protected final Setting<Boolean> crystalCheck = this.register(new BooleanSetting("CrystalCheck", true));
    protected final Setting<Boolean> doubleClicks = this.register(new BooleanSetting("DoubleClicks", false));
    protected final Setting<Boolean> noMove = this.register(new BooleanSetting("NoMove", false));
    protected final Setting<Boolean> cancelActions = this.register(new BooleanSetting("CancelActions", false));
    protected final Setting<Boolean> cancelActive = this.register(new BooleanSetting("CancelActive", false));
    protected final Setting<Boolean> noDoubleGapple = this.register(new BooleanSetting("NoDoubleGapple", false));
    protected final Setting<Boolean> doubleGappleToCrystal = this.register(new BooleanSetting("DoubleGappleToCrystal", false));
    protected final Map<Item, Integer> lastSlots = new HashMap<Item, Integer>();
    protected final StopWatch setSlotTimer = new StopWatch();
    protected final StopWatch timeOutTimer = new StopWatch();
    protected final StopWatch asyncTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected OffhandMode mode = OffhandMode.TOTEM;
    protected OffhandMode recovery = null;
    protected volatile int asyncSlot = -1;
    protected boolean swordGapped;
    protected boolean lookedUp;
    protected boolean pulledFromHotbar;
    protected boolean sneaking;
    protected boolean sprinting;

    public Offhand() {
        super("Offhand", Category.Combat);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerKeyboard(this));
        this.listeners.add(new ListenerRightClick(this));
        this.listeners.add(new ListenerTotem(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.setData(new OffhandData(this));
    }

    @Override
    public String getDisplayInfo() {
        switch (this.hudMode.getValue()) {
            case Info: {
                return this.mode.getName();
            }
            case Name: {
                return InventoryUtil.getCount(this.mode.getItem()) + "";
            }
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        if (this.hudMode.getValue() == HUDMode.Name) {
            if (OffhandMode.TOTEM.equals(this.mode)) {
                return "AutoTotem";
            }
            return "Offhand" + this.mode.getName();
        }
        return super.getDisplayName();
    }

    public void setMode(OffhandMode mode) {
        this.mode = mode;
        this.recovery = mode.equals(OffhandMode.TOTEM) ? this.recovery : null;
        this.swordGapped = false;
    }

    public OffhandMode getMode() {
        return this.mode;
    }

    public void doOffhand() {
        if (Offhand.mc.player != null && this.timer.passed(this.delay.getValue().intValue()) && InventoryUtil.validScreen() && !SUICIDE.returnIfPresent(Suicide::deactivateOffhand, false).booleanValue()) {
            if ((Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe) && this.swordGap.getValue().booleanValue() && (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE || Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
                if (Mouse.isButtonDown(1) && OffhandMode.TOTEM.equals(this.mode)) {
                    this.mode = OffhandMode.GAPPLE;
                    this.swordGapped = true;
                } else if (this.swordGapped && !Mouse.isButtonDown(1) && OffhandMode.GAPPLE.equals(this.mode)) {
                    this.setMode(OffhandMode.TOTEM);
                }
            }
            if (!this.isSafe()) {
                this.setRecovery(this.mode);
                this.mode = OffhandMode.TOTEM;
            } else if (this.recover.getValue().booleanValue() && this.recovery != null && this.timeOutTimer.passed(this.timeOut.getValue().intValue())) {
                this.setMode(this.recovery);
            }
            int tSlot = InventoryUtil.findItem(Items.TOTEM_OF_UNDYING, true);
            int hotbar = InventoryUtil.findHotbarItem(Items.TOTEM_OF_UNDYING, new Item[0]);
            if (this.pulledFromHotbar && this.hotbarFill.getValue().booleanValue() && InventoryUtil.findEmptyHotbarSlot() != -1 && (hotbar == -1 || hotbar == -2) && tSlot != -1 && this.timer.passed(this.timeOut.getValue().intValue())) {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                    if (InventoryUtil.get(tSlot).getItem() == Items.TOTEM_OF_UNDYING) {
                        Offhand.mc.playerController.windowClick(0, tSlot, 0, ClickType.QUICK_MOVE, Offhand.mc.player);
                    }
                });
                this.postWindowClick();
                this.pulledFromHotbar = false;
            }
            if (this.crystalsIfNoTotem.getValue().booleanValue() && this.mode == OffhandMode.TOTEM && InventoryUtil.getCount(Items.TOTEM_OF_UNDYING) == 0 && InventoryUtil.getCount(Items.END_CRYSTAL) != 0 && this.setSlotTimer.passed(250L)) {
                this.mode = OffhandMode.CRYSTAL;
                this.swordGapped = false;
            }
            if (this.noDoubleGapple.getValue().booleanValue() && this.mode == OffhandMode.GAPPLE && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE) {
                this.mode = this.doubleGappleToCrystal.getValue() != false ? OffhandMode.CRYSTAL : OffhandMode.TOTEM;
                this.swordGapped = false;
            }
            this.switchToItem(this.mode.getItem());
        }
    }

    public void setRecovery(OffhandMode recoveryIn) {
        if (this.recover.getValue().booleanValue() && recoveryIn != null && !recoveryIn.equals(OffhandMode.TOTEM)) {
            this.recovery = recoveryIn;
            this.timeOutTimer.reset();
        }
    }

    private void switchToItem(Item item) {
        ItemStack drag = Offhand.mc.player.inventory.getItemStack();
        Item dragItem = drag.getItem();
        Item offhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
        if (offhandItem != item) {
            if (dragItem == item) {
                this.preWindowClick();
                InventoryUtil.clickLocked(-2, 45, dragItem, offhandItem);
                this.postWindowClick();
                this.lookedUp = false;
            } else {
                Integer last = this.lastSlots.get(item);
                int slot = last != null && InventoryUtil.get(last).getItem() == item ? last.intValue() : this.findItem(item);
                if (slot != -1 && slot != -2) {
                    this.lastSlots.put(item, slot);
                    this.lookedUp = false;
                    Item slotItem = InventoryUtil.get(slot).getItem();
                    this.preWindowClick();
                    if (this.doubleClicks.getValue().booleanValue()) {
                        InventoryUtil.clickLocked(slot, 45, slotItem, offhandItem);
                    } else {
                        InventoryUtil.clickLocked(-1, slot, null, slotItem);
                    }
                    this.postWindowClick();
                }
            }
        } else if (!drag.isEmpty() && !this.lookedUp) {
            Integer lastSlot = this.lastSlots.get(dragItem);
            if (lastSlot != null && InventoryUtil.get(lastSlot).isEmpty()) {
                this.preWindowClick();
                InventoryUtil.clickLocked(-2, lastSlot, dragItem, InventoryUtil.get(lastSlot).getItem());
                this.postWindowClick();
            } else {
                int slot = this.findItem(Items.AIR);
                if (slot != -1 && slot != -2) {
                    this.lastSlots.put(dragItem, slot);
                    this.preWindowClick();
                    InventoryUtil.clickLocked(-2, slot, dragItem, InventoryUtil.get(slot).getItem());
                    this.postWindowClick();
                }
            }
            this.lookedUp = true;
        }
    }

    private int findItem(Item item) {
        return InventoryUtil.findItem(item, XCARRY.isEnabled());
    }

    public boolean isSafe() {
        float playerHealth = EntityUtil.getHealth(Offhand.mc.player);
        if (this.crystalCheck.getValue().booleanValue() && Offhand.mc.player != null && Offhand.mc.world != null) {
            float highestDamage = Offhand.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(DamageUtil::calculate).max(Comparator.comparing(damage -> damage)).orElse(Float.valueOf(0.0f)).floatValue();
            playerHealth -= highestDamage;
        }
        return PINGBYPASS.isEnabled() && AUTOTOTEM.isEnabled() || Managers.SAFETY.isSafe() && playerHealth >= this.safeH.getValue().floatValue() || playerHealth >= this.health.getValue().floatValue();
    }

    public void preWindowClick() {
        if (this.noMove.getValue().booleanValue() && Managers.POSITION.isOnGround()) {
            PacketUtil.doPosition(Managers.POSITION.getX(), Managers.POSITION.getY(), Managers.POSITION.getZ(), Managers.POSITION.isOnGround());
        }
        this.sneaking = Managers.ACTION.isSneaking();
        this.sprinting = Managers.ACTION.isSprinting();
        if (this.cancelActions.getValue().booleanValue()) {
            if (this.sneaking) {
                PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SNEAKING);
            }
            if (this.sprinting) {
                PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SPRINTING);
            }
        }
        if (this.cancelActive.getValue().booleanValue()) {
            NetworkUtil.send(new CPacketHeldItemChange(Offhand.mc.player.inventory.currentItem));
        }
    }

    public void postWindowClick() {
        AUTO_ARMOR.computeIfPresent(AutoArmor::resetTimer);
        this.timer.reset();
        if (this.cancelActions.getValue().booleanValue()) {
            if (this.sneaking) {
                PacketUtil.sendAction(CPacketEntityAction.Action.START_SNEAKING);
            }
            if (this.sprinting) {
                PacketUtil.sendAction(CPacketEntityAction.Action.START_SPRINTING);
            }
        }
    }

    public StopWatch getTimer() {
        return this.timer;
    }
}
