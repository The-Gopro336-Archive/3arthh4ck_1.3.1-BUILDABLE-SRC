package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject
extends ModuleListener<BlockLag, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(BlockLag module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (!((BlockLag)this.module).instantAttack.getValue().booleanValue() || ((SPacketSpawnObject)event.getPacket()).getType() != 51 || ListenerSpawnObject.mc.world == null || Managers.SWITCH.getLastSwitch() > (long)((BlockLag)this.module).cooldown.getValue().intValue() || !KeyBoardUtil.isKeyDown(((BlockLag)this.module).getBind()) && !((BlockLag)this.module).isEnabled() || DamageUtil.isWeaknessed() || ListenerSpawnObject.mc.world.getBlockState(PositionUtil.getPosition(RotationUtil.getRotationPlayer()).up(2)).func_185904_a().blocksMovement()) {
            return;
        }
        EntityPlayerSP player = ListenerSpawnObject.mc.player;
        if (player != null) {
            BlockPos pos = PositionUtil.getPosition(player);
            if (!ListenerSpawnObject.mc.world.getBlockState(pos).func_185904_a().isReplaceable()) {
                return;
            }
            EntityEnderCrystal crystal = new EntityEnderCrystal(ListenerSpawnObject.mc.world, ((SPacketSpawnObject)event.getPacket()).getX(), ((SPacketSpawnObject)event.getPacket()).getY(), ((SPacketSpawnObject)event.getPacket()).getZ());
            if (crystal.getEntityBoundingBox().intersects(new AxisAlignedBB(pos))) {
                float damage = DamageUtil.calculate(crystal);
                if (((BlockLag)this.module).pop.getValue().shouldPop(damage, ((BlockLag)this.module).popTime.getValue())) {
                    PacketUtil.attack(((SPacketSpawnObject)event.getPacket()).getEntityID());
                }
            }
        }
    }
}
