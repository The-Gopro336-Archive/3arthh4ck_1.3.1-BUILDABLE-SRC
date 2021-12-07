package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.LegConstellation;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerSpawnObject
extends ModuleListener<LegSwitch, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(LegSwitch module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        EntityPlayerSP player = ListenerSpawnObject.mc.player;
        if (((LegSwitch)this.module).instant.getValue().booleanValue() && player != null && Managers.SWITCH.getLastSwitch() >= (long)((LegSwitch)this.module).coolDown.getValue().intValue() && !DamageUtil.isWeaknessed() && ((LegSwitch)this.module).timer.passed(((LegSwitch)this.module).delay.getValue().intValue()) && ((SPacketSpawnObject)event.getPacket()).getType() == 51) {
            SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
            LegConstellation constellation = ((LegSwitch)this.module).constellation;
            if (!(constellation == null || constellation.firstNeedsObby || constellation.secondNeedsObby || !InventoryUtil.isHolding(Items.END_CRYSTAL) && ((LegSwitch)this.module).autoSwitch.getValue() == LegAutoSwitch.None)) {
                BlockPos previous;
                double z;
                double y;
                double x = packet.getX();
                BlockPos pos = new BlockPos(x, (y = packet.getY()) - 1.0, z = packet.getZ());
                if (!pos.equals(previous = ((LegSwitch)this.module).targetPos)) {
                    return;
                }
                BlockPos targetPos = constellation.firstPos.equals(previous) ? constellation.secondPos : constellation.firstPos;
                EntityEnderCrystal entity = new EntityEnderCrystal(ListenerSpawnObject.mc.world, x, y, z);
                if (!((LegSwitch)this.module).rotate.getValue().noRotate(ACRotate.Break) && !RotationUtil.isLegit(entity, new Entity[0]) || !((LegSwitch)this.module).rotate.getValue().noRotate(ACRotate.Place) && !RotationUtil.isLegit(targetPos)) {
                    return;
                }
                RayTraceResult result = RotationUtil.rayTraceTo(targetPos, ListenerSpawnObject.mc.world);
                if (result == null) {
                    result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
                }
                entity.setUniqueId(packet.getUniqueId());
                entity.setEntityId(packet.getEntityID());
                entity.setShowBottom(false);
                int slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
                RayTraceResult finalResult = result;
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    int last = player.inventory.currentItem;
                    EnumHand hand = player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || slot != -2 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    player.connection.sendPacket(new CPacketUseEntity(entity));
                    player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    InventoryUtil.switchTo(slot);
                    player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetPos, finalResult.sideHit, hand, (float)finalResult.hitVec.x, (float)finalResult.hitVec.y, (float)finalResult.hitVec.z));
                    player.connection.sendPacket(new CPacketAnimation(hand));
                    if (last != slot && ((LegSwitch)this.module).autoSwitch.getValue() != LegAutoSwitch.Keep) {
                        InventoryUtil.switchTo(last);
                    }
                });
                ((LegSwitch)this.module).targetPos = targetPos;
                if (((LegSwitch)this.module).setDead.getValue().booleanValue()) {
                    event.addPostEvent(() -> {
                        Entity e;
                        if (ListenerSpawnObject.mc.world != null && (e = ListenerSpawnObject.mc.world.getEntityByID(packet.getEntityID())) != null) {
                            Managers.SET_DEAD.setDead(e);
                        }
                    });
                }
                ((LegSwitch)this.module).timer.reset(((LegSwitch)this.module).delay.getValue().intValue());
            }
        }
    }
}
