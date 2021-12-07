package me.earth.earthhack.impl.managers.minecraft.combat.util;

import java.util.function.BooleanSupplier;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import net.minecraft.network.play.server.SPacketSoundEffect;

public class SimpleSoundObserver
extends SoundObserver {
    public SimpleSoundObserver() {
        this(() -> true);
    }

    public SimpleSoundObserver(BooleanSupplier soundRemove) {
        super(soundRemove);
    }

    @Override
    public void onChange(SPacketSoundEffect value) {
    }
}
