package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;

final class ListenerDestroyBlock
extends ModuleListener<AutoCrystal, BlockDestroyEvent> {
    public ListenerDestroyBlock(AutoCrystal module) {
        super(module, BlockDestroyEvent.class, -10);
    }

    @Override
    public void invoke(BlockDestroyEvent event) {
        if (((AutoCrystal)this.module).blockDestroyThread.getValue().booleanValue() && event.getStage() == Stage.PRE && ((AutoCrystal)this.module).multiThread.getValue().booleanValue() && !event.isCancelled() && !event.isUsed() && HelperUtil.validChange(event.getPos(), ListenerDestroyBlock.mc.world.playerEntities)) {
            ((AutoCrystal)this.module).threadHelper.startThread(event.getPos().down());
        }
    }
}
