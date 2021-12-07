package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import net.minecraft.entity.Entity;

public class MotionUpdateEvent$Riding
extends MotionUpdateEvent {
    private float moveStrafing;
    private float moveForward;
    private boolean jump;
    private boolean sneak;

    public MotionUpdateEvent$Riding(Stage stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround, float moveStrafing, float moveForward, boolean jump, boolean sneak) {
        super(stage, x, y, z, rotationYaw, rotationPitch, onGround);
        this.moveStrafing = moveStrafing;
        this.moveForward = moveForward;
        this.jump = jump;
        this.sneak = sneak;
    }

    public MotionUpdateEvent$Riding(Stage stage, MotionUpdateEvent$Riding event) {
        this(stage, event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround(), event.moveStrafing, event.moveForward, event.jump, event.sneak);
    }

    public Entity getEntity() {
        return MotionUpdateEvent$Riding.mc.player.getLowestRidingEntity();
    }

    public float getMoveStrafing() {
        return this.moveStrafing;
    }

    public void setMoveStrafing(float moveStrafing) {
        this.modified = true;
        this.moveStrafing = moveStrafing;
    }

    public float getMoveForward() {
        return this.moveForward;
    }

    public void setMoveForward(float moveForward) {
        this.modified = true;
        this.moveForward = moveForward;
    }

    public boolean getJump() {
        return this.jump;
    }

    public void setJump(boolean jump) {
        this.modified = true;
        this.jump = jump;
    }

    public boolean getSneak() {
        return this.sneak;
    }

    public void setSneak(boolean sneak) {
        this.modified = true;
        this.sneak = sneak;
    }
}
