/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.surround;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion
extends ModuleListener<Surround, MotionUpdateEvent> {
    public ListenerMotion(Surround module) {
        super(module, MotionUpdateEvent.class, -999999999);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            ListenerMotion.start((Surround)this.module, event);
        } else {
            ((Surround)this.module).setPosition = true;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, ((Surround)this.module)::execute);
            Managers.ROTATION.setBlocking(false);
        }
    }

    public static void start(Surround module) {
        ListenerMotion.start(module, new MotionUpdateEvent(Stage.PRE, 0.0, 0.0, 0.0, 0.0f, 0.0f, false));
    }

    public static void start(Surround module, MotionUpdateEvent event) {
        module.rotations = null;
        module.attacking = null;
        module.blocksPlaced = 0;
        module.center();
        if (module.updatePosAndBlocks()) {
            module.placed.removeAll(module.confirmed);
            boolean hasPlaced = false;
            Optional<BlockPos> crystalPos = module.targets.stream().filter(pos -> !ListenerMotion.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos)).isEmpty() && ListenerMotion.mc.world.getBlockState(pos).getMaterial().isReplaceable()).findFirst();
            if (crystalPos.isPresent()) {
                BlockPos pos2 = crystalPos.get();
                module.confirmed.remove((Object)pos2);
                hasPlaced = module.placeBlock(pos2);
            }
            if (!hasPlaced || !module.crystalCheck.getValue().booleanValue()) {
                ArrayList<BlockPos> surrounding = new ArrayList<BlockPos>(module.targets);
                if (module.getPlayer().motionX != 0.0 || module.getPlayer().motionZ != 0.0) {
                    BlockPos pos3 = new BlockPos((Entity)module.getPlayer()).add(module.getPlayer().motionX * 10000.0, 0.0, module.getPlayer().motionZ * 10000.0);
                    surrounding.sort(Comparator.comparingDouble(p -> p.distanceSq((double)pos3.getX() + 0.5, (double)pos3.getY(), (double)pos3.getZ() + 0.5)));
                }
                for (BlockPos pos4 : surrounding) {
                    if (module.placed.contains((Object)pos4) || !ListenerMotion.mc.world.getBlockState(pos4).getMaterial().isReplaceable()) continue;
                    module.confirmed.remove((Object)pos4);
                    if (!module.placeBlock(pos4)) continue;
                    break;
                }
            }
        }
        if (module.blocksPlaced == 0) {
            module.placed.clear();
        }
        if (module.rotate.getValue() != Rotate.None) {
            if (module.rotations != null) {
                Managers.ROTATION.setBlocking(true);
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
                if (Surround.FREECAM.isEnabled()) {
                    ((Freecam)Surround.FREECAM.get()).rotate(module.rotations[0], module.rotations[1]);
                }
            }
        } else if (module.setPosition) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, module::execute);
        }
    }
}

