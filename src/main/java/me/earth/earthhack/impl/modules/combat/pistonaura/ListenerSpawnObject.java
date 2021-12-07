package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject
extends ModuleListener<PistonAura, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(PistonAura module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        SPacketSpawnObject packet;
        if (((PistonAura)this.module).stage == PistonStage.BREAK && ((PistonAura)this.module).current != null && ((PistonAura)this.module).breakTimer.passed(((PistonAura)this.module).breakDelay.getValue().intValue()) && Managers.SWITCH.getLastSwitch() > (long)((PistonAura)this.module).coolDown.getValue().intValue() && (packet = (SPacketSpawnObject)event.getPacket()).getType() == 51) {
            BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            try {
                if (((PistonAura)this.module).current.getCrystalPos().equals(pos.down()) || ((PistonAura)this.module).current.getStartPos().equals(pos.down()) && (((PistonAura)this.module).rotate.getValue() == Rotate.None || RotationUtil.isLegit(pos)) || RotationUtil.isLegit(pos.up())) {
                    ((PistonAura)this.module).entityId = packet.getEntityID();
                    if (!((PistonAura)this.module).instant.getValue().booleanValue() || !((PistonAura)this.module).explode.getValue().booleanValue()) {
                        return;
                    }
                    ICPacketUseEntity useEntity = (ICPacketUseEntity)((Object)new CPacketUseEntity());
                    useEntity.setAction(CPacketUseEntity.Action.ATTACK);
                    useEntity.setEntityId(packet.getEntityID());
                    ListenerSpawnObject.mc.player.connection.sendPacket((Packet)((Object)useEntity));
                    ListenerSpawnObject.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    ((PistonAura)this.module).breakTimer.reset();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}
