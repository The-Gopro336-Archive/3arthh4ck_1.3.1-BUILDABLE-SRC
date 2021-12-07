/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSoundEffect
 */
package me.earth.earthhack.impl.managers.minecraft.combat.util;

import java.util.function.BooleanSupplier;
import me.earth.earthhack.api.observable.Observer;
import net.minecraft.network.play.server.SPacketSoundEffect;

public abstract class SoundObserver
implements Observer<SPacketSoundEffect> {
    private final BooleanSupplier soundRemove;

    public SoundObserver(BooleanSupplier soundRemove) {
        this.soundRemove = soundRemove;
    }

    public boolean shouldRemove() {
        return this.soundRemove.getAsBoolean();
    }

    public boolean shouldBeNotified() {
        return this.shouldRemove();
    }
}

