package me.earth.earthhack.impl.modules.combat.webaura;

import me.earth.earthhack.impl.modules.combat.autotrap.modes.TrapTarget;

class ListenerWebAura$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autotrap$modes$TrapTarget;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autotrap$modes$TrapTarget = new int[TrapTarget.values().length];
        try {
            ListenerWebAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autotrap$modes$TrapTarget[TrapTarget.Closest.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerWebAura$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autotrap$modes$TrapTarget[TrapTarget.Untrapped.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
