package me.earth.earthhack.impl.modules.misc.packets;

import java.util.List;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

final class ListenerSound
extends ModuleListener<Packets, PacketEvent.Receive<SPacketSoundEffect>> {
    public ListenerSound(Packets module) {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
        List<Entity> entities;
        SPacketSoundEffect packet;
        if (((Packets)this.module).fastSetDead.getValue().booleanValue() && ListenerSound.mc.player != null && (packet = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && (entities = Managers.ENTITIES.getEntities()) != null) {
            Managers.SET_DEAD.removeCrystals(new Vec3d(packet.getX(), packet.getY(), packet.getZ()), 11.0f, entities);
        }
    }
}
