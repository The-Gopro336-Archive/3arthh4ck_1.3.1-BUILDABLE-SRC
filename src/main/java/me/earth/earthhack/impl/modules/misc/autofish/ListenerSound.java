/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autofish.AutoFish;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.Vec3d;

final class ListenerSound
extends ModuleListener<AutoFish, PacketEvent.Receive<SPacketSoundEffect>> {
    public ListenerSound(AutoFish module) {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
        block2: {
            block3: {
                EntityFishHook fish;
                SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
                if (!packet.getSound().equals((Object)SoundEvents.ENTITY_BOBBER_SPLASH) || (fish = ListenerSound.mc.player.fishEntity) == null || !ListenerSound.mc.player.equals((Object)fish.getAngler())) break block2;
                if (((AutoFish)this.module).range.getValue() == 0.0) break block3;
                Vec3d vec3d = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
                if (!(fish.getPositionVector().distanceTo(vec3d) <= ((AutoFish)this.module).range.getValue())) break block2;
            }
            ((AutoFish)this.module).splash = true;
        }
    }
}

