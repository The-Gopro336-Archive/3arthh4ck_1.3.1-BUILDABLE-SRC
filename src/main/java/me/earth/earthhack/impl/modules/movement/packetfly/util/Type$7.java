package me.earth.earthhack.impl.modules.movement.packetfly.util;

import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import net.minecraft.util.math.Vec3d;

final class Type$7
extends Type {
    @Override
    public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
        return vec3d.addVector(invalid, 0.0, invalid);
    }
}
