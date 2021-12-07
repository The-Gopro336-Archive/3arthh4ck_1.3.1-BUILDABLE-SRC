package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.util.math.BlockPos;

final class ListenerGameLoop
extends ModuleListener<AutoCrystal, GameLoopEvent> {
    public ListenerGameLoop(AutoCrystal module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        ((AutoCrystal)this.module).rotationCanceller.onGameLoop();
        if (((AutoCrystal)this.module).multiThread.getValue().booleanValue() && ((AutoCrystal)this.module).rotate.getValue() != ACRotate.None && ((AutoCrystal)this.module).rotationThread.getValue() == RotationThread.Predict && mc.getRenderPartialTicks() >= ((AutoCrystal)this.module).partial.getValue().floatValue()) {
            ((AutoCrystal)this.module).threadHelper.startThread(new BlockPos[0]);
        }
        if (((AutoCrystal)this.module).multiThread.getValue().booleanValue() && ((AutoCrystal)this.module).rotate.getValue() == ACRotate.None && ((AutoCrystal)this.module).serverThread.getValue().booleanValue() && ListenerGameLoop.mc.world != null && ListenerGameLoop.mc.player != null) {
            if (Managers.TICK.valid(Managers.TICK.getTickTimeAdjusted(), Managers.TICK.normalize(Managers.TICK.getSpawnTime() - ((AutoCrystal)this.module).tickThreshold.getValue()), Managers.TICK.normalize(Managers.TICK.getSpawnTime() - ((AutoCrystal)this.module).preSpawn.getValue()))) {
                if (!((AutoCrystal)this.module).earlyFeetThread.getValue().booleanValue()) {
                    ((AutoCrystal)this.module).threadHelper.startThread(new BlockPos[0]);
                } else if (((AutoCrystal)this.module).lateBreakThread.getValue().booleanValue()) {
                    ((AutoCrystal)this.module).threadHelper.startThread(true, false, new BlockPos[0]);
                }
            } else if (EntityUtil.getClosestEnemy() != null && BlockUtil.isSemiSafe(EntityUtil.getClosestEnemy(), true, ((AutoCrystal)this.module).newVer.getValue()) && BlockUtil.canBeFeetPlaced(EntityUtil.getClosestEnemy(), true, ((AutoCrystal)this.module).newVer.getValue()) && ((AutoCrystal)this.module).earlyFeetThread.getValue().booleanValue() && Managers.TICK.valid(Managers.TICK.getTickTimeAdjusted(), 0, ((AutoCrystal)this.module).maxEarlyThread.getValue())) {
                ((AutoCrystal)this.module).threadHelper.startThread(false, true, new BlockPos[0]);
            }
        }
    }
}
