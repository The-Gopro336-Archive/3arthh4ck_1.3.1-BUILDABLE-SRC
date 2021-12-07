package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.suicide.SuicideMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

final class ListenerMotion
extends ModuleListener<Suicide, MotionUpdateEvent> {
    public ListenerMotion(Suicide module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        int slot;
        if (((Suicide)this.module).displaying) {
            return;
        }
        if (ListenerMotion.mc.player.getHealth() <= 0.0f) {
            ((Suicide)this.module).disable();
            return;
        }
        if (((Suicide)this.module).mode.getValue() == SuicideMode.Command) {
            NetworkUtil.sendPacketNoEvent(new CPacketChatMessage("/kill"));
            ((Suicide)this.module).disable();
            return;
        }
        if (((Suicide)this.module).throwAwayTotem.getValue().booleanValue() && InventoryUtil.validScreen() && ((Suicide)this.module).timer.passed(((Suicide)this.module).throwDelay.getValue().intValue()) && ListenerMotion.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> ListenerMotion.mc.playerController.windowClick(0, 45, 1, ClickType.THROW, ListenerMotion.mc.player));
            ((Suicide)this.module).timer.reset();
        }
        if ((slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0])) == -1) {
            ModuleUtil.disableRed((Module)this.module, "No Crystals found!");
            return;
        }
        if (event.getStage() == Stage.PRE) {
            ((Suicide)this.module).result = null;
            ((Suicide)this.module).pos = null;
            ((Suicide)this.module).crystal = null;
            if (((Suicide)this.module).breakTimer.passed(((Suicide)this.module).breakDelay.getValue().intValue())) {
                Entity crystal = null;
                float maxDamage = Float.MIN_VALUE;
                for (Entity entity : ListenerMotion.mc.world.loadedEntityList) {
                    float damage;
                    if (entity.isDead || !(entity instanceof EntityEnderCrystal) || !(RotationUtil.getRotationPlayer().getDistanceSq(entity) < (double)MathUtil.square(((Suicide)this.module).breakRange.getValue().floatValue())) || !RotationUtil.getRotationPlayer().canEntityBeSeen(entity) && !(RotationUtil.getRotationPlayer().getDistanceSq(entity) < (double)MathUtil.square(((Suicide)this.module).trace.getValue().floatValue())) || !((damage = DamageUtil.calculate(entity)) > maxDamage)) continue;
                    maxDamage = damage;
                    crystal = entity;
                }
                if (crystal != null) {
                    ((Suicide)this.module).crystal = crystal;
                    if (((Suicide)this.module).rotate.getValue().booleanValue()) {
                        float[] rotations = RotationUtil.getRotations(crystal);
                        event.setYaw(rotations[0]);
                        event.setPitch(rotations[1]);
                    }
                    return;
                }
            }
            if (!((Suicide)this.module).placeTimer.passed(((Suicide)this.module).placeDelay.getValue().intValue())) {
                return;
            }
            float maxDamage = Float.MIN_VALUE;
            BlockPos middle = PositionUtil.getPosition();
            int x = middle.getX();
            int y = middle.getY();
            int z = middle.getZ();
            int maxRadius = Sphere.getRadius(6.0);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos bestPos = null;
            for (int i = 1; i < maxRadius; ++i) {
                float damage;
                Vec3i v = Sphere.get(i);
                pos.setPos(x + v.getX(), y + v.getY(), z + v.getZ());
                if (!BlockUtil.canPlaceCrystal(pos, false, ((Suicide)this.module).newVer.getValue(), ListenerMotion.mc.world.loadedEntityList, ((Suicide)this.module).newVerEntities.getValue(), 0L) || !BlockUtil.isCrystalPosInRange(pos, ((Suicide)this.module).placeRange.getValue().floatValue(), ((Suicide)this.module).placeRange.getValue().floatValue(), ((Suicide)this.module).trace.getValue().floatValue()) || !((damage = DamageUtil.calculate(pos)) > maxDamage)) continue;
                maxDamage = damage;
                bestPos = pos.toImmutable();
            }
            if (bestPos != null) {
                Ray result = RayTraceFactory.fullTrace(RotationUtil.getRotationPlayer(), ListenerMotion.mc.world, bestPos, -1.0);
                if (result == null) {
                    return;
                }
                if (((Suicide)this.module).rotate.getValue().booleanValue()) {
                    event.setYaw(result.getRotations()[0]);
                    event.setPitch(result.getRotations()[1]);
                }
                ((Suicide)this.module).pos = bestPos;
                ((Suicide)this.module).result = result.getResult();
            }
        } else if (event.getStage() == Stage.POST) {
            if (((Suicide)this.module).crystal != null) {
                ListenerMotion.mc.player.connection.sendPacket(new CPacketUseEntity(((Suicide)this.module).crystal));
                ListenerMotion.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                ((Suicide)this.module).breakTimer.reset();
                return;
            }
            if (((Suicide)this.module).pos != null && ((Suicide)this.module).result != null) {
                float[] r = RayTraceUtil.hitVecToPlaceVec(((Suicide)this.module).pos, ((Suicide)this.module).result.hitVec);
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    int last = ListenerMotion.mc.player.inventory.currentItem;
                    InventoryUtil.switchTo(slot);
                    ListenerMotion.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(((Suicide)this.module).pos, ((Suicide)this.module).result.sideHit, InventoryUtil.getHand(slot), r[0], r[1], r[2]));
                    ListenerMotion.mc.player.connection.sendPacket(new CPacketAnimation(InventoryUtil.getHand(slot)));
                    if (((Suicide)this.module).silent.getValue().booleanValue()) {
                        InventoryUtil.switchTo(last);
                    }
                });
                ((Suicide)this.module).placed.add(((Suicide)this.module).pos);
                ((Suicide)this.module).placeTimer.reset();
            }
        }
    }
}
