package me.earth.earthhack.impl.modules.render.waypoints;

import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointType;

class WayPoints$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointType;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointType = new int[WayPointType.values().length];
        try {
            WayPoints$1.$SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointType[WayPointType.OVW.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WayPoints$1.$SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointType[WayPointType.End.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WayPoints$1.$SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointType[WayPointType.Nether.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
