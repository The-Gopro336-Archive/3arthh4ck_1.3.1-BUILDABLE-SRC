package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autoregear.AutoRegear;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion
extends ModuleListener<AutoRegear, MotionUpdateEvent> {
    public ListenerMotion(AutoRegear module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            // empty if block
        }
        if (((AutoRegear)this.module).steal.getValue().booleanValue() && ListenerMotion.mc.currentScreen == null && !((AutoRegear)this.module).shouldRegear) {
            BlockPos craftingPos = ((AutoRegear)this.module).getShulkerBox();
            float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
            RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, ListenerMotion.mc.world);
            float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
            if (((AutoRegear)this.module).rotate.getValue() == Rotate.Normal) {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            } else if (((AutoRegear)this.module).rotate.getValue() != Rotate.None) {
                PacketUtil.doRotation(rotations[0], rotations[1], ListenerMotion.mc.player.onGround);
            }
            NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
            return;
        }
        if (((AutoRegear)this.module).shouldRegear && ListenerMotion.mc.currentScreen == null) {
            BlockPos optimal = ((AutoRegear)this.module).getOptimalPlacePos(false);
            boolean swapped = false;
            if (((AutoRegear)this.module).placeEchest.getValue().booleanValue() && ((AutoRegear)this.module).getBlock(Blocks.ENDER_CHEST) == null && InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST, new Block[0]) != -1 && !((AutoRegear)this.module).hasKit() && optimal != null) {
                int slot = InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST, new Block[0]);
                if (slot == -1) {
                    return;
                }
                ((AutoRegear)this.module).slot = slot;
                EnumFacing facing = BlockUtil.getFacing(optimal);
                ((AutoRegear)this.module).placeBlock(optimal.offset(facing), facing.getOpposite());
                if (((AutoRegear)this.module).rotate.getValue() == Rotate.Normal && ((AutoRegear)this.module).rotations != null) {
                    event.setYaw(((AutoRegear)this.module).rotations[0]);
                    event.setPitch(((AutoRegear)this.module).rotations[1]);
                }
                ((AutoRegear)this.module).execute();
                return;
            }
            if (((AutoRegear)this.module).grabShulker.getValue().booleanValue() && ((AutoRegear)this.module).getBlock(Blocks.ENDER_CHEST) != null && ((AutoRegear)this.module).getShulkerBox() == null && ListenerMotion.mc.currentScreen == null && !((AutoRegear)this.module).hasKit()) {
                BlockPos craftingPos = ((AutoRegear)this.module).getBlock(Blocks.ENDER_CHEST);
                float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
                RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, ListenerMotion.mc.world);
                float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
                if (((AutoRegear)this.module).rotate.getValue() == Rotate.Normal) {
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                } else if (((AutoRegear)this.module).rotate.getValue() != Rotate.None) {
                    PacketUtil.doRotation(rotations[0], rotations[1], ListenerMotion.mc.player.onGround);
                }
                NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
                return;
            }
            if (((AutoRegear)this.module).placeShulker.getValue().booleanValue() && ((AutoRegear)this.module).getShulkerBox() == null && ((AutoRegear)this.module).hasKit() && optimal != null) {
                optimal = ((AutoRegear)this.module).getOptimalPlacePos(true);
                if (optimal == null) {
                    return;
                }
                int slot = InventoryUtil.findInHotbar(stack -> stack.getItem() instanceof ItemBlock && ((ItemBlock)((Object)stack.getItem())).getBlock() instanceof BlockShulkerBox);
                int inventorySlot = InventoryUtil.findInInventory(stack -> stack.getItem() instanceof ItemBlock && ((ItemBlock)((Object)stack.getItem())).getBlock() instanceof BlockShulkerBox, true);
                if (slot == -1) {
                    if (inventorySlot == -1) {
                        return;
                    }
                    slot = InventoryUtil.hotbarToInventory(8);
                    ListenerMotion.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, ListenerMotion.mc.player);
                    ListenerMotion.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ListenerMotion.mc.player);
                    ListenerMotion.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, ListenerMotion.mc.player);
                    swapped = true;
                }
                ((AutoRegear)this.module).slot = slot;
                EnumFacing facing = EnumFacing.DOWN;
                ((AutoRegear)this.module).placeBlock(optimal.offset(facing), facing.getOpposite());
                if (((AutoRegear)this.module).rotate.getValue() == Rotate.Normal && ((AutoRegear)this.module).rotations != null) {
                    event.setYaw(((AutoRegear)this.module).rotations[0]);
                    event.setPitch(((AutoRegear)this.module).rotations[1]);
                }
                ((AutoRegear)this.module).execute();
                if (swapped) {
                    ListenerMotion.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, ListenerMotion.mc.player);
                    ListenerMotion.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ListenerMotion.mc.player);
                    ListenerMotion.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, ListenerMotion.mc.player);
                }
                return;
            }
            BlockPos craftingPos = ((AutoRegear)this.module).getShulkerBox();
            if (craftingPos == null) {
                return;
            }
            float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
            RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, ListenerMotion.mc.world);
            float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
            if (((AutoRegear)this.module).rotate.getValue() == Rotate.Normal) {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            } else if (((AutoRegear)this.module).rotate.getValue() != Rotate.None) {
                PacketUtil.doRotation(rotations[0], rotations[1], ListenerMotion.mc.player.onGround);
            }
            NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
            ((AutoRegear)this.module).shouldRegear = false;
        }
    }
}
