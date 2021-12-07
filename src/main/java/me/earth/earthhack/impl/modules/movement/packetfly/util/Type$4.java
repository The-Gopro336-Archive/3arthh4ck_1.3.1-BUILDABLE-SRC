package me.earth.earthhack.impl.modules.movement.packetfly.util;

import java.util.Random;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import net.minecraft.util.math.Vec3d;

final class Type$4
extends Type {
    private final Random random = new Random();

    @Override
    public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
        boolean down = this.random.nextBoolean();
        return down ? vec3d.addVector(0.0, -invalid, 0.0) : vec3d.addVector(0.0, invalid, 0.0);
    }
}
