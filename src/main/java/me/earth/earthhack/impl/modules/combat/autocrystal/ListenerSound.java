/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;

final class ListenerSound
extends SoundObserver {
    private final AutoCrystal module;

    public ListenerSound(AutoCrystal module) {
        super(module.soundRemove::getValue);
        this.module = module;
    }

    @Override
    public void onChange(SPacketSoundEffect value) {
        if (this.module.soundThread.getValue().booleanValue()) {
            this.module.threadHelper.startThread(new BlockPos[0]);
        }
    }

    @Override
    public boolean shouldBeNotified() {
        return true;
    }
}

