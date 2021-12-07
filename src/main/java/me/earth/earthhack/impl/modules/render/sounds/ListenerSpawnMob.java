package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnMob
extends ModuleListener<Sounds, PacketEvent.Receive<SPacketSpawnMob>> {
    public ListenerSpawnMob(Sounds module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnMob.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnMob> event) {
        SPacketSpawnMob p;
        EntityPlayerSP player = ListenerSpawnMob.mc.player;
        if (player != null && ((Sounds)this.module).slimes.getValue().booleanValue() && (p = (SPacketSpawnMob)event.getPacket()).getEntityType() == 55 && p.getY() <= 40.0 && !ListenerSpawnMob.mc.world.getBiome(player.getPosition()).getBiomeName().toLowerCase().contains("swamp")) {
            BlockPos pos = new BlockPos(p.getX(), p.getY(), p.getZ());
            ((Sounds)this.module).log("Slime: " + ListenerSpawnMob.mc.world.getChunkFromBlockCoords((BlockPos)pos).x + "-ChunkX, " + ListenerSpawnMob.mc.world.getChunkFromBlockCoords((BlockPos)pos).z + "-ChunkZ.");
        }
    }
}
