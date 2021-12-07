package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange
extends ModuleListener<AntiSurround, PacketEvent.Post<SPacketBlockChange>> {
    public ListenerBlockChange(AntiSurround module) {
        super(module, PacketEvent.Post.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Post<SPacketBlockChange> event) {
        if (!((AntiSurround)this.module).async.getValue().booleanValue() || ((AntiSurround)this.module).active.get() || ListenerBlockChange.mc.player == null || ((AntiSurround)this.module).holdingCheck()) {
            return;
        }
        if (((SPacketBlockChange)event.getPacket()).getBlockState().func_185904_a().isReplaceable()) {
            ((AntiSurround)this.module).onBlockBreak(((SPacketBlockChange)event.getPacket()).getBlockPosition(), Managers.ENTITIES.getPlayers(), Managers.ENTITIES.getEntities());
        }
    }
}
