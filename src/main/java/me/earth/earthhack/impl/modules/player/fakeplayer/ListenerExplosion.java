package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

final class ListenerExplosion
extends ModuleListener<FakePlayer, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(FakePlayer module) {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        if (((FakePlayer)this.module).damage.getValue().booleanValue()) {
            mc.addScheduledTask(() -> this.handleExplosion((SPacketExplosion)event.getPacket()));
        }
    }

    private void handleExplosion(SPacketExplosion packet) {
        double z;
        double y;
        if (ListenerExplosion.mc.world == null || ((FakePlayer)this.module).fakePlayer == null || !((FakePlayer)this.module).isEnabled()) {
            return;
        }
        double x = packet.getX();
        double distance = ((FakePlayer)this.module).fakePlayer.getDistance(x, y = packet.getY(), z = packet.getZ()) / 12.0;
        if (distance > 1.0) {
            return;
        }
        float size = packet.getStrength();
        double density = ListenerExplosion.mc.world.getBlockDensity(new Vec3d(x, y, z), ((FakePlayer)this.module).fakePlayer.getEntityBoundingBox());
        double densityDistance = distance = (1.0 - distance) * density;
        float damage = (float)((densityDistance * densityDistance + distance) / 2.0 * 7.0 * (double)size * 2.0 + 1.0);
        DamageSource damageSource = DamageSource.causeExplosionDamage((Explosion)new Explosion(ListenerExplosion.mc.world, ListenerExplosion.mc.player, x, y, z, size, false, true));
        float limbSwing = ((FakePlayer)this.module).fakePlayer.limbSwingAmount;
        ((FakePlayer)this.module).fakePlayer.attackEntityFrom(damageSource, damage);
        ((FakePlayer)this.module).fakePlayer.limbSwingAmount = limbSwing;
    }
}
