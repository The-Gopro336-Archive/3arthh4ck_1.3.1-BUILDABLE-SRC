package me.earth.earthhack.impl.modules.render.waypoints;

import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointRender;

class ListenerRender$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointRender;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointRender = new int[WayPointRender.values().length];
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointRender[WayPointRender.Distance.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointRender[WayPointRender.Coordinates.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ListenerRender$1.$SwitchMap$me$earth$earthhack$impl$modules$render$waypoints$mode$WayPointRender[WayPointRender.Both.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
