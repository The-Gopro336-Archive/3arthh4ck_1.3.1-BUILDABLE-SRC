/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketAnimation
 */
package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.network.play.server.SPacketAnimation;

final class ListenerAnimation
extends ModuleListener<NoRender, PacketEvent.Receive<SPacketAnimation>> {
    public ListenerAnimation(NoRender module) {
        super(module, PacketEvent.Receive.class, SPacketAnimation.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketAnimation> event) {
        if (((NoRender)this.module).critParticles.getValue().booleanValue() && (((SPacketAnimation)event.getPacket()).getAnimationType() == 4 || ((SPacketAnimation)event.getPacket()).getAnimationType() == 5)) {
            event.setCancelled(true);
        }
    }
}

