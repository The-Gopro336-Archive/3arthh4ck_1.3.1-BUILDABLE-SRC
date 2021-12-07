/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MouseFilter
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.Queue;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.AbstractCalculation;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.CalculationMotion;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotateMode;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion
extends ModuleListener<AutoCrystal, MotionUpdateEvent> {
    private final MouseFilter pitchMouseFilter = new MouseFilter();
    private final MouseFilter yawMouseFilter = new MouseFilter();

    public ListenerMotion(AutoCrystal module) {
        super(module, MotionUpdateEvent.class, 1500);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(MotionUpdateEvent event) {
        if (AbstractCalculation.ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue() || AbstractCalculation.LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
            return;
        }
        if (event.getStage() == Stage.PRE) {
            RotationFunction rotation;
            if (!((AutoCrystal)this.module).multiThread.getValue().booleanValue() && ((AutoCrystal)this.module).motionCalc.getValue().booleanValue() && (Managers.POSITION.getX() != event.getX() || Managers.POSITION.getY() != event.getY() || Managers.POSITION.getZ() != event.getZ())) {
                CalculationMotion calc = new CalculationMotion((AutoCrystal)this.module, ListenerMotion.mc.world.loadedEntityList, ListenerMotion.mc.world.playerEntities);
                ((AutoCrystal)this.module).threadHelper.start(calc, false);
            } else if (((AutoCrystal)this.module).motionThread.getValue().booleanValue()) {
                ((AutoCrystal)this.module).threadHelper.startThread(new BlockPos[0]);
            }
            AbstractCalculation<?> current = ((AutoCrystal)this.module).threadHelper.getCurrentCalc();
            if (current != null && !current.isFinished() && ((AutoCrystal)this.module).rotate.getValue() != ACRotate.None && ((AutoCrystal)this.module).rotationThread.getValue() == RotationThread.Wait) {
                AutoCrystal autoCrystal = (AutoCrystal)this.module;
                synchronized (autoCrystal) {
                    try {
                        ((AutoCrystal)this.module).wait(((AutoCrystal)this.module).timeOut.getValue().intValue());
                    }
                    catch (InterruptedException e) {
                        Earthhack.getLogger().warn("Minecraft Main-Thread interrupted!");
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if ((rotation = ((AutoCrystal)this.module).rotation) != null) {
                ((AutoCrystal)this.module).isSpoofing = true;
                float[] rotations = rotation.apply(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch());
                if (((AutoCrystal)this.module).rotateMode.getValue() == RotateMode.Smooth) {
                    float yaw = this.yawMouseFilter.smooth(rotations[0] + MathUtil.getRandomInRange(-1.0f, 5.0f), ((AutoCrystal)this.module).smoothSpeed.getValue().floatValue());
                    float pitch = this.pitchMouseFilter.smooth(rotations[1] + MathUtil.getRandomInRange(-1.2f, 3.5f), ((AutoCrystal)this.module).smoothSpeed.getValue().floatValue());
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                } else {
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
            }
        } else {
            ((AutoCrystal)this.module).motionID.incrementAndGet();
            Queue<Runnable> queue = ((AutoCrystal)this.module).post;
            synchronized (queue) {
                ((AutoCrystal)this.module).runPost();
            }
            ((AutoCrystal)this.module).isSpoofing = false;
        }
    }

    @Override
    public int getPriority() {
        return ((AutoCrystal)this.module).priority.getValue();
    }
}

