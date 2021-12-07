/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.antipackets;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.antipackets.AntiPackets;

final class AntiPacketData
extends DefaultData<AntiPackets> {
    public AntiPacketData(AntiPackets antiPackets) {
        super(antiPackets);
    }

    @Override
    public int getColor() {
        return -65507;
    }

    @Override
    public String getDescription() {
        return "Cancel packets that you receive (SPackets) or send (CPackets).";
    }
}

