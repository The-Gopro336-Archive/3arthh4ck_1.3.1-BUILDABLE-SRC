package me.earth.earthhack.impl.modules.render.waypoints.mode;

import net.minecraft.world.DimensionType;

class WayPointType$1 {
    static final int[] $SwitchMap$net$minecraft$world$DimensionType;

    static {
        $SwitchMap$net$minecraft$world$DimensionType = new int[DimensionType.values().length];
        try {
            WayPointType$1.$SwitchMap$net$minecraft$world$DimensionType[DimensionType.OVERWORLD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WayPointType$1.$SwitchMap$net$minecraft$world$DimensionType[DimensionType.NETHER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WayPointType$1.$SwitchMap$net$minecraft$world$DimensionType[DimensionType.THE_END.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
