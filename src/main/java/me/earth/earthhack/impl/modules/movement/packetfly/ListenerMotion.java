package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Phase;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMotion
extends ModuleListener<PacketFly, MotionUpdateEvent> {
    public ListenerMotion(PacketFly module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            double ySpeed;
            ListenerMotion.mc.player.motionX = 0.0;
            ListenerMotion.mc.player.motionY = 0.0;
            ListenerMotion.mc.player.motionZ = 0.0;
            if (((PacketFly)this.module).mode.getValue() != Mode.Setback && ((PacketFly)this.module).teleportID.get() == 0) {
                if (((PacketFly)this.module).checkPackets(6)) {
                    ((PacketFly)this.module).sendPackets(0.0, 0.0, 0.0, true);
                }
                return;
            }
            boolean isPhasing = ((PacketFly)this.module).isPlayerCollisionBoundingBoxEmpty();
            if (ListenerMotion.mc.player.movementInput.jump && (isPhasing || !MovementUtil.isMoving())) {
                ySpeed = ((PacketFly)this.module).antiKick.getValue().booleanValue() && !isPhasing ? (((PacketFly)this.module).checkPackets(((PacketFly)this.module).mode.getValue() == Mode.Setback ? 10 : 20) ? -0.032 : 0.062) : (((PacketFly)this.module).yJitter.getValue() != false && ((PacketFly)this.module).zoomies ? 0.061 : 0.062);
            } else if (ListenerMotion.mc.player.movementInput.sneak) {
                ySpeed = ((PacketFly)this.module).yJitter.getValue() != false && ((PacketFly)this.module).zoomies ? -0.061 : -0.062;
            } else {
                double d = !isPhasing ? (((PacketFly)this.module).checkPackets(4) ? (((PacketFly)this.module).antiKick.getValue().booleanValue() ? -0.04 : 0.0) : 0.0) : (ySpeed = 0.0);
            }
            if (((PacketFly)this.module).phase.getValue() == Phase.Full && isPhasing && MovementUtil.isMoving() && ySpeed != 0.0) {
                ySpeed /= 2.5;
            }
            double high = ((PacketFly)this.module).xzJitter.getValue() != false && ((PacketFly)this.module).zoomies ? 0.25 : 0.26;
            double low = ((PacketFly)this.module).xzJitter.getValue() != false && ((PacketFly)this.module).zoomies ? 0.03 : 0.031;
            double[] dirSpeed = MovementUtil.strafe(((PacketFly)this.module).phase.getValue() == Phase.Full && isPhasing ? low : high);
            if (((PacketFly)this.module).mode.getValue() == Mode.Increment) {
                if (((PacketFly)this.module).lastFactor >= ((PacketFly)this.module).factor.getValue().floatValue()) {
                    ((PacketFly)this.module).lastFactor = 1.0f;
                } else {
                    float f;
                    ((PacketFly)this.module).lastFactor += 1.0f;
                    if (f > ((PacketFly)this.module).factor.getValue().floatValue()) {
                        ((PacketFly)this.module).lastFactor = ((PacketFly)this.module).factor.getValue().floatValue();
                    }
                }
            } else {
                ((PacketFly)this.module).lastFactor = ((PacketFly)this.module).factor.getValue().floatValue();
            }
            int i = 1;
            while (true) {
                float f = i;
                float f2 = ((PacketFly)this.module).mode.getValue() == Mode.Factor || ((PacketFly)this.module).mode.getValue() == Mode.Slow || ((PacketFly)this.module).mode.getValue() == Mode.Increment ? ((PacketFly)this.module).lastFactor : 1.0f;
                if (!(f <= f2)) break;
                double conceal = ListenerMotion.mc.player.posY < ((PacketFly)this.module).concealY.getValue() && !MovementUtil.noMovementKeys() ? ((PacketFly)this.module).conceal.getValue() : 1.0;
                ListenerMotion.mc.player.motionX = dirSpeed[0] * (double)i * conceal * ((PacketFly)this.module).xzSpeed.getValue();
                ListenerMotion.mc.player.motionY = ySpeed * (double)i * ((PacketFly)this.module).ySpeed.getValue();
                ListenerMotion.mc.player.motionZ = dirSpeed[1] * (double)i * conceal * ((PacketFly)this.module).xzSpeed.getValue();
                ((PacketFly)this.module).sendPackets(ListenerMotion.mc.player.motionX, ListenerMotion.mc.player.motionY, ListenerMotion.mc.player.motionZ, ((PacketFly)this.module).mode.getValue() != Mode.Setback);
                ++i;
            }
            ++((PacketFly)this.module).zoomTimer;
            if (((PacketFly)this.module).zoomTimer > ((PacketFly)this.module).zoomer.getValue()) {
                ((PacketFly)this.module).zoomies = !((PacketFly)this.module).zoomies;
                ((PacketFly)this.module).zoomTimer = 0;
            }
        }
    }
}
