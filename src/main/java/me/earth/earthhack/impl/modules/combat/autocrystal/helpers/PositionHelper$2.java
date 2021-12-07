package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;

class PositionHelper$2
extends EventListener<UpdateEntitiesEvent> {
    PositionHelper$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(UpdateEntitiesEvent event) {
        HashMap tempMap = new HashMap(PositionHelper.this.motionTrackerMap);
        for (Map.Entry entry : tempMap.entrySet()) {
            if (EntityUtil.isDead((Entity)entry.getValue())) {
                PositionHelper.this.motionTrackerMap.remove(entry.getValue());
                continue;
            }
            ((MotionTracker)entry.getValue()).updateFromTrackedEntity();
        }
    }
}
