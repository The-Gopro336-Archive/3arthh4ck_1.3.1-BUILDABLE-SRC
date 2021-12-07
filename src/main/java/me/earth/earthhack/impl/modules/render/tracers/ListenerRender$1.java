package me.earth.earthhack.impl.modules.render.tracers;

import me.earth.earthhack.impl.modules.render.tracers.mode.BodyPart;
import me.earth.earthhack.impl.modules.render.tracers.mode.TracerMode;

class ListenerRender$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$BodyPart;
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$TracerMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$TracerMode = new int[TracerMode.values().length];
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$TracerMode[TracerMode.Outline.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$TracerMode[TracerMode.Fill.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$BodyPart = new int[BodyPart.values().length];
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$BodyPart[BodyPart.Head.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$BodyPart[BodyPart.Body.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$tracers$mode$BodyPart[BodyPart.Feet.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
