/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.nofall.NoFall;
import me.earth.earthhack.impl.modules.movement.nofall.mode.FallMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

final class ListenerMotion
extends ModuleListener<NoFall, MotionUpdateEvent> {
    public ListenerMotion(NoFall module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        int slot;
        if (((NoFall)this.module).mode.getValue() == FallMode.Bucket && (slot = InventoryUtil.findHotbarItem(Items.WATER_BUCKET, new Item[0])) != -1) {
            Vec3d positionVector = ListenerMotion.mc.player.getPositionVector();
            RayTraceResult rayTraceBlocks = ListenerMotion.mc.world.rayTraceBlocks(positionVector, new Vec3d(positionVector.x, positionVector.y - 3.0, positionVector.z), true);
            if (ListenerMotion.mc.player.fallDistance < 5.0f || rayTraceBlocks == null || rayTraceBlocks.typeOfHit != RayTraceResult.Type.BLOCK || ListenerMotion.mc.world.getBlockState(rayTraceBlocks.getBlockPos()).getBlock() instanceof BlockLiquid || PositionUtil.inLiquid() || PositionUtil.inLiquid(false)) {
                return;
            }
            if (event.getStage() == Stage.PRE) {
                event.setPitch(90.0f);
            } else {
                RayTraceResult rayTraceBlocks2 = ListenerMotion.mc.world.rayTraceBlocks(positionVector, new Vec3d(positionVector.x, positionVector.y - 5.0, positionVector.z), true);
                if (rayTraceBlocks2 != null && rayTraceBlocks2.typeOfHit == RayTraceResult.Type.BLOCK && !(ListenerMotion.mc.world.getBlockState(rayTraceBlocks2.getBlockPos()).getBlock() instanceof BlockLiquid) && ((NoFall)this.module).timer.passed(1000L)) {
                    Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                        InventoryUtil.switchTo(slot);
                        ListenerMotion.mc.playerController.processRightClick((EntityPlayer)ListenerMotion.mc.player, (World)ListenerMotion.mc.world, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    });
                    ((NoFall)this.module).timer.reset();
                }
            }
        }
    }
}

