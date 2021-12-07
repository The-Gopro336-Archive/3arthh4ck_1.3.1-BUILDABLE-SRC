package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.impl.gui.hud.Orientation;

class SnapPoint$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$gui$hud$Orientation;

    static {
        $SwitchMap$me$earth$earthhack$impl$gui$hud$Orientation = new int[Orientation.values().length];
        try {
            SnapPoint$1.$SwitchMap$me$earth$earthhack$impl$gui$hud$Orientation[Orientation.TOP.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SnapPoint$1.$SwitchMap$me$earth$earthhack$impl$gui$hud$Orientation[Orientation.BOTTOM.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SnapPoint$1.$SwitchMap$me$earth$earthhack$impl$gui$hud$Orientation[Orientation.RIGHT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SnapPoint$1.$SwitchMap$me$earth$earthhack$impl$gui$hud$Orientation[Orientation.LEFT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
