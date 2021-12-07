/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;

final class ListenerBlockBreak
extends ModuleListener<AntiSurround, BlockDestroyEvent> {
    public ListenerBlockBreak(AntiSurround module) {
        super(module, BlockDestroyEvent.class);
    }

    @Override
    public void invoke(BlockDestroyEvent event) {
        if (((AntiSurround)this.module).active.get() || !((AntiSurround)this.module).normal.getValue().booleanValue() || event.isCancelled() || event.isUsed() || event.getStage() != Stage.PRE || ListenerBlockBreak.mc.player == null || ((AntiSurround)this.module).holdingCheck()) {
            return;
        }
        event.setUsed(true);
        ((AntiSurround)this.module).onBlockBreak(event.getPos(), ListenerBlockBreak.mc.world.playerEntities, ListenerBlockBreak.mc.world.loadedEntityList);
    }
}

