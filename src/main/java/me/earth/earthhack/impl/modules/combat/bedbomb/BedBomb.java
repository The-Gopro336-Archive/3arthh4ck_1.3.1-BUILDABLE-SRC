package me.earth.earthhack.impl.modules.combat.bedbomb;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class BedBomb
extends Module {
    private final Setting<Boolean> place = this.register(new BooleanSetting("Place", false));
    private final Setting<Integer> placeDelay = this.register(new NumberSetting<Integer>("Placedelay", 50, 0, 500));
    private final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f)));
    private final Setting<Boolean> extraPacket = this.register(new BooleanSetting("InsanePacket", false));
    private final Setting<Boolean> packet = this.register(new BooleanSetting("Packet", false));
    private final Setting<Boolean> explode = this.register(new BooleanSetting("Break", true));
    private final Setting<BreakLogic> breakMode = this.register(new EnumSetting<BreakLogic>("BreakMode", BreakLogic.ALL));
    private final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("Breakdelay", 50, 0, 500));
    private final Setting<Float> breakRange = this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f)));
    private final Setting<Float> minDamage = this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(36.0f)));
    private final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(12.0f)));
    private final Setting<Boolean> suicide = this.register(new BooleanSetting("Suicide", false));
    private final Setting<Boolean> removeTiles = this.register(new BooleanSetting("RemoveTiles", false));
    private final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    private final Setting<Boolean> oneDot15 = this.register(new BooleanSetting("1.15", false));
    private final Setting<Logic> logic = this.register(new EnumSetting<Logic>("Logic", Logic.BREAKPLACE));
    private final Setting<Boolean> craft = this.register(new BooleanSetting("Craft", false));
    private final Setting<Boolean> placeCraftingTable = this.register(new BooleanSetting("PlaceTable", false));
    private final Setting<Boolean> openCraftingTable = this.register(new BooleanSetting("OpenTable", false));
    private final Setting<Boolean> craftTable = this.register(new BooleanSetting("CraftTable", false));
    private final Setting<Float> tableRange = this.register(new NumberSetting<Float>("TableRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f)));
    private final Setting<Integer> craftDelay = this.register(new NumberSetting<Integer>("CraftDelay", 4, 1, 10));
    private final Setting<Integer> tableSlot = this.register(new NumberSetting<Integer>("TableSlot", 8, 0, 8));
    private final StopWatch breakTimer = new StopWatch();
    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch craftTimer = new StopWatch();
    private EntityPlayer target = null;
    private boolean sendRotationPacket = false;
    private final AtomicDouble yaw = new AtomicDouble(-1.0);
    private final AtomicDouble pitch = new AtomicDouble(-1.0);
    private final AtomicBoolean shouldRotate = new AtomicBoolean(false);
    private MotionUpdateEvent current;
    private boolean one;
    private boolean two;
    private boolean three;
    private boolean four;
    private boolean five;
    private boolean six;
    private BlockPos maxPos = null;
    private boolean shouldCraft;
    private int craftStage = 0;
    private int bedSlot = -1;
    private BlockPos finalPos;
    private EnumFacing finalFacing;

    public BedBomb() {
        super("BedBomb", Category.Combat);
        this.setData(new SimpleData(this, "Quick and dirty Port of the awful old Phobos BedBomb."));
        this.listeners.add(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class){

            @Override
            public void invoke(MotionUpdateEvent event) {
                BedBomb.this.onUpdateWalkingPlayer(event);
            }
        });
        this.listeners.addAll(new CPacketPlayerListener(){

            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
                BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
            }

            @Override
            protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
                BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
            }

            @Override
            protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
                BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
                BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
            }
        }.getListeners());
    }

    @Override
    protected void onEnable() {
        this.current = null;
        this.bedSlot = -1;
        this.sendRotationPacket = false;
        this.target = null;
        this.yaw.set(-1.0);
        this.pitch.set(-1.0);
        this.shouldRotate.set(false);
        this.shouldCraft = false;
    }

    public void onPacket(CPacketPlayer packet) {
        if (this.shouldRotate.get()) {
            ((ICPacketPlayer)((Object)packet)).setYaw((float)this.yaw.get());
            ((ICPacketPlayer)((Object)packet)).setPitch((float)this.pitch.get());
            this.shouldRotate.set(false);
        }
    }

    public static int findInventoryWool() {
        return InventoryUtil.findInInventory(s -> {
            if (s.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock)((Object)s.getItem())).getBlock();
                return block.getDefaultState().func_185904_a() == Material.CLOTH;
            }
            return false;
        }, true);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            BedBomb.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            BedBomb.mc.playerController.processRightClickBlock(BedBomb.mc.player, BedBomb.mc.world, pos, direction, vec, hand);
        }
        BedBomb.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public void onUpdateWalkingPlayer(MotionUpdateEvent event) {
        this.current = event;
        if (BedBomb.mc.player.dimension != -1 && BedBomb.mc.player.dimension != 1) {
            return;
        }
        if (event.getStage() == Stage.PRE) {
            this.doBedBomb();
            if (this.shouldCraft && BedBomb.mc.currentScreen instanceof GuiCrafting) {
                int woolSlot = BedBomb.findInventoryWool();
                int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock)((Object)s.getItem())).getBlock() instanceof BlockPlanks, true);
                if (woolSlot == -1 || woodSlot == -1 || woolSlot == -2 || woodSlot == -2) {
                    mc.displayGuiScreen(null);
                    BedBomb.mc.currentScreen = null;
                    this.shouldCraft = false;
                    return;
                }
                if (this.craftStage > 1 && !this.one) {
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 1, 1, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    this.one = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() && !this.two) {
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 2, 1, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    this.two = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 2 && !this.three) {
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 3, 1, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    this.three = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 3 && !this.four) {
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 4, 1, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    this.four = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 4 && !this.five) {
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 5, 1, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    this.five = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 5 && !this.six) {
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 6, 1, ClickType.PICKUP, BedBomb.mc.player);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
                    this.recheckBedSlots(woolSlot, woodSlot);
                    BedBomb.mc.playerController.windowClick(((GuiContainer)BedBomb.mc.currentScreen).inventorySlots.windowId, 0, 0, ClickType.QUICK_MOVE, BedBomb.mc.player);
                    this.six = true;
                    this.one = false;
                    this.two = false;
                    this.three = false;
                    this.four = false;
                    this.five = false;
                    this.six = false;
                    this.craftStage = -2;
                    this.shouldCraft = false;
                }
                ++this.craftStage;
            }
        } else if (event.getStage() == Stage.POST && this.finalPos != null) {
            Vec3d hitVec = new Vec3d(this.finalPos.down()).addVector(0.5, 0.5, 0.5).add(new Vec3d(this.finalFacing.getOpposite().getDirectionVec()).scale(0.5));
            BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            InventoryUtil.switchTo(this.bedSlot);
            BedBomb.rightClickBlock(this.finalPos.down(), hitVec, this.bedSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, this.packet.getValue());
            BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.placeTimer.reset();
            this.finalPos = null;
        }
    }

    public void recheckBedSlots(int woolSlot, int woodSlot) {
        int i;
        for (i = 1; i <= 3; ++i) {
            if (BedBomb.mc.player.openContainer.getInventory().get(i) != ItemStack.EMPTY) continue;
            BedBomb.mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(1, i, 1, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
        }
        for (i = 4; i <= 6; ++i) {
            if (BedBomb.mc.player.openContainer.getInventory().get(i) != ItemStack.EMPTY) continue;
            BedBomb.mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(1, i, 1, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
        }
    }

    public void incrementCraftStage() {
        if (this.craftTimer.passed(this.craftDelay.getValue().intValue())) {
            ++this.craftStage;
            if (this.craftStage > 9) {
                this.craftStage = 0;
            }
            this.craftTimer.reset();
        }
    }

    private void doBedBomb() {
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.mapBeds();
                this.breakBeds();
                this.placeBeds();
                break;
            }
            case PLACEBREAK: {
                this.mapBeds();
                this.placeBeds();
                this.breakBeds();
            }
        }
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = PositionUtil.getEyePos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{BedBomb.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - BedBomb.mc.player.rotationYaw)), BedBomb.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - BedBomb.mc.player.rotationPitch))};
    }

    private void breakBeds() {
        if (this.explode.getValue().booleanValue() && this.breakTimer.passed(this.breakDelay.getValue().intValue())) {
            if (this.breakMode.getValue() == BreakLogic.CALC) {
                if (this.maxPos != null) {
                    RayTraceResult result;
                    Vec3d hitVec = new Vec3d(this.maxPos).addVector(0.5, 0.5, 0.5);
                    float[] rotations = BedBomb.getLegitRotations(hitVec);
                    this.yaw.set(rotations[0]);
                    if (this.rotate.getValue().booleanValue()) {
                        this.shouldRotate.set(true);
                        this.pitch.set(rotations[1]);
                    }
                    EnumFacing facing = (result = BedBomb.mc.world.rayTraceBlocks(new Vec3d(BedBomb.mc.player.posX, BedBomb.mc.player.posY + (double)BedBomb.mc.player.getEyeHeight(), BedBomb.mc.player.posZ), new Vec3d((double)this.maxPos.getX() + 0.5, (double)this.maxPos.getY() - 0.5, (double)this.maxPos.getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                    BedBomb.rightClickBlock(this.maxPos, hitVec, EnumHand.MAIN_HAND, facing, true);
                    this.breakTimer.reset();
                }
            } else {
                for (TileEntity entityBed : BedBomb.mc.world.loadedTileEntityList) {
                    RayTraceResult result;
                    if (!(entityBed instanceof TileEntityBed) || BedBomb.mc.player.getDistanceSq(entityBed.getPos()) > (double)MathUtil.square(this.breakRange.getValue().floatValue())) continue;
                    Vec3d hitVec = new Vec3d(entityBed.getPos()).addVector(0.5, 0.5, 0.5);
                    float[] rotations = BedBomb.getLegitRotations(hitVec);
                    this.yaw.set(rotations[0]);
                    if (this.rotate.getValue().booleanValue()) {
                        this.shouldRotate.set(true);
                        this.pitch.set(rotations[1]);
                    }
                    EnumFacing facing = (result = BedBomb.mc.world.rayTraceBlocks(new Vec3d(BedBomb.mc.player.posX, BedBomb.mc.player.posY + (double)BedBomb.mc.player.getEyeHeight(), BedBomb.mc.player.posZ), new Vec3d((double)entityBed.getPos().getX() + 0.5, (double)entityBed.getPos().getY() - 0.5, (double)entityBed.getPos().getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                    BedBomb.rightClickBlock(entityBed.getPos(), hitVec, EnumHand.MAIN_HAND, facing, true);
                    this.breakTimer.reset();
                }
            }
        }
    }

    public static boolean canTakeDamage(boolean suicide) {
        return !BedBomb.mc.player.capabilities.isCreativeMode && !suicide;
    }

    private void mapBeds() {
        this.maxPos = null;
        float maxDamage = 0.5f;
        if (this.removeTiles.getValue().booleanValue()) {
            ArrayList<BedData> removedBlocks = new ArrayList<BedData>();
            for (TileEntity tile : BedBomb.mc.world.loadedTileEntityList) {
                if (!(tile instanceof TileEntityBed)) continue;
                TileEntityBed bed = (TileEntityBed)((Object)tile);
                BedData data = new BedData(tile.getPos(), BedBomb.mc.world.getBlockState(tile.getPos()), bed, bed.isHeadPiece());
                removedBlocks.add(data);
            }
            for (BedData data : removedBlocks) {
                BedBomb.mc.world.setBlockToAir(data.getPos());
            }
            for (BedData data : removedBlocks) {
                float selfDamage;
                BlockPos pos;
                if (!data.isHeadPiece() || !(BedBomb.mc.player.getDistanceSq(pos = data.getPos()) <= (double)MathUtil.square(this.breakRange.getValue().floatValue())) || !((double)(selfDamage = DamageUtil.calculate(pos, (EntityLivingBase)BedBomb.mc.player)) + 1.0 < (double)EntityUtil.getHealth(BedBomb.mc.player)) && BedBomb.canTakeDamage(this.suicide.getValue())) continue;
                for (EntityPlayer player : BedBomb.mc.world.playerEntities) {
                    float damage;
                    if (!(player.getDistanceSq(pos) < (double)MathUtil.square(this.range.getValue().floatValue())) || !EntityUtil.isValid(player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) || !((damage = DamageUtil.calculate(pos, (EntityLivingBase)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !BedBomb.canTakeDamage(this.suicide.getValue())) && !(damage > EntityUtil.getHealth(player)) || !(damage > maxDamage)) continue;
                    maxDamage = damage;
                    this.maxPos = pos;
                }
            }
            for (BedData data : removedBlocks) {
                BedBomb.mc.world.setBlockState(data.getPos(), data.getState());
            }
        } else {
            for (TileEntity tile : BedBomb.mc.world.loadedTileEntityList) {
                float selfDamage;
                BlockPos pos;
                TileEntityBed bed;
                if (!(tile instanceof TileEntityBed) || !(bed = (TileEntityBed)((Object)tile)).isHeadPiece() || !(BedBomb.mc.player.getDistanceSq(pos = bed.func_174877_v()) <= (double)MathUtil.square(this.breakRange.getValue().floatValue())) || !((double)(selfDamage = DamageUtil.calculate(pos, (EntityLivingBase)BedBomb.mc.player)) + 1.0 < (double)EntityUtil.getHealth(BedBomb.mc.player)) && BedBomb.canTakeDamage(this.suicide.getValue())) continue;
                for (EntityPlayer player : BedBomb.mc.world.playerEntities) {
                    float damage;
                    if (!(player.getDistanceSq(pos) < (double)MathUtil.square(this.range.getValue().floatValue())) || !EntityUtil.isValid(player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) || !((damage = DamageUtil.calculate(pos, (EntityLivingBase)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !BedBomb.canTakeDamage(this.suicide.getValue())) && !(damage > EntityUtil.getHealth(player)) || !(damage > maxDamage)) continue;
                    maxDamage = damage;
                    this.maxPos = pos;
                }
            }
        }
    }

    private void placeBeds() {
        if (this.place.getValue().booleanValue() && this.placeTimer.passed(this.placeDelay.getValue().intValue()) && this.maxPos == null) {
            this.bedSlot = this.findBedSlot();
            if (this.bedSlot == -1) {
                if (BedBomb.mc.player.getHeldItemOffhand().getItem() == Items.BED) {
                    this.bedSlot = -2;
                } else {
                    if (this.craft.getValue().booleanValue() && !this.shouldCraft && EntityUtil.getClosestEnemy(BedBomb.mc.world.playerEntities) != null) {
                        this.doBedCraft();
                    }
                    return;
                }
            }
            this.target = EntityUtil.getClosestEnemy(BedBomb.mc.world.playerEntities);
            if (this.target != null && this.target.getDistanceSq(BedBomb.mc.player) < 49.0) {
                BlockPos targetPos = new BlockPos(this.target.getPositionVector());
                this.placeBed(targetPos, true);
                if (this.craft.getValue().booleanValue()) {
                    this.doBedCraft();
                }
            }
        }
    }

    private void placeBed(BlockPos pos, boolean firstCheck) {
        if (BedBomb.mc.world.getBlockState(pos).getBlock() == Blocks.BED) {
            return;
        }
        float damage = DamageUtil.calculate(pos, (EntityLivingBase)BedBomb.mc.player);
        if ((double)damage > (double)EntityUtil.getHealth(BedBomb.mc.player) + 0.5) {
            if (firstCheck && this.oneDot15.getValue().booleanValue()) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        if (!BedBomb.mc.world.getBlockState(pos).func_185904_a().isReplaceable()) {
            if (firstCheck && this.oneDot15.getValue().booleanValue()) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        HashMap<BlockPos, EnumFacing> facings = new HashMap<BlockPos, EnumFacing>();
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos position;
            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP || !(BedBomb.mc.player.getDistanceSq(position = pos.offset(facing)) <= (double)MathUtil.square(this.placeRange.getValue().floatValue())) || !BedBomb.mc.world.getBlockState(position).func_185904_a().isReplaceable() || BedBomb.mc.world.getBlockState(position.down()).func_185904_a().isReplaceable()) continue;
            positions.add(position);
            facings.put(position, facing.getOpposite());
        }
        if (positions.isEmpty()) {
            if (firstCheck && this.oneDot15.getValue().booleanValue()) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        positions.sort(Comparator.comparingDouble(pos2 -> BedBomb.mc.player.getDistanceSq((BlockPos)pos2)));
        this.finalPos = (BlockPos)positions.get(0);
        this.finalFacing = (EnumFacing)facings.get(this.finalPos);
        float[] rotation = BedBomb.simpleFacing(this.finalFacing);
        if (!this.sendRotationPacket && this.extraPacket.getValue().booleanValue()) {
            BedBomb.faceYawAndPitch(rotation[0], rotation[1]);
            this.sendRotationPacket = true;
        }
        this.yaw.set(rotation[0]);
        this.pitch.set(rotation[1]);
        this.shouldRotate.set(true);
        if (this.current != null) {
            this.current.setYaw(rotation[0]);
            this.current.setPitch(rotation[1]);
        }
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        BedBomb.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, BedBomb.mc.player.onGround));
    }

    public static float[] simpleFacing(EnumFacing facing) {
        switch (facing) {
            case DOWN: {
                return new float[]{BedBomb.mc.player.rotationYaw, 90.0f};
            }
            case UP: {
                return new float[]{BedBomb.mc.player.rotationYaw, -90.0f};
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

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                int y = sphere ? cy - (int)r : cy;
                while (true) {
                    float f = y;
                    float f2 = sphere ? (float)cy + r : (float)(cy + h);
                    if (!(f < f2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double)(r * r)) || hollow && dist < (double)((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static List<BlockPos> getBlockSphere(float breakRange, Class clazz) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BedBomb.getSphere(BedBomb.mc.player.getPosition(), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(BedBomb.mc.world.getBlockState((BlockPos)pos).getBlock())).collect(Collectors.toList()));
        return positions;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return BedBomb.isPositionPlaceable(pos, rayTrace, true);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || BedBomb.mc.world.rayTraceBlocks(new Vec3d(BedBomb.mc.player.posX, BedBomb.mc.player.posY + (double)BedBomb.mc.player.getEyeHeight(), BedBomb.mc.player.posZ), new Vec3d(pos.getX(), (float)pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        if (BedBomb.mc.world == null || pos == null) {
            return facings;
        }
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = BedBomb.mc.world.getBlockState(neighbour);
            if (blockState == null || !blockState.getBlock().canCollideCheck(blockState, false) || blockState.func_185904_a().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BedBomb.getBlock(pos).canCollideCheck(BedBomb.getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return BedBomb.getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return BedBomb.mc.world.getBlockState(pos);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = BedBomb.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (!BedBomb.rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (Entity entity : BedBomb.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : BedBomb.getPossibleSides(pos)) {
            if (!BedBomb.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = BedBomb.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BedBomb.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BedBomb.mc.world.getBlockState(neighbour).getBlock();
        if (!BedBomb.mc.player.isSneaking() && (SpecialBlocks.BAD_BLOCKS.contains(neighbourBlock) || SpecialBlocks.SHULKERS.contains(neighbourBlock))) {
            BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BedBomb.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            BedBomb.faceVector(hitVec, true);
        }
        BedBomb.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BedBomb.mc.player.swingArm(EnumHand.MAIN_HAND);
        return sneaking || isSneaking;
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = BedBomb.getLegitRotations(vec);
        BedBomb.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? (float)MathHelper.normalizeAngle((int)((int)rotations[1]), (int)360) : rotations[1], BedBomb.mc.player.onGround));
    }

    public void doBedCraft() {
        BlockPos target;
        List targets;
        int woolSlot = BedBomb.findInventoryWool();
        int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock)((Object)s.getItem())).getBlock() instanceof BlockPlanks, true);
        if (woolSlot == -1 || woodSlot == -1) {
            if (BedBomb.mc.currentScreen instanceof GuiCrafting) {
                mc.displayGuiScreen(null);
                BedBomb.mc.currentScreen = null;
            }
            return;
        }
        if (this.placeCraftingTable.getValue().booleanValue() && BedBomb.getBlockSphere(this.tableRange.getValue().floatValue() - 1.0f, BlockWorkbench.class).size() == 0 && !(targets = BedBomb.getSphere(BedBomb.mc.player.getPosition(), this.tableRange.getValue().floatValue(), this.tableRange.getValue().intValue(), false, true, 0).stream().filter(pos -> BedBomb.isPositionPlaceable(pos, false) == 3).sorted(Comparator.comparingInt(pos -> -this.safety((BlockPos)pos))).collect(Collectors.toList())).isEmpty()) {
            target = (BlockPos)targets.get(0);
            int tableSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock)((Object)s.getItem())).getBlock() instanceof BlockPlanks, true);
            if (tableSlot != -1) {
                BedBomb.mc.player.inventory.currentItem = tableSlot;
                BedBomb.placeBlock(target, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
            } else {
                if (this.craftTable.getValue().booleanValue()) {
                    this.craftTable();
                }
                if ((tableSlot = InventoryUtil.findInHotbar(s -> s.getItem() instanceof ItemBlock && ((ItemBlock)((Object)s.getItem())).getBlock() instanceof BlockPlanks)) != -1 && tableSlot != -2) {
                    BedBomb.mc.player.inventory.currentItem = tableSlot;
                    BedBomb.placeBlock(target, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
                }
            }
        }
        if (this.openCraftingTable.getValue().booleanValue()) {
            List<BlockPos> tables = BedBomb.getBlockSphere(this.tableRange.getValue().floatValue(), BlockWorkbench.class);
            tables.sort(Comparator.comparingDouble(pos -> BedBomb.mc.player.getDistanceSq((BlockPos)pos)));
            if (!tables.isEmpty() && !(BedBomb.mc.currentScreen instanceof GuiCrafting)) {
                RayTraceResult result;
                target = tables.get(0);
                BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                if (BedBomb.mc.player.getDistanceSq(target) > (double)MathUtil.square(this.breakRange.getValue().floatValue())) {
                    return;
                }
                Vec3d hitVec = new Vec3d(target);
                float[] rotations = BedBomb.getLegitRotations(hitVec);
                this.yaw.set(rotations[0]);
                if (this.rotate.getValue().booleanValue()) {
                    this.shouldRotate.set(true);
                    this.pitch.set(rotations[1]);
                }
                EnumFacing facing = (result = BedBomb.mc.world.rayTraceBlocks(new Vec3d(BedBomb.mc.player.posX, BedBomb.mc.player.posY + (double)BedBomb.mc.player.getEyeHeight(), BedBomb.mc.player.posZ), new Vec3d((double)target.getX() + 0.5, (double)target.getY() - 0.5, (double)target.getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                BedBomb.rightClickBlock(target, hitVec, EnumHand.MAIN_HAND, facing, true);
                this.breakTimer.reset();
                if (BedBomb.mc.player.isSneaking()) {
                    BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
            }
            this.shouldCraft = BedBomb.mc.currentScreen instanceof GuiCrafting;
            this.craftStage = 0;
            this.craftTimer.reset();
        }
    }

    public void craftTable() {
        int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock)((Object)s.getItem())).getBlock() instanceof BlockPlanks, true);
        if (woodSlot != -1) {
            BedBomb.mc.playerController.windowClick(0, woodSlot, 0, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(0, 1, 1, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(0, 2, 1, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(0, 3, 1, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(0, 4, 1, ClickType.PICKUP, BedBomb.mc.player);
            BedBomb.mc.playerController.windowClick(0, 0, 0, ClickType.QUICK_MOVE, BedBomb.mc.player);
            int table = InventoryUtil.findInInventory(s -> s.getItem() instanceof ItemBlock && ((ItemBlock)((Object)s.getItem())).getBlock() instanceof BlockPlanks, true);
            if (table != -1) {
                BedBomb.mc.playerController.windowClick(0, table, 0, ClickType.PICKUP, BedBomb.mc.player);
                BedBomb.mc.playerController.windowClick(0, this.tableSlot.getValue(), 0, ClickType.PICKUP, BedBomb.mc.player);
                BedBomb.mc.playerController.windowClick(0, table, 0, ClickType.PICKUP, BedBomb.mc.player);
            }
        }
    }

    private int findBedSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = BedBomb.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || stack.getItem() != Items.BED) continue;
            return i;
        }
        return -1;
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (BedBomb.mc.world.getBlockState(pos.offset(facing)).func_185904_a().isReplaceable()) continue;
            ++safety;
        }
        return safety;
    }

    public static enum BreakLogic {
        ALL,
        CALC;

    }

    public static enum Logic {
        BREAKPLACE,
        PLACEBREAK;

    }

    public static class BedData {
        private final BlockPos pos;
        private final IBlockState state;
        private final boolean isHeadPiece;
        private final TileEntityBed entity;

        public BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
            this.pos = pos;
            this.state = state;
            this.entity = bed;
            this.isHeadPiece = isHeadPiece;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public IBlockState getState() {
            return this.state;
        }

        public boolean isHeadPiece() {
            return this.isHeadPiece;
        }

        public TileEntityBed getEntity() {
            return this.entity;
        }
    }
}
