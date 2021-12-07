/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.SoundEventAccessor
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

final class ListenerSound
extends ModuleListener<Sounds, PacketEvent.Receive<SPacketSoundEffect>> {
    public ListenerSound(Sounds module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
        ITextComponent c;
        SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
        if (((Sounds)this.module).thunder.getValue().booleanValue() && packet.getCategory() == SoundCategory.WEATHER && packet.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER) {
            double x = packet.getX() - ListenerSound.mc.player.posX;
            double z = packet.getZ() - ListenerSound.mc.player.posZ;
            double yaw = MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(x, z) - 90.0));
            ((Sounds)this.module).log("Lightning: " + ListenerSound.mc.player.posX + "-x, " + ListenerSound.mc.player.posZ + "-z, " + yaw + "-angle.");
        }
        boolean cancelled = event.isCancelled();
        if (((Sounds)this.module).client.getValue().booleanValue() || !((Sounds)this.module).packet.getValue().booleanValue() || cancelled && !((Sounds)this.module).cancelled.getValue().booleanValue()) {
            return;
        }
        ResourceLocation location = packet.getSound().getSoundName();
        SoundEventAccessor access = mc.getSoundHandler().getAccessor(location);
        ITextComponent iTextComponent = c = access == null ? null : access.getSubtitle();
        if (c != null && ((Sounds)this.module).isValid(c.getUnformattedComponentText()) || c == null && ((Sounds)this.module).isValid(location.toString())) {
            String s = c != null ? c.getUnformattedComponentText() : location.toString();
            ((Sounds)this.module).sounds.put(new SoundPosition(packet.getX(), packet.getY(), packet.getZ(), (cancelled ? "Cancelled: " : "") + s), System.currentTimeMillis());
        }
    }
}

