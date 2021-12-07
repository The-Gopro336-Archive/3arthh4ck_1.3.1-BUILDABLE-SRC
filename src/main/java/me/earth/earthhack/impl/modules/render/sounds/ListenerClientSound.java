package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.audio.PlaySoundEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

final class ListenerClientSound
extends ModuleListener<Sounds, PlaySoundEvent> {
    public ListenerClientSound(Sounds module) {
        super(module, PlaySoundEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PlaySoundEvent event) {
        ITextComponent c;
        boolean cancelled = event.isCancelled();
        if (!((Sounds)this.module).client.getValue().booleanValue() || cancelled && !((Sounds)this.module).cancelled.getValue().booleanValue()) {
            return;
        }
        ISound sound = event.getSound();
        ResourceLocation location = sound.getSoundLocation();
        SoundEventAccessor access = mc.getSoundHandler().getAccessor(location);
        ITextComponent iTextComponent = c = access == null ? null : access.getSubtitle();
        if (c != null && ((Sounds)this.module).isValid(c.getUnformattedComponentText()) || c == null && ((Sounds)this.module).isValid(location.toString())) {
            String s = c != null ? c.getUnformattedComponentText() : location.toString();
            ((Sounds)this.module).sounds.put(new SoundPosition(sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), (cancelled ? "Cancelled: " : "") + s), System.currentTimeMillis());
        }
    }
}
