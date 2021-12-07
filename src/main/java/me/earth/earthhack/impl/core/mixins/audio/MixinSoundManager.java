/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.SoundManager
 */
package me.earth.earthhack.impl.core.mixins.audio;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.audio.PlaySoundEvent;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SoundManager.class})
public class MixinSoundManager {
    @Shadow
    private boolean loaded;

    @Inject(method={"playSound"}, at={@At(value="HEAD")}, cancellable=true)
    private void playSoundHook(ISound p_sound, CallbackInfo ci) {
        if (this.loaded) {
            PlaySoundEvent event = new PlaySoundEvent(p_sound);
            Bus.EVENT_BUS.post(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }
}

