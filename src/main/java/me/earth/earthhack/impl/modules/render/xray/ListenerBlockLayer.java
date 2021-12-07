/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.BlockRenderLayer
 */
package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.impl.event.events.render.BlockLayerEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import net.minecraft.util.BlockRenderLayer;

final class ListenerBlockLayer
extends ModuleListener<XRay, BlockLayerEvent> {
    public ListenerBlockLayer(XRay module) {
        super(module, BlockLayerEvent.class);
    }

    @Override
    public void invoke(BlockLayerEvent event) {
        if (!((XRay)this.module).isValid(event.getBlock().getLocalizedName())) {
            event.setLayer(BlockRenderLayer.TRANSLUCENT);
        }
    }
}

