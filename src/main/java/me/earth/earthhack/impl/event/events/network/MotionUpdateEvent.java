package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.entity.Entity;

public class MotionUpdateEvent
extends StageEvent
implements Globals {
    private double x;
    private double y;
    private double z;
    private float rotationYaw;
    private float rotationPitch;
    private boolean onGround;
    protected boolean modified;

    public MotionUpdateEvent(Stage stage, MotionUpdateEvent event) {
        this(stage, event.x, event.y, event.z, event.rotationYaw, event.rotationPitch, event.onGround);
    }

    public MotionUpdateEvent(Stage stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround) {
        super(stage);
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
    }

    public boolean isModified() {
        return this.modified;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.modified = true;
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.modified = true;
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.modified = true;
        this.z = z;
    }

    public float getYaw() {
        return this.rotationYaw;
    }

    public void setYaw(float rotationYaw) {
        this.modified = true;
        this.rotationYaw = rotationYaw;
    }

    public float getPitch() {
        return this.rotationPitch;
    }

    public void setPitch(float rotationPitch) {
        this.modified = true;
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.modified = true;
        this.onGround = onGround;
    }

    public static class Riding
    extends MotionUpdateEvent {
        private float moveStrafing;
        private float moveForward;
        private boolean jump;
        private boolean sneak;

        public Riding(Stage stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround, float moveStrafing, float moveForward, boolean jump, boolean sneak) {
            super(stage, x, y, z, rotationYaw, rotationPitch, onGround);
            this.moveStrafing = moveStrafing;
            this.moveForward = moveForward;
            this.jump = jump;
            this.sneak = sneak;
        }

        public Riding(Stage stage, Riding event) {
            this(stage, event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround(), event.moveStrafing, event.moveForward, event.jump, event.sneak);
        }

        public Entity getEntity() {
            return Riding.mc.player.getLowestRidingEntity();
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
}
