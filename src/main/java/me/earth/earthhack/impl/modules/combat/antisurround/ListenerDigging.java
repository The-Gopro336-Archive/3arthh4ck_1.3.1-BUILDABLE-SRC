/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 */
package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.antisurround.PreCrystalFunction;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import net.minecraft.network.play.client.CPacketPlayerDigging;

final class ListenerDigging
extends ModuleListener<AntiSurround, PacketEvent.Send<CPacketPlayerDigging>> {
    private final AntiSurroundFunction function;

    public ListenerDigging(AntiSurround module) {
        super(module, PacketEvent.Send.class, -1000, CPacketPlayerDigging.class);
        this.function = new PreCrystalFunction(module);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event) {
        if (event.isCancelled() || ((CPacketPlayerDigging)event.getPacket()).getAction() != CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK || ((AntiSurround)this.module).holdingCheck() || !((AntiSurround)this.module).preCrystal.getValue().booleanValue() || !((ICPacketPlayerDigging)event.getPacket()).isClientSideBreaking()) {
            return;
        }
        ((AntiSurround)this.module).onBlockBreak(((CPacketPlayerDigging)event.getPacket()).getPosition(), Managers.ENTITIES.getPlayersAsync(), Managers.ENTITIES.getEntitiesAsync(), this.function);
    }
}

