package me.earth.earthhack.impl.core.mixins;

import me.earth.earthhack.impl.core.ducks.IMinecraft;

class MixinMinecraft$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$core$ducks$IMinecraft$Click;

    static {
        $SwitchMap$me$earth$earthhack$impl$core$ducks$IMinecraft$Click = new int[IMinecraft.Click.values().length];
        try {
            MixinMinecraft$1.$SwitchMap$me$earth$earthhack$impl$core$ducks$IMinecraft$Click[IMinecraft.Click.RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinMinecraft$1.$SwitchMap$me$earth$earthhack$impl$core$ducks$IMinecraft$Click[IMinecraft.Click.LEFT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MixinMinecraft$1.$SwitchMap$me$earth$earthhack$impl$core$ducks$IMinecraft$Click[IMinecraft.Click.MIDDLE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
