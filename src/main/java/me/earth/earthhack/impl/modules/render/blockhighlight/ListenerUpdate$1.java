package me.earth.earthhack.impl.modules.render.blockhighlight;

import net.minecraft.util.math.RayTraceResult;

class ListenerUpdate$1 {
    static final int[] $SwitchMap$net$minecraft$util$math$RayTraceResult$Type;

    static {
        $SwitchMap$net$minecraft$util$math$RayTraceResult$Type = new int[RayTraceResult.Type.values().length];
        try {
            ListenerUpdate$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.BLOCK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerUpdate$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.ENTITY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
