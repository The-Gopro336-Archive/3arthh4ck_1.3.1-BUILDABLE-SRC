/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.lagometer.LagOMeter;
import me.earth.earthhack.impl.util.math.MathUtil;

final class ListenerText
extends ModuleListener<LagOMeter, Render2DEvent> {
    public ListenerText(LagOMeter module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        String toRender;
        long time;
        if (!((LagOMeter)this.module).response.getValue().booleanValue() && !((LagOMeter)this.module).lagTime.getValue().booleanValue()) {
            return;
        }
        ((LagOMeter)this.module).lagMessage = null;
        ((LagOMeter)this.module).respondingMessage = null;
        if (((LagOMeter)this.module).response.getValue().booleanValue() && Managers.SERVER.lastResponse() > (long)((LagOMeter)this.module).responseTime.getValue().intValue()) {
            ((LagOMeter)this.module).respondingMessage = "\u00a7cServer not responding. (" + MathUtil.round((double)Managers.SERVER.lastResponse() / 1000.0, 1) + ")";
        }
        if (((LagOMeter)this.module).lagTime.getValue().booleanValue() && (time = (long)((LagOMeter)this.module).chatTime.getValue().intValue() - (System.currentTimeMillis() - Managers.NCP.getTimeStamp())) >= 0L) {
            ((LagOMeter)this.module).lagMessage = "\u00a7cServer lagged you back (" + MathUtil.round((double)time / 1000.0, 1) + ")";
        }
        if ((toRender = ((LagOMeter)this.module).respondingMessage) == null) {
            toRender = ((LagOMeter)this.module).lagMessage;
        }
        if (toRender == null || !((LagOMeter)this.module).render.getValue().booleanValue()) {
            return;
        }
        Managers.TEXT.drawString(toRender, (float)((LagOMeter)this.module).resolution.getScaledWidth() / 2.0f - (float)Managers.TEXT.getStringWidth(toRender) / 2.0f + 2.0f, 20.0f, -1, true);
    }
}

