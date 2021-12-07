package me.earth.earthhack.impl.util.math.rotation;

import me.earth.earthhack.impl.managers.minecraft.movement.RotationManager;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;

public class RotationSmoother {
    private final RotationManager manager;
    private int rotationTicks;
    private boolean rotating;

    public RotationSmoother(RotationManager manager) {
        this.manager = manager;
    }

    public float[] getRotations(Entity from, Entity entity, double height, double maxAngle) {
        return this.getRotations(entity, from.posX, from.posY, from.posZ, from.getEyeHeight(), height, maxAngle);
    }

    public float[] getRotations(Entity entity, double fromX, double fromY, double fromZ, float eyeHeight, double height, double maxAngle) {
        float[] rotations = RotationUtil.getRotations(entity.posX, entity.posY + (double)entity.getEyeHeight() * height, entity.posZ, fromX, fromY, fromZ, eyeHeight);
        return this.smoothen(rotations, maxAngle);
    }

    public float[] smoothen(float[] rotations, double maxAngle) {
        float[] server = new float[]{this.manager.getServerYaw(), this.manager.getServerPitch()};
        return this.smoothen(server, rotations, maxAngle);
    }

    public float[] smoothen(float[] server, float[] rotations, double maxAngle) {
        if (maxAngle >= 180.0 || maxAngle <= 0.0 || RotationUtil.angle(server, rotations) <= maxAngle) {
            this.rotating = false;
            return rotations;
        }
        this.rotationTicks = 0;
        this.rotating = true;
        return RotationUtil.faceSmoothly(server[0], server[1], rotations[0], rotations[1], maxAngle, maxAngle);
    }

    public void incrementRotationTicks() {
        ++this.rotationTicks;
    }

    public int getRotationTicks() {
        return this.rotationTicks;
    }

    public boolean isRotating() {
        return this.rotating;
    }
}
