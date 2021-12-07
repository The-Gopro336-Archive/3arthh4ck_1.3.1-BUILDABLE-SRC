package me.earth.earthhack.impl.modules.movement.packetfly.util;

import java.util.Random;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import net.minecraft.util.math.Vec3d;

final class Type$3
extends Type {
    private final Random random = new Random();

    private int randomInt() {
        int result = this.random.nextInt(29000000);
        if (this.random.nextBoolean()) {
            return result;
        }
        return -result;
    }

    @Override
    public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
        return vec3d.addVector(this.randomInt(), 0.0, this.randomInt());
    }
}
