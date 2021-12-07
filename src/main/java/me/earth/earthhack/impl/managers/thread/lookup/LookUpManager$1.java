package me.earth.earthhack.impl.managers.thread.lookup;

import me.earth.earthhack.impl.managers.thread.lookup.LookUp;

class LookUpManager$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$managers$thread$lookup$LookUp$Type;

    static {
        $SwitchMap$me$earth$earthhack$impl$managers$thread$lookup$LookUp$Type = new int[LookUp.Type.values().length];
        try {
            LookUpManager$1.$SwitchMap$me$earth$earthhack$impl$managers$thread$lookup$LookUp$Type[LookUp.Type.NAME.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LookUpManager$1.$SwitchMap$me$earth$earthhack$impl$managers$thread$lookup$LookUp$Type[LookUp.Type.UUID.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LookUpManager$1.$SwitchMap$me$earth$earthhack$impl$managers$thread$lookup$LookUp$Type[LookUp.Type.HISTORY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
