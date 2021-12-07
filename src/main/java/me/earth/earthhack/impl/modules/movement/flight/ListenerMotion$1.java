package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;

class ListenerMotion$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode = new int[FlightMode.values().length];
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode[FlightMode.ConstantiamNew.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode[FlightMode.ConstoHare.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode[FlightMode.ConstoHareFast.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode[FlightMode.Constantiam.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode[FlightMode.Normal.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerMotion$1.$SwitchMap$me$earth$earthhack$impl$modules$movement$flight$mode$FlightMode[FlightMode.Jump.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
