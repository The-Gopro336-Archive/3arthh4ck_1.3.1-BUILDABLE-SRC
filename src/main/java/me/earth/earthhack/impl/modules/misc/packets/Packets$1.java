package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;

class Packets$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$misc$packets$util$BookCrashMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$misc$packets$util$BookCrashMode = new int[BookCrashMode.values().length];
        try {
            Packets$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$packets$util$BookCrashMode[BookCrashMode.None.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Packets$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$packets$util$BookCrashMode[BookCrashMode.Creative.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Packets$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$packets$util$BookCrashMode[BookCrashMode.ClickWindow.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Packets$1.$SwitchMap$me$earth$earthhack$impl$modules$misc$packets$util$BookCrashMode[BookCrashMode.Console.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
