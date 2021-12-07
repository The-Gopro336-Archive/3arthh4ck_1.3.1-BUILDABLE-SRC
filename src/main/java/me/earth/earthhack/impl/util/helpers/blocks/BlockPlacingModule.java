package me.earth.earthhack.impl.util.helpers.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public abstract class BlockPlacingModule
extends DisablingModule {
    private static final ModuleCache<NoGlitchBlocks> NO_GLITCH_BLOCKS = Caches.getModule(NoGlitchBlocks.class);
    public final Setting<Integer> blocks = this.register(new NumberSetting<Integer>("Blocks/Place", 4, 1, 10));
    public final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 25, 0, 1000));
    public final Setting<Rotate> rotate = this.register(new EnumSetting<Rotate>("Rotations", Rotate.None));
    public final Setting<Boolean> packet = this.register(new BooleanSetting("Packet", true));
    public final Setting<Boolean> swing = this.register(new BooleanSetting("Swing", false));
    public final Setting<CooldownBypass> cooldownBypass = this.register(new EnumSetting<CooldownBypass>("Cooldown-Bypass", CooldownBypass.None));
    public final Setting<Boolean> stackPacket = this.register(new BooleanSetting("StackPacket", false));
    public final Setting<Boolean> smartSneak = this.register(new BooleanSetting("Smart-Sneak", false));
    public final Setting<PlaceSwing> placeSwing = this.register(new EnumSetting<PlaceSwing>("PlaceSwing", PlaceSwing.Always));
    public final Setting<BlockingType> blockingType = this.register(new EnumSetting<BlockingType>("Blocking", BlockingType.Strict));
    public final Setting<RayTraceMode> smartRay = this.register(new EnumSetting<RayTraceMode>("Raytrace", RayTraceMode.Fast));
    public final DiscreteTimer timer = new GuardTimer(500L).reset(this.getDelay());
    public final List<Packet<?>> packets = new ArrayList();
    public final List<Runnable> post = new ArrayList<Runnable>();
    public int blocksPlaced = 0;
    public int slot = -1;
    public int lastSlot = -1;
    public float[] rotations;

    protected BlockPlacingModule(String name, Category category) {
        super(name, category);
        this.setData(new BlockPlacingData<BlockPlacingModule>(this));
    }

    @Override
    protected void onEnable() {
        this.checkNull();
    }

    public void placeBlock(BlockPos on, EnumFacing facing) {
        EntityPlayer from = this.getPlayerForRotations();
        float[] r = RotationUtil.getRotations(on, facing, from);
        RayTraceResult result = RayTraceUtil.getRayTraceResultWithEntity(r[0], r[1], from);
        this.placeBlock(on, facing, r, result.hitVec);
    }

    public void placeBlock(BlockPos on, EnumFacing facing, float[] helpingRotations, Vec3d hitVec) {
        if (this.rotations == null && (this.rotate.getValue() == Rotate.Normal || this.blocksPlaced == 0 && this.rotate.getValue() == Rotate.Packet)) {
            this.rotations = helpingRotations;
        } else if (this.rotate.getValue() == Rotate.Packet) {
            this.packets.add(new CPacketPlayer.Rotation(helpingRotations[0], helpingRotations[1], this.getPlayer().onGround));
        }
        float[] f = RayTraceUtil.hitVecToPlaceVec(on, hitVec);
        EnumHand hand = InventoryUtil.getHand(this.slot);
        this.packets.add(new CPacketPlayerTryUseItemOnBlock(on, facing, hand, f[0], f[1], f[2]));
        if (this.placeSwing.getValue() == PlaceSwing.Always) {
            this.packets.add(new CPacketAnimation(InventoryUtil.getHand(this.slot)));
        }
        if (!(this.packet.getValue().booleanValue() || NO_GLITCH_BLOCKS.isPresent() && ((NoGlitchBlocks)NO_GLITCH_BLOCKS.get()).noPlace())) {
            ItemStack stack = this.slot == -2 ? BlockPlacingModule.mc.player.getHeldItemOffhand() : BlockPlacingModule.mc.player.inventory.getStackInSlot(this.slot);
            mc.addScheduledTask(() -> this.placeClient(stack, on, hand, facing, f[0], f[1], f[2]));
        }
        ++this.blocksPlaced;
    }

    public void placeClient(ItemStack stack, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (stack.getItem() instanceof ItemBlock) {
            int i;
            IBlockState placeState;
            ItemBlock itemBlock = (ItemBlock)((Object)stack.getItem());
            Block block = itemBlock.getBlock();
            IBlockState iblockstate = BlockPlacingModule.mc.world.getBlockState(pos);
            Block iBlock = iblockstate.getBlock();
            if (!iBlock.isReplaceable(BlockPlacingModule.mc.world, pos)) {
                pos = pos.offset(facing);
            }
            if (!stack.isEmpty() && BlockPlacingModule.mc.player.canPlayerEdit(pos, facing, stack) && BlockPlacingModule.mc.world.mayPlace(block, pos, false, facing, null) && itemBlock.placeBlockAt(stack, BlockPlacingModule.mc.player, BlockPlacingModule.mc.world, pos, facing, hitX, hitY, hitZ, placeState = block.getStateForPlacement(BlockPlacingModule.mc.world, pos, facing, hitX, hitY, hitZ, i = itemBlock.func_77647_b(stack.getMetadata()), BlockPlacingModule.mc.player, hand))) {
                placeState = BlockPlacingModule.mc.world.getBlockState(pos);
                SoundType soundtype = placeState.getBlock().getSoundType(placeState, BlockPlacingModule.mc.world, pos, BlockPlacingModule.mc.player);
                BlockPlacingModule.mc.world.playSound(BlockPlacingModule.mc.player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
                if (!BlockPlacingModule.mc.player.isCreative() && this.stackPacket.getValue().booleanValue()) {
                    stack.shrink(1);
                }
            }
        }
    }

    public boolean execute() {
        if (!this.packets.isEmpty()) {
            boolean sneaking = this.sneak(this.packets);
            int lastSlot = this.lastSlot == -1 ? BlockPlacingModule.mc.player.inventory.currentItem : this.lastSlot;
            switch (this.cooldownBypass.getValue()) {
                case None: {
                    InventoryUtil.switchTo(this.slot);
                    break;
                }
                case Pick: {
                    InventoryUtil.bypassSwitch(this.slot);
                    break;
                }
                case Slot: {
                    InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(this.slot));
                }
            }
            if (!sneaking) {
                BlockPlacingModule.mc.player.connection.sendPacket(new CPacketEntityAction(BlockPlacingModule.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            this.packets.forEach(packet -> BlockPlacingModule.mc.player.connection.sendPacket((Packet)packet));
            this.timer.reset(this.delay.getValue().intValue());
            if (this.placeSwing.getValue() == PlaceSwing.Once) {
                Swing.Packet.swing(InventoryUtil.getHand(this.slot));
            }
            if (!sneaking) {
                BlockPlacingModule.mc.player.connection.sendPacket(new CPacketEntityAction(BlockPlacingModule.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.post.forEach(Runnable::run);
            this.packets.clear();
            this.post.clear();
            switch (this.cooldownBypass.getValue()) {
                case None: {
                    InventoryUtil.switchTo(lastSlot);
                    break;
                }
                case Slot: {
                    InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(this.slot));
                    break;
                }
                case Pick: {
                    InventoryUtil.bypassSwitch(this.slot);
                }
            }
            if (this.swing.getValue().booleanValue()) {
                Swing.Client.swing(InventoryUtil.getHand(this.slot));
            }
            return true;
        }
        return false;
    }

    protected boolean sneak(Collection<Packet<?>> packets) {
        return this.smartSneak.getValue() != false && !Managers.ACTION.isSneaking() && !packets.stream().anyMatch(SpecialBlocks.PACKETCHECK);
    }

    protected boolean checkNull() {
        this.packets.clear();
        this.blocksPlaced = 0;
        if (BlockPlacingModule.mc.player == null || BlockPlacingModule.mc.world == null) {
            this.disable();
            return false;
        }
        return true;
    }

    public boolean entityCheck(BlockPos pos) {
        return this.entityCheckSimple(pos);
    }

    protected boolean entityCheckSimple(BlockPos pos) {
        for (Entity entity : BlockPlacingModule.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || entity instanceof EntityPlayer && !BlockUtil.isBlocking(pos, (EntityPlayer)entity, this.blockingType.getValue()) || entity instanceof EntityEnderCrystal && this.blockingType.getValue() == BlockingType.Crystals) continue;
            return false;
        }
        return true;
    }

    public EntityPlayer getPlayerForRotations() {
        return BlockPlacingModule.mc.player;
    }

    public EntityPlayer getPlayer() {
        return BlockPlacingModule.mc.player;
    }

    public int getDelay() {
        return this.delay.getValue();
    }
}
