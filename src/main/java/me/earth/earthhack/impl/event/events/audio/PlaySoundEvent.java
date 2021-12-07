/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 */
package me.earth.earthhack.impl.event.events.audio;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.audio.ISound;

public class PlaySoundEvent
extends Event {
    private final ISound sound;

    public PlaySoundEvent(ISound sound) {
        this.sound = sound;
    }

    public ISound getSound() {
        return this.sound;
    }
}

