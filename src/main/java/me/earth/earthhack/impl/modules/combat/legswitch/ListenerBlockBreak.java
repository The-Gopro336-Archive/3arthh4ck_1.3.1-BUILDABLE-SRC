/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 */
package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import net.minecraft.init.Blocks;

final class ListenerBlockBreak
extends ModuleListener<LegSwitch, BlockDestroyEvent> {
    private final BlockStateHelper helper = new BlockStateHelper();

    public ListenerBlockBreak(LegSwitch module) {
        super(module, BlockDestroyEvent.class, 11);
    }

    @Override
    public void invoke(BlockDestroyEvent event) {
        if (!((LegSwitch)this.module).breakBlock.getValue().booleanValue() || event.isCancelled() || event.isUsed() || event.getStage() != Stage.PRE) {
            return;
        }
        event.setUsed(true);
        this.helper.addBlockState(event.getPos(), Blocks.AIR.getDefaultState());
        ((LegSwitch)this.module).startCalculation(this.helper);
        this.helper.delete(event.getPos());
    }
}

