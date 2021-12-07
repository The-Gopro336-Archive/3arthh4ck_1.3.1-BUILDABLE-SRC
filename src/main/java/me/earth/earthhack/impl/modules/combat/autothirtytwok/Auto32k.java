/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockDeadBush
 *  net.minecraft.block.BlockDispenser
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockHopper
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.BlockShulkerBox
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.client.gui.GuiHopper
 *  net.minecraft.client.gui.inventory.GuiDispenser
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.ContainerHopper
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketCloseWindow
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.ListenerCPacketCloseWindow;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.ListenerCPacketPlayer;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.ListenerGuiOpen;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.ListenerKeyPress;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.ListenerMotion;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.input.Keyboard;

public class Auto32k
extends Module {
    private static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    protected Setting<Mode> mode = this.register(new EnumSetting<Mode>("Mode", Mode.NORMAL));
    protected final Setting<Boolean> swing = this.register(new BooleanSetting("Swing", false));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay/Place", 25, 0, 250));
    protected final Setting<Integer> delayDispenser = this.register(new NumberSetting<Integer>("Blocks/Place", 1, 1, 8));
    protected final Setting<Integer> blocksPerPlace = this.register(new NumberSetting<Integer>("Actions/Place", 1, 1, 3));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Boolean> raytrace = this.register(new BooleanSetting("Raytrace", false));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> autoSwitch = this.register(new BooleanSetting("AutoSwitch", false));
    protected final Setting<Boolean> withBind = this.register(new BooleanSetting("WithBind", false));
    protected final Setting<Bind> switchBind = this.register(new BindSetting("SwitchBind", Bind.none()));
    protected final Setting<Double> targetRange = this.register(new NumberSetting<Double>("TargetRange", 6.0, 0.0, 20.0));
    protected final Setting<Boolean> extra = this.register(new BooleanSetting("ExtraRotation", false));
    protected final Setting<PlaceType> placeType = this.register(new EnumSetting<PlaceType>("Place", PlaceType.CLOSE));
    protected final Setting<Boolean> freecam = this.register(new BooleanSetting("Freecam", false));
    protected final Setting<Boolean> onOtherHoppers = this.register(new BooleanSetting("UseHoppers", false));
    protected final Setting<Boolean> preferObby = this.register(new BooleanSetting("UseObby", false));
    protected final Setting<Boolean> checkForShulker = this.register(new BooleanSetting("CheckShulker", true));
    protected final Setting<Integer> checkDelay = this.register(new NumberSetting<Integer>("CheckDelay", 500, 0, 500));
    protected final Setting<Boolean> drop = this.register(new BooleanSetting("Drop", false));
    protected final Setting<Boolean> mine = this.register(new BooleanSetting("Mine", false));
    protected final Setting<Boolean> checkStatus = this.register(new BooleanSetting("CheckState", true));
    protected final Setting<Boolean> packet = this.register(new BooleanSetting("Packet", false));
    protected final Setting<Boolean> superPacket = this.register(new BooleanSetting("DispExtra", false));
    protected final Setting<Boolean> secretClose = this.register(new BooleanSetting("SecretClose", false));
    protected final Setting<Boolean> closeGui = this.register(new BooleanSetting("CloseGui", false));
    protected final Setting<Boolean> repeatSwitch = this.register(new BooleanSetting("SwitchOnFail", true));
    protected final Setting<Boolean> simulate = this.register(new BooleanSetting("Simulate", true));
    protected final Setting<Float> hopperDistance = this.register(new NumberSetting<Float>("HopperRange", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Integer> trashSlot = this.register(new NumberSetting<Integer>("32kSlot", 0, 0, 9));
    protected final Setting<Boolean> messages = this.register(new BooleanSetting("Messages", false));
    protected final Setting<Boolean> antiHopper = this.register(new BooleanSetting("AntiHopper", false));
    private float yaw;
    private float pitch;
    private boolean spoof;
    public boolean switching;
    private int lastHotbarSlot = -1;
    private int shulkerSlot = -1;
    private int hopperSlot = -1;
    private BlockPos hopperPos;
    private EntityPlayer target;
    public Step currentStep = Step.PRE;
    private final StopWatch placeTimer = new StopWatch();
    private static Auto32k instance;
    private int obbySlot = -1;
    private int dispenserSlot = -1;
    private int redstoneSlot = -1;
    private DispenserData finalDispenserData;
    private int actionsThisTick = 0;
    private boolean checkedThisTick = false;
    private boolean authSneakPacket = false;
    private StopWatch disableTimer = new StopWatch();
    private boolean shouldDisable;
    private boolean rotationprepared = false;

    public Auto32k() {
        super("Auto32k", Category.Combat);
        instance = this;
        this.listeners.add(new ListenerCPacketCloseWindow(this));
        this.listeners.addAll(new ListenerCPacketPlayer(this).getListeners());
        this.listeners.add(new ListenerGuiOpen(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.listeners.add(new ListenerMotion(this));
        this.setData(new SimpleData(this, "Port of the awful old Phobos Auto32k."));
    }

    public static Auto32k getInstance() {
        if (instance == null) {
            instance = new Auto32k();
        }
        return instance;
    }

    @Override
    public void onEnable() {
        this.checkedThisTick = false;
        this.resetFields();
        if (Auto32k.mc.currentScreen instanceof GuiHopper) {
            this.currentStep = Step.HOPPERGUI;
        }
        if (this.mode.getValue() == Mode.NORMAL && this.autoSwitch.getValue().booleanValue() && !this.withBind.getValue().booleanValue()) {
            this.switching = true;
        }
    }

    public void onUpdateWalkingPlayer(MotionUpdateEvent event) {
        if (event.getStage() != Stage.PRE) {
            return;
        }
        if (this.shouldDisable && this.disableTimer.passed(1000L)) {
            this.shouldDisable = false;
            this.disable();
            return;
        }
        this.checkedThisTick = false;
        this.actionsThisTick = 0;
        if (!this.isEnabled() || this.mode.getValue() == Mode.NORMAL && this.autoSwitch.getValue().booleanValue() && !this.switching) {
            return;
        }
        if (this.mode.getValue() == Mode.NORMAL) {
            this.normal32k();
        } else {
            this.processDispenser32k();
        }
    }

    protected void onGui(GuiScreenEvent<?> event) {
        if (!this.isEnabled()) {
            return;
        }
        if (!this.secretClose.getValue().booleanValue() && Auto32k.mc.currentScreen instanceof GuiHopper) {
            if (this.drop.getValue().booleanValue() && Auto32k.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && this.hopperPos != null) {
                int pickaxeSlot;
                Auto32k.mc.player.dropItem(true);
                if (this.mine.getValue().booleanValue() && this.hopperPos != null && (pickaxeSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE, new Item[0])) != -1) {
                    InventoryUtil.switchTo(pickaxeSlot);
                    if (this.rotate.getValue().booleanValue()) {
                        this.rotateToPos(this.hopperPos.up(), null);
                    }
                    Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                    Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                    Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
            this.resetFields();
            if (this.mode.getValue() != Mode.NORMAL) {
                this.disable();
                return;
            }
            if (!this.autoSwitch.getValue().booleanValue() || this.mode.getValue() == Mode.DISPENSER) {
                this.disable();
            } else if (!this.withBind.getValue().booleanValue()) {
                this.disable();
            }
        } else if (event.getScreen() instanceof GuiHopper) {
            this.currentStep = Step.HOPPERGUI;
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "\u00a7aSwitch";
        }
        return null;
    }

    protected void onKeyInput(KeyboardEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        if (Keyboard.getEventKeyState() && this.switchBind.getValue().getKey() == Keyboard.getEventKey() && this.withBind.getValue().booleanValue()) {
            if (this.switching) {
                this.resetFields();
                this.switching = true;
            }
            this.switching = !this.switching;
        }
    }

    protected void onSettingChange(SettingEvent event) {
        if (event.getSetting().getContainer() == this) {
            this.resetFields();
        }
    }

    protected void onCPacketPlayer(CPacketPlayer packet) {
        if ((packet instanceof CPacketPlayer.PositionRotation || packet instanceof CPacketPlayer.Rotation) && this.spoof) {
            ((ICPacketPlayer)packet).setYaw(this.yaw);
            ((ICPacketPlayer)packet).setPitch(this.pitch);
            this.spoof = false;
        }
    }

    protected void onCPacketCloseWindow(PacketEvent.Send<CPacketCloseWindow> event) {
        if (!this.secretClose.getValue().booleanValue() && Auto32k.mc.currentScreen instanceof GuiHopper && this.hopperPos != null) {
            if (this.drop.getValue().booleanValue() && Auto32k.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                int pickaxeSlot;
                Auto32k.mc.player.dropItem(true);
                if (this.mine.getValue().booleanValue() && (pickaxeSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE, new Item[0])) != -1) {
                    InventoryUtil.switchTo(pickaxeSlot);
                    if (this.rotate.getValue().booleanValue()) {
                        this.rotateToPos(this.hopperPos.up(), null);
                    }
                    Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                    Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                    Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
            this.resetFields();
            if (!this.autoSwitch.getValue().booleanValue() || this.mode.getValue() == Mode.DISPENSER) {
                this.disable();
            } else if (!this.withBind.getValue().booleanValue()) {
                this.disable();
            }
        } else if (this.secretClose.getValue().booleanValue() && (!this.autoSwitch.getValue().booleanValue() || this.switching || this.mode.getValue() == Mode.DISPENSER) && this.currentStep == Step.HOPPERGUI) {
            event.setCancelled(true);
        }
    }

    private void normal32k() {
        if (this.autoSwitch.getValue().booleanValue()) {
            if (this.switching) {
                this.processNormal32k();
            } else {
                this.resetFields();
            }
        } else {
            this.processNormal32k();
        }
    }

    private void processNormal32k() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.placeTimer.passed(this.delay.getValue().intValue())) {
            this.check();
            switch (this.currentStep) {
                case PRE: {
                    this.runPreStep();
                    if (this.currentStep == Step.PRE) break;
                }
                case HOPPER: {
                    if (this.currentStep == Step.HOPPER) {
                        this.checkState();
                        if (this.currentStep == Step.PRE) {
                            if (this.checkedThisTick) {
                                this.processNormal32k();
                            }
                            return;
                        }
                        this.runHopperStep();
                        if (this.actionsThisTick >= this.blocksPerPlace.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue())) break;
                    }
                }
                case SHULKER: {
                    this.checkState();
                    if (this.currentStep == Step.PRE) {
                        if (this.checkedThisTick) {
                            this.processNormal32k();
                        }
                        return;
                    }
                    this.runShulkerStep();
                    if (this.actionsThisTick >= this.blocksPerPlace.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue())) break;
                }
                case CLICKHOPPER: {
                    this.checkState();
                    if (this.currentStep == Step.PRE) {
                        if (this.checkedThisTick) {
                            this.processNormal32k();
                        }
                        return;
                    }
                    this.runClickHopper();
                }
                case HOPPERGUI: {
                    this.runHopperGuiStep();
                    break;
                }
                default: {
                    this.currentStep = Step.PRE;
                }
            }
        }
    }

    private void runPreStep() {
        if (!this.isEnabled()) {
            return;
        }
        PlaceType type = this.placeType.getValue();
        if (FREECAM.isEnabled() && !this.freecam.getValue().booleanValue()) {
            if (this.messages.getValue().booleanValue()) {
                Managers.CHAT.sendDeleteMessage("\u00a7c<Auto32k> Disable freecam.", this.getDisplayName(), 2000);
            }
            if (this.autoSwitch.getValue().booleanValue()) {
                this.resetFields();
                if (!this.withBind.getValue().booleanValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
            return;
        }
        this.lastHotbarSlot = Auto32k.mc.player.inventory.currentItem;
        this.hopperSlot = InventoryUtil.findHotbarBlock((Block)Blocks.HOPPER, new Block[0]);
        this.shulkerSlot = InventoryUtil.findInHotbar(item -> item.getItem() instanceof ItemShulkerBox);
        if (Auto32k.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)Auto32k.mc.player.getHeldItemOffhand().getItem()).getBlock();
            if (block instanceof BlockShulkerBox) {
                this.shulkerSlot = -2;
            } else if (block instanceof BlockHopper) {
                this.hopperSlot = -2;
            }
        }
        if (this.shulkerSlot == -1 || this.hopperSlot == -1) {
            if (this.messages.getValue().booleanValue()) {
                Managers.CHAT.sendDeleteMessage("\u00a7c<Auto32k> Materials not found.", this.getDisplayName(), 2000);
            }
            if (this.autoSwitch.getValue().booleanValue()) {
                this.resetFields();
                if (!this.withBind.getValue().booleanValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
            return;
        }
        this.target = EntityUtil.getClosestEnemy();
        if (this.target == null || Auto32k.mc.player.getDistanceSq((Entity)this.target) > MathUtil.square(this.targetRange.getValue())) {
            if (this.autoSwitch.getValue().booleanValue()) {
                if (this.switching) {
                    this.resetFields();
                    this.switching = true;
                } else {
                    this.resetFields();
                }
                return;
            }
            type = this.placeType.getValue() == PlaceType.MOUSE ? PlaceType.MOUSE : PlaceType.CLOSE;
        }
        this.hopperPos = this.findBestPos(type, this.target);
        if (this.hopperPos != null) {
            this.currentStep = Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper ? Step.SHULKER : Step.HOPPER;
        } else {
            if (this.messages.getValue().booleanValue()) {
                Managers.CHAT.sendDeleteMessage("\u00a7c<Auto32k> Block not found.", this.getDisplayName(), 2000);
            }
            if (this.autoSwitch.getValue().booleanValue()) {
                this.resetFields();
                if (!this.withBind.getValue().booleanValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
        }
    }

    private void runHopperStep() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.currentStep == Step.HOPPER) {
            this.runPlaceStep(this.hopperPos, this.hopperSlot);
            this.currentStep = Step.SHULKER;
        }
    }

    private void runShulkerStep() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.currentStep == Step.SHULKER) {
            this.runPlaceStep(this.hopperPos.up(), this.shulkerSlot);
            this.currentStep = Step.CLICKHOPPER;
        }
    }

    private void runClickHopper() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.currentStep != Step.CLICKHOPPER) {
            return;
        }
        if (this.mode.getValue() == Mode.NORMAL && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) && this.checkForShulker.getValue().booleanValue()) {
            if (this.placeTimer.passed(this.checkDelay.getValue().intValue())) {
                this.currentStep = Step.SHULKER;
            }
            return;
        }
        this.clickBlock(this.hopperPos);
        this.currentStep = Step.HOPPERGUI;
    }

    private void runHopperGuiStep() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.currentStep != Step.HOPPERGUI) {
            return;
        }
        if (Auto32k.mc.player.openContainer instanceof ContainerHopper) {
            if (!Auto32k.holding32k((EntityPlayer)Auto32k.mc.player)) {
                int swordIndex = -1;
                for (int i = 0; i < 5; ++i) {
                    if (!Auto32k.is32k(((Slot)Auto32k.mc.player.openContainer.inventorySlots.get((int)0)).inventory.getStackInSlot(i))) continue;
                    swordIndex = i;
                    break;
                }
                if (swordIndex == -1) {
                    return;
                }
                if (this.trashSlot.getValue() != 0) {
                    InventoryUtil.switchTo(this.trashSlot.getValue() - 1);
                } else if (this.mode.getValue() != Mode.NORMAL && this.shulkerSlot > 35 && this.shulkerSlot != 45) {
                    InventoryUtil.switchTo(44 - this.shulkerSlot);
                }
                Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, swordIndex, this.trashSlot.getValue() == 0 ? Auto32k.mc.player.inventory.currentItem : this.trashSlot.getValue() - 1, ClickType.SWAP, (EntityPlayer)Auto32k.mc.player);
            } else if (this.closeGui.getValue().booleanValue() && this.secretClose.getValue().booleanValue()) {
                Auto32k.mc.player.closeScreen();
            }
        } else if (Auto32k.holding32k((EntityPlayer)Auto32k.mc.player)) {
            if (this.autoSwitch.getValue().booleanValue() && this.mode.getValue() == Mode.NORMAL) {
                this.switching = false;
            } else if (!this.autoSwitch.getValue().booleanValue() || this.mode.getValue() == Mode.DISPENSER) {
                this.shouldDisable = true;
                this.disableTimer.reset();
            }
        }
    }

    private void runPlaceStep(BlockPos pos, int slot) {
        if (!this.isEnabled()) {
            return;
        }
        EnumFacing side = EnumFacing.UP;
        if (this.antiHopper.getValue().booleanValue() && this.currentStep == Step.HOPPER) {
            boolean foundfacing = false;
            for (EnumFacing facing : EnumFacing.values()) {
                if (Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER || Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) continue;
                foundfacing = true;
                side = facing;
                break;
            }
            if (!foundfacing) {
                this.resetFields();
                return;
            }
        } else {
            side = BlockUtil.getFacing(pos);
            if (side == null) {
                this.resetFields();
                return;
            }
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = Auto32k.mc.world.getBlockState(neighbour).getBlock();
        this.authSneakPacket = true;
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.authSneakPacket = false;
        if (this.rotate.getValue().booleanValue()) {
            if (this.blocksPerPlace.getValue() > 1) {
                float[] angle = RotationUtil.getRotations(hitVec);
                if (this.extra.getValue().booleanValue()) {
                    Auto32k.faceYawAndPitch(angle[0], angle[1]);
                }
            } else {
                this.rotateToPos(null, hitVec);
            }
        }
        InventoryUtil.switchTo(slot);
        Auto32k.rightClickBlock(neighbour, hitVec, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, this.packet.getValue(), this.swing.getValue());
        this.authSneakPacket = true;
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.authSneakPacket = false;
        this.placeTimer.reset();
        ++this.actionsThisTick;
    }

    private BlockPos findBestPos(PlaceType type, EntityPlayer target) {
        BlockPos pos = null;
        NonNullList positions = NonNullList.create();
        BlockPos middle = PositionUtil.getPosition();
        int maxRadius = Sphere.getRadius(this.range.getValue().floatValue());
        for (int i = 1; i < maxRadius; ++i) {
            BlockPos pos1 = middle.add(Sphere.get(i));
            if (!this.canPlace(pos1)) continue;
            positions.add((Object)pos1);
        }
        if (positions.isEmpty()) {
            return null;
        }
        switch (type) {
            case MOUSE: {
                if (Auto32k.mc.objectMouseOver != null && Auto32k.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos mousePos = Auto32k.mc.objectMouseOver.getBlockPos();
                    if (mousePos != null && !this.canPlace(mousePos)) {
                        BlockPos mousePosUp = mousePos.up();
                        if (this.canPlace(mousePosUp)) {
                            pos = mousePosUp;
                        }
                    } else {
                        pos = mousePos;
                    }
                }
                if (pos != null) break;
            }
            case CLOSE: {
                positions.sort(Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                pos = (BlockPos)positions.get(0);
                break;
            }
            case ENEMY: {
                positions.sort(Comparator.comparingDouble(((EntityPlayer)target)::func_174818_b));
                pos = (BlockPos)positions.get(0);
                break;
            }
            case MIDDLE: {
                ArrayList<BlockPos> toRemove = new ArrayList<BlockPos>();
                NonNullList copy = NonNullList.create();
                copy.addAll((Collection)positions);
                for (BlockPos position : copy) {
                    double difference = Auto32k.mc.player.getDistanceSq(position) - target.getDistanceSq(position);
                    if (!(difference > 1.0) && !(difference < -1.0)) continue;
                    toRemove.add(position);
                }
                copy.removeAll(toRemove);
                if (copy.isEmpty()) {
                    copy.addAll((Collection)positions);
                }
                copy.sort(Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                pos = (BlockPos)copy.get(0);
                break;
            }
            case FAR: {
                positions.sort(Comparator.comparingDouble(pos2 -> -target.getDistanceSq(pos2)));
                pos = (BlockPos)positions.get(0);
                break;
            }
            case SAFE: {
                positions.sort(Comparator.comparingInt(pos2 -> -this.safetyFactor((BlockPos)pos2)));
                pos = (BlockPos)positions.get(0);
            }
        }
        return pos;
    }

    private boolean canPlace(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        BlockPos boost = pos.up();
        if (!this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos).getBlock(), this.onOtherHoppers.getValue()) || !this.isGoodMaterial(Auto32k.mc.world.getBlockState(boost).getBlock(), false)) {
            return false;
        }
        if (!(!this.raytrace.getValue().booleanValue() || Auto32k.rayTracePlaceCheck(pos, this.raytrace.getValue()) && Auto32k.rayTracePlaceCheck(pos, this.raytrace.getValue()))) {
            return false;
        }
        if (this.badEntities(pos) || this.badEntities(boost)) {
            return false;
        }
        if (this.onOtherHoppers.getValue().booleanValue() && Auto32k.mc.world.getBlockState(pos).getBlock() instanceof BlockHopper) {
            return true;
        }
        return this.findFacing(pos);
    }

    private void check() {
        if (!(this.currentStep == Step.PRE || this.currentStep == Step.HOPPER || this.hopperPos == null || Auto32k.mc.currentScreen instanceof GuiHopper || Auto32k.holding32k((EntityPlayer)Auto32k.mc.player) || !(Auto32k.mc.player.getDistanceSq(this.hopperPos) > (double)MathUtil.square(this.hopperDistance.getValue().floatValue())) && Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() == Blocks.HOPPER)) {
            this.resetFields();
            if (!this.autoSwitch.getValue().booleanValue() || !this.withBind.getValue().booleanValue() || this.mode.getValue() != Mode.NORMAL) {
                this.disable();
            }
        }
    }

    private void checkState() {
        if (!this.checkStatus.getValue().booleanValue() || this.checkedThisTick || this.currentStep != Step.HOPPER && this.currentStep != Step.SHULKER && this.currentStep != Step.CLICKHOPPER) {
            this.checkedThisTick = false;
            return;
        }
        if (this.hopperPos == null || !this.isGoodMaterial(Auto32k.mc.world.getBlockState(this.hopperPos).getBlock(), true) || !this.isGoodMaterial(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock(), false) && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) || this.badEntities(this.hopperPos) || this.badEntities(this.hopperPos.up())) {
            if (this.autoSwitch.getValue().booleanValue() && this.mode.getValue() == Mode.NORMAL) {
                if (this.switching) {
                    this.resetFields();
                    if (this.repeatSwitch.getValue().booleanValue()) {
                        this.switching = true;
                    }
                } else {
                    this.resetFields();
                }
                if (!this.withBind.getValue().booleanValue()) {
                    this.disable();
                }
            } else {
                this.disable();
            }
            this.checkedThisTick = true;
        }
    }

    private void processDispenser32k() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.placeTimer.passed(this.delay.getValue().intValue())) {
            this.check();
            switch (this.currentStep) {
                case PRE: {
                    this.runDispenserPreStep();
                    if (this.currentStep == Step.PRE) break;
                }
                case HOPPER: {
                    this.runHopperStep();
                    this.currentStep = Step.DISPENSER;
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue())) break;
                }
                case DISPENSER: {
                    boolean quickCheck;
                    this.runDispenserStep();
                    boolean bl = quickCheck = !Auto32k.mc.world.getBlockState(this.finalDispenserData.getHelpingPos()).getMaterial().isReplaceable();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue()) || this.currentStep != Step.DISPENSER_HELPING && this.currentStep != Step.CLICK_DISPENSER || this.rotate.getValue().booleanValue() && quickCheck) break;
                }
                case DISPENSER_HELPING: {
                    this.runDispenserStep();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue()) || this.currentStep != Step.CLICK_DISPENSER && this.currentStep != Step.DISPENSER_HELPING || this.rotate.getValue().booleanValue()) break;
                }
                case CLICK_DISPENSER: {
                    this.clickDispenser();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue())) break;
                }
                case DISPENSER_GUI: {
                    this.dispenserGui();
                    if (this.currentStep == Step.DISPENSER_GUI) break;
                }
                case REDSTONE: {
                    this.placeRedstone();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue())) break;
                }
                case CLICKHOPPER: {
                    this.runClickHopper();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passed(this.delay.getValue().intValue())) break;
                }
                case HOPPERGUI: {
                    this.runHopperGuiStep();
                    if (this.actionsThisTick < this.delayDispenser.getValue() || this.placeTimer.passed(this.delay.getValue().intValue())) break;
                    break;
                }
            }
        }
    }

    private void placeRedstone() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.badEntities(this.hopperPos.up()) && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox)) {
            return;
        }
        this.runPlaceStep(this.finalDispenserData.getRedStonePos(), this.redstoneSlot);
        this.currentStep = Step.CLICKHOPPER;
    }

    private void clickDispenser() {
        if (!this.isEnabled()) {
            return;
        }
        this.clickBlock(this.finalDispenserData.getDispenserPos());
        this.currentStep = Step.DISPENSER_GUI;
    }

    private void dispenserGui() {
        if (!this.isEnabled()) {
            return;
        }
        if (!(Auto32k.mc.currentScreen instanceof GuiDispenser)) {
            return;
        }
        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, this.shulkerSlot, 0, ClickType.QUICK_MOVE, (EntityPlayer)Auto32k.mc.player);
        Auto32k.mc.player.closeScreen();
        this.currentStep = Step.REDSTONE;
    }

    private void clickBlock(BlockPos pos) {
        if (!this.isEnabled() || pos == null) {
            return;
        }
        this.authSneakPacket = true;
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.authSneakPacket = false;
        Vec3d hitVec = new Vec3d((Vec3i)pos).add(0.5, -0.5, 0.5);
        if (this.rotate.getValue().booleanValue()) {
            this.rotateToPos(null, hitVec);
        }
        EnumFacing facing = EnumFacing.UP;
        if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getDispenserPos().equals((Object)pos) && pos.getY() > new BlockPos(Auto32k.mc.player.getPositionVector()).up().getY()) {
            facing = EnumFacing.DOWN;
        }
        Auto32k.rightClickBlock(pos, hitVec, this.shulkerSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, facing, this.packet.getValue(), this.swing.getValue());
        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
        ++this.actionsThisTick;
    }

    private void runDispenserStep() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.finalDispenserData == null || this.finalDispenserData.getDispenserPos() == null || this.finalDispenserData.getHelpingPos() == null) {
            this.resetFields();
            return;
        }
        if (this.currentStep != Step.DISPENSER && this.currentStep != Step.DISPENSER_HELPING) {
            return;
        }
        BlockPos dispenserPos = this.finalDispenserData.getDispenserPos();
        BlockPos helpingPos = this.finalDispenserData.getHelpingPos();
        if (Auto32k.mc.world.getBlockState(helpingPos).getMaterial().isReplaceable()) {
            this.currentStep = Step.DISPENSER_HELPING;
            EnumFacing facing = EnumFacing.DOWN;
            boolean foundHelpingPos = false;
            for (EnumFacing enumFacing : EnumFacing.values()) {
                BlockPos position = helpingPos.offset(enumFacing);
                if (position.equals((Object)this.hopperPos) || position.equals((Object)this.hopperPos.up()) || position.equals((Object)dispenserPos) || position.equals((Object)this.finalDispenserData.getRedStonePos()) || !(Auto32k.mc.player.getDistanceSq(position) <= (double)MathUtil.square(this.range.getValue().floatValue())) || this.raytrace.getValue().booleanValue() && !Auto32k.rayTracePlaceCheck(position, this.raytrace.getValue()) || Auto32k.mc.world.getBlockState(position).getMaterial().isReplaceable()) continue;
                foundHelpingPos = true;
                facing = enumFacing;
                break;
            }
            if (!foundHelpingPos) {
                this.disable();
                return;
            }
            BlockPos neighbour = helpingPos.offset(facing);
            EnumFacing opposite = facing.getOpposite();
            Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            Block neighbourBlock = Auto32k.mc.world.getBlockState(neighbour).getBlock();
            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.authSneakPacket = false;
            if (this.rotate.getValue().booleanValue()) {
                if (this.blocksPerPlace.getValue() > 1) {
                    float[] angle = RotationUtil.getRotations(hitVec);
                    if (this.extra.getValue().booleanValue()) {
                        Auto32k.faceYawAndPitch(angle[0], angle[1]);
                    }
                } else {
                    this.rotateToPos(null, hitVec);
                }
            }
            int slot = this.preferObby.getValue() != false && this.obbySlot != -1 ? this.obbySlot : this.dispenserSlot;
            InventoryUtil.switchTo(slot);
            Auto32k.rightClickBlock(neighbour, hitVec, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, this.packet.getValue(), this.swing.getValue());
            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.authSneakPacket = false;
            this.placeTimer.reset();
            ++this.actionsThisTick;
            return;
        }
        this.placeDispenserAgainstBlock(dispenserPos, helpingPos);
        ++this.actionsThisTick;
        this.currentStep = Step.CLICK_DISPENSER;
    }

    private void placeDispenserAgainstBlock(BlockPos dispenserPos, BlockPos helpingPos) {
        if (!this.isEnabled()) {
            return;
        }
        EnumFacing facing = EnumFacing.DOWN;
        for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos position = dispenserPos.offset(enumFacing);
            if (!position.equals((Object)helpingPos)) continue;
            facing = enumFacing;
            break;
        }
        EnumFacing opposite = facing.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i)helpingPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = Auto32k.mc.world.getBlockState(helpingPos).getBlock();
        this.authSneakPacket = true;
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.authSneakPacket = false;
        Vec3d rotationVec = null;
        EnumFacing facings = EnumFacing.UP;
        if (this.rotate.getValue().booleanValue()) {
            if (this.blocksPerPlace.getValue() > 1) {
                float[] arrf = RotationUtil.getRotations(hitVec);
                if (this.extra.getValue().booleanValue()) {
                    Auto32k.faceYawAndPitch(arrf[0], arrf[1]);
                }
            } else {
                this.rotateToPos(null, hitVec);
            }
            rotationVec = new Vec3d((Vec3i)helpingPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        } else if (dispenserPos.getY() <= new BlockPos(Auto32k.mc.player.getPositionVector()).up().getY()) {
            for (EnumFacing enumFacing : EnumFacing.values()) {
                BlockPos position = this.hopperPos.up().offset(enumFacing);
                if (!position.equals((Object)dispenserPos)) continue;
                facings = enumFacing;
                break;
            }
            float[] arrf = Auto32k.simpleFacing(facings);
            this.yaw = arrf[0];
            this.pitch = arrf[1];
            this.spoof = true;
        } else {
            float[] arrf = Auto32k.simpleFacing(facings);
            this.yaw = arrf[0];
            this.pitch = arrf[1];
            this.spoof = true;
        }
        rotationVec = new Vec3d((Vec3i)helpingPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        float[] arrf = Auto32k.simpleFacing(facings);
        float[] angle = RotationUtil.getRotations(hitVec);
        if (this.superPacket.getValue().booleanValue()) {
            Auto32k.faceYawAndPitch(this.rotate.getValue() == false ? arrf[0] : angle[0], this.rotate.getValue() == false ? arrf[1] : angle[1]);
        }
        InventoryUtil.switchTo(this.dispenserSlot);
        Auto32k.rightClickBlock(helpingPos, rotationVec, this.dispenserSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, this.packet.getValue(), this.swing.getValue());
        this.authSneakPacket = true;
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.authSneakPacket = false;
        this.placeTimer.reset();
        ++this.actionsThisTick;
        this.currentStep = Step.CLICK_DISPENSER;
    }

    private void runDispenserPreStep() {
        if (!this.isEnabled()) {
            return;
        }
        if (FREECAM.isEnabled() && !this.freecam.getValue().booleanValue()) {
            if (this.messages.getValue().booleanValue()) {
                Managers.CHAT.sendDeleteMessage("\u00a7c<Auto32k> Disable freecam.", this.getDisplayName(), 2000);
            }
            this.disable();
            return;
        }
        this.lastHotbarSlot = Auto32k.mc.player.inventory.currentItem;
        this.hopperSlot = InventoryUtil.findHotbarBlock((Block)Blocks.HOPPER, new Block[0]);
        this.shulkerSlot = InventoryUtil.findInInventory(item -> item.getItem() instanceof ItemShulkerBox, false);
        this.dispenserSlot = InventoryUtil.findHotbarBlock(Blocks.DISPENSER, new Block[0]);
        this.redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK, new Block[0]);
        this.obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
        if (Auto32k.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)Auto32k.mc.player.getHeldItemOffhand().getItem()).getBlock();
            if (block instanceof BlockHopper) {
                this.hopperSlot = -2;
            } else if (block instanceof BlockDispenser) {
                this.dispenserSlot = -2;
            } else if (block == Blocks.REDSTONE_BLOCK) {
                this.redstoneSlot = -2;
            } else if (block instanceof BlockObsidian) {
                this.obbySlot = -2;
            }
        }
        if (this.shulkerSlot == -1 || this.hopperSlot == -1 || this.dispenserSlot == -1 || this.redstoneSlot == -1) {
            if (this.messages.getValue().booleanValue()) {
                Managers.CHAT.sendDeleteMessage("\u00a7c<Auto32k> Materials not found.", this.getDisplayName(), 2000);
            }
            this.disable();
            return;
        }
        this.finalDispenserData = this.findBestPos();
        if (this.finalDispenserData.isPlaceable()) {
            this.hopperPos = this.finalDispenserData.getHopperPos();
            this.currentStep = Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper ? Step.DISPENSER : Step.HOPPER;
        } else {
            if (this.messages.getValue().booleanValue()) {
                Managers.CHAT.sendDeleteMessage("\u00a7c<Auto32k> Block not found.", this.getDisplayName(), 2000);
            }
            this.disable();
        }
    }

    private DispenserData findBestPos() {
        PlaceType type = this.placeType.getValue();
        this.target = EntityUtil.getClosestEnemy();
        if (this.target == null || Auto32k.mc.player.getDistanceSq((Entity)this.target) > MathUtil.square(this.targetRange.getValue())) {
            type = this.placeType.getValue() == PlaceType.MOUSE ? PlaceType.MOUSE : PlaceType.CLOSE;
        }
        NonNullList positions = NonNullList.create();
        BlockPos middle = PositionUtil.getPosition();
        int maxRadius = Sphere.getRadius(this.range.getValue().floatValue());
        for (int i = 1; i < maxRadius; ++i) {
            positions.add((Object)middle.add(Sphere.get(i)));
        }
        DispenserData data = new DispenserData();
        switch (type) {
            case MOUSE: {
                BlockPos mousePos;
                if (Auto32k.mc.objectMouseOver != null && Auto32k.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && (mousePos = Auto32k.mc.objectMouseOver.getBlockPos()) != null && !(data = this.analyzePos(mousePos)).isPlaceable()) {
                    data = this.analyzePos(mousePos.up());
                }
                if (data.isPlaceable()) {
                    return data;
                }
            }
            case CLOSE: {
                positions.sort(Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                break;
            }
            case ENEMY: {
                positions.sort(Comparator.comparingDouble(((EntityPlayer)this.target)::func_174818_b));
                break;
            }
            case MIDDLE: {
                ArrayList<BlockPos> toRemove = new ArrayList<BlockPos>();
                NonNullList copy = NonNullList.create();
                copy.addAll((Collection)positions);
                for (BlockPos position : copy) {
                    double difference = Auto32k.mc.player.getDistanceSq(position) - this.target.getDistanceSq(position);
                    if (!(difference > 1.0) && !(difference < -1.0)) continue;
                    toRemove.add(position);
                }
                copy.removeAll(toRemove);
                if (copy.isEmpty()) {
                    copy.addAll((Collection)positions);
                }
                copy.sort(Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                break;
            }
            case FAR: {
                positions.sort(Comparator.comparingDouble(pos2 -> -this.target.getDistanceSq(pos2)));
                break;
            }
            case SAFE: {
                positions.sort(Comparator.comparingInt(pos2 -> -this.safetyFactor((BlockPos)pos2)));
            }
        }
        data = this.findData((NonNullList<BlockPos>)positions);
        return data;
    }

    private DispenserData findData(NonNullList<BlockPos> positions) {
        for (BlockPos position : positions) {
            DispenserData data = this.analyzePos(position);
            if (!data.isPlaceable()) continue;
            return data;
        }
        return new DispenserData();
    }

    private DispenserData analyzePos(BlockPos pos) {
        DispenserData data = new DispenserData(pos);
        if (pos == null) {
            return data;
        }
        if (!this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos).getBlock(), this.onOtherHoppers.getValue()) || !this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos.up()).getBlock(), false)) {
            return data;
        }
        if (this.raytrace.getValue().booleanValue() && !Auto32k.rayTracePlaceCheck(pos, this.raytrace.getValue())) {
            return data;
        }
        if (this.badEntities(pos) || this.badEntities(pos.up())) {
            return data;
        }
        if (this.hasAdjancedRedstone(pos)) {
            return data;
        }
        if (!this.findFacing(pos)) {
            return data;
        }
        BlockPos[] otherPositions = this.checkForDispenserPos(pos);
        if (otherPositions[0] == null || otherPositions[1] == null || otherPositions[2] == null) {
            return data;
        }
        data.setDispenserPos(otherPositions[0]);
        data.setRedStonePos(otherPositions[1]);
        data.setHelpingPos(otherPositions[2]);
        data.setPlaceable(true);
        return data;
    }

    private boolean findFacing(BlockPos pos) {
        boolean foundFacing = false;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) continue;
            if (facing == EnumFacing.DOWN && this.antiHopper.getValue().booleanValue() && Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) {
                foundFacing = false;
                break;
            }
            if (Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable() || this.antiHopper.getValue().booleanValue() && Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) continue;
            foundFacing = true;
        }
        return foundFacing;
    }

    private BlockPos[] checkForDispenserPos(BlockPos posIn) {
        BlockPos[] pos;
        block12: {
            List<BlockPos> possiblePositions;
            block11: {
                pos = new BlockPos[3];
                BlockPos playerPos = new BlockPos(Auto32k.mc.player.getPositionVector());
                if (posIn.getY() < playerPos.down().getY()) {
                    return pos;
                }
                possiblePositions = this.getDispenserPositions(posIn);
                if (posIn.getY() < playerPos.getY()) {
                    possiblePositions.remove((Object)posIn.up().up());
                } else if (posIn.getY() > playerPos.getY()) {
                    possiblePositions.remove((Object)posIn.west().up());
                    possiblePositions.remove((Object)posIn.north().up());
                    possiblePositions.remove((Object)posIn.south().up());
                    possiblePositions.remove((Object)posIn.east().up());
                }
                if (!this.rotate.getValue().booleanValue() && !this.simulate.getValue().booleanValue()) break block11;
                possiblePositions.sort(Comparator.comparingDouble(pos2 -> -Auto32k.mc.player.getDistanceSq(pos2)));
                BlockPos posToCheck = possiblePositions.get(0);
                if (!this.isGoodMaterial(Auto32k.mc.world.getBlockState(posToCheck).getBlock(), false)) {
                    return pos;
                }
                if (Auto32k.mc.player.getDistanceSq(posToCheck) > (double)MathUtil.square(this.range.getValue().floatValue())) {
                    return pos;
                }
                if (this.raytrace.getValue().booleanValue() && !Auto32k.rayTracePlaceCheck(posToCheck, this.raytrace.getValue())) {
                    return pos;
                }
                if (this.badEntities(posToCheck)) {
                    return pos;
                }
                if (this.hasAdjancedRedstone(posToCheck)) {
                    return pos;
                }
                List<BlockPos> possibleRedStonePositions = this.checkRedStone(posToCheck, posIn);
                if (possiblePositions.isEmpty()) {
                    return pos;
                }
                BlockPos[] helpingStuff = this.getHelpingPos(posToCheck, posIn, possibleRedStonePositions);
                if (helpingStuff == null || helpingStuff[0] == null || helpingStuff[1] == null) break block12;
                pos[0] = posToCheck;
                pos[1] = helpingStuff[1];
                pos[2] = helpingStuff[0];
                break block12;
            }
            possiblePositions.removeIf(position -> Auto32k.mc.player.getDistanceSq(position) > (double)MathUtil.square(this.range.getValue().floatValue()));
            possiblePositions.removeIf(position -> !this.isGoodMaterial(Auto32k.mc.world.getBlockState(position).getBlock(), false));
            possiblePositions.removeIf(position -> this.raytrace.getValue() != false && !Auto32k.rayTracePlaceCheck(position, this.raytrace.getValue()));
            possiblePositions.removeIf(this::badEntities);
            possiblePositions.removeIf(this::hasAdjancedRedstone);
            for (BlockPos position2 : possiblePositions) {
                BlockPos[] helpingStuff;
                List<BlockPos> possibleRedStonePositions = this.checkRedStone(position2, posIn);
                if (possiblePositions.isEmpty() || (helpingStuff = this.getHelpingPos(position2, posIn, possibleRedStonePositions)) == null || helpingStuff[0] == null || helpingStuff[1] == null) continue;
                pos[0] = position2;
                pos[1] = helpingStuff[1];
                pos[2] = helpingStuff[0];
                break;
            }
        }
        return pos;
    }

    private List<BlockPos> checkRedStone(BlockPos pos, BlockPos hopperPos) {
        ArrayList<BlockPos> toCheck = new ArrayList<BlockPos>();
        for (EnumFacing facing : EnumFacing.values()) {
            toCheck.add(pos.offset(facing));
        }
        toCheck.removeIf(position -> position.equals((Object)hopperPos.up()));
        toCheck.removeIf(position -> Auto32k.mc.player.getDistanceSq(position) > (double)MathUtil.square(this.range.getValue().floatValue()));
        toCheck.removeIf(position -> !this.isGoodMaterial(Auto32k.mc.world.getBlockState(position).getBlock(), false));
        toCheck.removeIf(position -> this.raytrace.getValue() != false && !Auto32k.rayTracePlaceCheck(position, this.raytrace.getValue()));
        toCheck.removeIf(this::badEntities);
        toCheck.sort(Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
        return toCheck;
    }

    private boolean hasAdjancedRedstone(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos position = pos.offset(facing);
            if (Auto32k.mc.world.getBlockState(position).getBlock() != Blocks.REDSTONE_BLOCK && Auto32k.mc.world.getBlockState(position).getBlock() != Blocks.REDSTONE_TORCH) continue;
            return true;
        }
        return false;
    }

    private List<BlockPos> getDispenserPositions(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<BlockPos>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.DOWN) continue;
            list.add(pos.offset(facing).up());
        }
        return list;
    }

    private BlockPos[] getHelpingPos(BlockPos pos, BlockPos hopperPos, List<BlockPos> redStonePositions) {
        BlockPos[] result = new BlockPos[2];
        ArrayList<BlockPos> possiblePositions = new ArrayList<BlockPos>();
        if (redStonePositions.isEmpty()) {
            return null;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos facingPos = pos.offset(facing);
            if (facingPos.equals((Object)hopperPos) || facingPos.equals((Object)hopperPos.up())) continue;
            if (!Auto32k.mc.world.getBlockState(facingPos).getMaterial().isReplaceable()) {
                if (redStonePositions.contains((Object)facingPos)) {
                    redStonePositions.remove((Object)facingPos);
                    if (redStonePositions.isEmpty()) {
                        redStonePositions.add(facingPos);
                        continue;
                    }
                    result[0] = facingPos;
                    result[1] = redStonePositions.get(0);
                    return result;
                }
                result[0] = facingPos;
                result[1] = redStonePositions.get(0);
                return result;
            }
            for (EnumFacing facing1 : EnumFacing.values()) {
                BlockPos facingPos1 = facingPos.offset(facing1);
                if (facingPos1.equals((Object)hopperPos) || facingPos1.equals((Object)hopperPos.up()) || facingPos1.equals((Object)pos) || Auto32k.mc.world.getBlockState(facingPos1).getMaterial().isReplaceable()) continue;
                if (redStonePositions.contains((Object)facingPos)) {
                    redStonePositions.remove((Object)facingPos);
                    if (redStonePositions.isEmpty()) {
                        redStonePositions.add(facingPos);
                        continue;
                    }
                    possiblePositions.add(facingPos);
                    continue;
                }
                possiblePositions.add(facingPos);
            }
        }
        possiblePositions.removeIf(position -> Auto32k.mc.player.getDistanceSq(position) > (double)MathUtil.square(this.range.getValue().floatValue()));
        possiblePositions.sort(Comparator.comparingDouble(position -> Auto32k.mc.player.getDistanceSq(position)));
        if (!possiblePositions.isEmpty()) {
            redStonePositions.remove(possiblePositions.get(0));
            if (!redStonePositions.isEmpty()) {
                result[0] = (BlockPos)possiblePositions.get(0);
                result[1] = redStonePositions.get(0);
            }
            return result;
        }
        return null;
    }

    private void rotateToPos(BlockPos pos, Vec3d vec3d) {
        float[] angle = vec3d == null ? Auto32k.calcAngle(Auto32k.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() - 0.5f), (double)((float)pos.getZ() + 0.5f))) : RotationUtil.getRotations(vec3d);
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.spoof = true;
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((double)(difX * difX + difZ * difZ));
        return new float[]{(float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0)), (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist)))};
    }

    private boolean isGoodMaterial(Block block, boolean allowHopper) {
        return block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow || allowHopper && block instanceof BlockHopper;
    }

    private void resetFields() {
        this.shouldDisable = false;
        this.spoof = false;
        this.switching = false;
        this.lastHotbarSlot = -1;
        this.shulkerSlot = -1;
        this.hopperSlot = -1;
        this.hopperPos = null;
        this.target = null;
        this.currentStep = Step.PRE;
        this.obbySlot = -1;
        this.dispenserSlot = -1;
        this.redstoneSlot = -1;
        this.finalDispenserData = null;
        this.actionsThisTick = 0;
        this.rotationprepared = false;
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet, boolean swing) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            Auto32k.mc.playerController.processRightClickBlock(Auto32k.mc.player, Auto32k.mc.world, pos, direction, vec, hand);
        }
        if (swing) {
            Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || Auto32k.mc.world.rayTraceBlocks(new Vec3d(Auto32k.mc.player.posX, Auto32k.mc.player.posY + (double)Auto32k.mc.player.getEyeHeight(), Auto32k.mc.player.posZ), new Vec3d((double)pos.getX(), (double)((float)pos.getY() + height), (double)pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return Auto32k.rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return Auto32k.rayTracePlaceCheck(pos, true);
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(yaw, pitch, Auto32k.mc.player.onGround));
    }

    private boolean badEntities(BlockPos pos) {
        for (Entity entity : Auto32k.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityExpBottle || entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private int safetyFactor(BlockPos pos) {
        return this.safety(pos) + this.safety(pos.up());
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) continue;
            ++safety;
        }
        return safety;
    }

    public static float[] simpleFacing(EnumFacing facing) {
        switch (facing) {
            case DOWN: {
                return new float[]{Auto32k.mc.player.rotationYaw, 90.0f};
            }
            case UP: {
                return new float[]{Auto32k.mc.player.rotationYaw, -90.0f};
            }
            case NORTH: {
                return new float[]{180.0f, 0.0f};
            }
            case SOUTH: {
                return new float[]{0.0f, 0.0f};
            }
            case WEST: {
                return new float[]{90.0f, 0.0f};
            }
        }
        return new float[]{270.0f, 0.0f};
    }

    public static boolean holding32k(EntityPlayer player) {
        return Auto32k.is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getTagCompound() == null) {
            return false;
        }
        NBTTagList enchants = (NBTTagList)stack.getTagCompound().getTag("ench");
        if (enchants == null) {
            return false;
        }
        for (int i = 0; i < enchants.tagCount(); ++i) {
            NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") != 16) continue;
            int lvl = enchant.getInteger("lvl");
            if (lvl < 42) break;
            return true;
        }
        return false;
    }

    public static boolean simpleIs32k(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.SHARPNESS, (ItemStack)stack) >= 1000;
    }

    public static enum Step {
        PRE,
        HOPPER,
        SHULKER,
        CLICKHOPPER,
        HOPPERGUI,
        DISPENSER_HELPING,
        DISPENSER_GUI,
        DISPENSER,
        CLICK_DISPENSER,
        REDSTONE;

    }

    public static enum Mode {
        NORMAL,
        DISPENSER;

    }

    public static enum PlaceType {
        MOUSE,
        CLOSE,
        ENEMY,
        MIDDLE,
        FAR,
        SAFE;

    }

    public static class DispenserData {
        private BlockPos dispenserPos;
        private BlockPos redStonePos;
        private BlockPos hopperPos;
        private BlockPos helpingPos;
        private boolean isPlaceable = false;

        public DispenserData() {
        }

        public DispenserData(BlockPos pos) {
            this.hopperPos = pos;
        }

        public void setPlaceable(boolean placeable) {
            this.isPlaceable = placeable;
        }

        public boolean isPlaceable() {
            return this.dispenserPos != null && this.hopperPos != null && this.redStonePos != null && this.helpingPos != null;
        }

        public BlockPos getDispenserPos() {
            return this.dispenserPos;
        }

        public void setDispenserPos(BlockPos dispenserPos) {
            this.dispenserPos = dispenserPos;
        }

        public BlockPos getRedStonePos() {
            return this.redStonePos;
        }

        public void setRedStonePos(BlockPos redStonePos) {
            this.redStonePos = redStonePos;
        }

        public BlockPos getHopperPos() {
            return this.hopperPos;
        }

        public void setHopperPos(BlockPos hopperPos) {
            this.hopperPos = hopperPos;
        }

        public BlockPos getHelpingPos() {
            return this.helpingPos;
        }

        public void setHelpingPos(BlockPos helpingPos) {
            this.helpingPos = helpingPos;
        }
    }
}

