/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;

final class PacketFlyData
extends DefaultData<PacketFly> {
    public PacketFlyData(PacketFly module) {
        super(module);
        this.register(module.mode, "-Setback slow PacketFly, doesn't predict LagBacks.\n-Fast standard PacketFly mode.\n-Factor like Fast (when Factor is 1.0) but you can use the Factor Setting to make it go even faster (desyncs).\n-Slow similar to SetBack, but still predicts LagBacks, also uses Factor (desync).");
        this.register(module.factor, "The Speed multiplier when using Mode Factor or Slow.");
        this.register(module.phase, "-Off don't phase\n-Semi phase\n-Full phase fully into blocks.");
        this.register(module.type, "-Down sends an invalid packet going down\n-Up sends an invalid packet going up\n-Preserve sends invalid packets with random offsets.");
        this.register(module.antiKick, "Makes you glide down slowly to prevent you from getting kicked.");
        this.register(module.conceal, "Multiplier for when you PacketFly inside the void. With low values (like 0.4) and Mode Preserve certain AntiCheats that prevent flying in the void (2b2tpvp) can be bypassed.");
        this.register(module.answer, "Answers LagBacks legitimately.");
        this.register(module.bbOffset, "Makes your hitbox smaller for detecting phase.");
        this.register(module.invalidY, "Offset for the OutOfBounds packets.");
        this.register(module.sendTeleport, "If LagBacks should be predicted.");
        this.register(module.concealY, "If the player is under this Y-Coordinate the C-Multiplier will be applied. This is important for certain AntiCheats (2b2tpvp - NoVoidPacketFly)");
        this.register(module.conceal, "Lower -> Slower, will make you slower.");
        this.register(module.ySpeed, "Multiplier for your vertical Speed.");
        this.register(module.xzSpeed, "Multiplier for your horizontal Speed.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Uses a special exploit to Fly and Phase.";
    }
}

