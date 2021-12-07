package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;
import me.earth.earthhack.impl.modules.render.sounds.util.CoordLogger;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.math.MathHelper;

final class ListenerEffect
extends ModuleListener<Sounds, PacketEvent.Receive<SPacketEffect>> {
    public ListenerEffect(Sounds module) {
        super(module, PacketEvent.Receive.class, SPacketEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEffect> event) {
        SPacketEffect packet = (SPacketEffect)event.getPacket();
        switch (packet.getSoundType()) {
            case 1038: {
                if (!((Sounds)this.module).portal.getValue().booleanValue()) break;
                ((Sounds)this.module).log("EndPortal: " + packet.getSoundPos().getX() + "-X, " + packet.getSoundPos().getY() + "-Y, " + packet.getSoundPos().getZ() + "-Z.");
                break;
            }
            case 1023: {
                if (!((Sounds)this.module).wither.getValue().booleanValue()) break;
                if (((Sounds)this.module).coordLogger.getValue() == CoordLogger.Vanilla) {
                    ((Sounds)this.module).log("Wither: " + packet.getSoundPos().getX() + "-X, " + packet.getSoundPos().getY() + "-Y, " + packet.getSoundPos().getZ() + "-Z.");
                    break;
                }
                double x = (double)packet.getSoundPos().getX() - ListenerEffect.mc.player.posX;
                double z = (double)packet.getSoundPos().getZ() - ListenerEffect.mc.player.posZ;
                double yaw = MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(x, z) - 90.0));
                ((Sounds)this.module).log("Wither: " + ListenerEffect.mc.player.posX + "-X, " + ListenerEffect.mc.player.posZ + "-Z, " + yaw + "-Angle.");
                break;
            }
            case 1028: {
                if (!((Sounds)this.module).dragon.getValue().booleanValue()) break;
                double x = (double)packet.getSoundPos().getX() - ListenerEffect.mc.player.posX;
                double z = (double)packet.getSoundPos().getZ() - ListenerEffect.mc.player.posZ;
                double yaw = MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(x, z) - 90.0));
                ((Sounds)this.module).log("Dragon: " + ListenerEffect.mc.player.posX + "-X, " + ListenerEffect.mc.player.posZ + "-Z, " + yaw + "-Angle.");
                break;
            }
        }
    }
}
