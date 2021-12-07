/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.movement.packetfly.util;

import java.util.Random;
import net.minecraft.util.math.Vec3d;

public enum Type {
    Down{

        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
            return vec3d.add(0.0, (double)(-invalid), 0.0);
        }
    }
    ,
    Up{

        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
            return vec3d.add(0.0, (double)invalid, 0.0);
        }
    }
    ,
    Preserve{
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
            return vec3d.add((double)this.randomInt(), 0.0, (double)this.randomInt());
        }
    }
    ,
    Switch{
        private final Random random = new Random();

        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
            boolean down = this.random.nextBoolean();
            return down ? vec3d.add(0.0, (double)(-invalid), 0.0) : vec3d.add(0.0, (double)invalid, 0.0);
        }
    }
    ,
    X{

        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
            return vec3d.add((double)invalid, 0.0, 0.0);
        }
    }
    ,
    Z{

        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
            return vec3d.add(0.0, 0.0, (double)invalid);
        }
    }
    ,
    XZ{

        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid) {
            return vec3d.add((double)invalid, 0.0, (double)invalid);
        }
    };


    public abstract Vec3d createOutOfBounds(Vec3d var1, int var2);
}

