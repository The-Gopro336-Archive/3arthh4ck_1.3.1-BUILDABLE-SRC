/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemFood
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion
extends ModuleListener<Phase, MotionUpdateEvent> {
    private static final double[] off = new double[]{-0.025f, -0.028571428997176036, -0.033333333830038704, -0.04000000059604645, -0.05f, -0.06666666766007741, -0.1f, -0.2f, -0.04000000059604645, -0.033333333830038704, -0.028571428997176036, -0.025f};
    private final StopWatch timer = new StopWatch();

    public ListenerMotion(Phase module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        double xSpeed = (double)ListenerMotion.mc.player.getHorizontalFacing().getDirectionVec().getX() * 0.1;
        double zSpeed = (double)ListenerMotion.mc.player.getHorizontalFacing().getDirectionVec().getZ() * 0.1;
        switch (((Phase)this.module).mode.getValue()) {
            case Constantiam: {
                if (event.getStage() == Stage.PRE && ListenerMotion.mc.player.collidedHorizontally && !((Phase)this.module).isPhasing()) {
                    event.setY(event.getY() - 0.032);
                }
                if (event.getStage() == Stage.PRE && ((Phase)this.module).isPhasing() && ListenerMotion.mc.world.getBlockState(PositionUtil.getPosition().up()).getBlock() == Blocks.AIR) {
                    event.setY(event.getY() - 0.032);
                }
            }
            case Normal: {
                if (event.getStage() != Stage.PRE || !ListenerMotion.mc.player.isSneaking() || !((Phase)this.module).isPhasing() || ((Phase)this.module).requireForward.getValue().booleanValue() && !ListenerMotion.mc.gameSettings.keyBindForward.isKeyDown()) break;
                if (this.checkAutoClick()) {
                    return;
                }
                float yaw = ListenerMotion.mc.player.rotationYaw;
                ListenerMotion.mc.player.setEntityBoundingBox(ListenerMotion.mc.player.getEntityBoundingBox().offset(((Phase)this.module).distance.getValue() * Math.cos(Math.toRadians(yaw + 90.0f)), 0.0, ((Phase)this.module).distance.getValue() * Math.sin(Math.toRadians(yaw + 90.0f))));
                break;
            }
            case Sand: {
                ListenerMotion.mc.player.motionY = 0.0;
                if (ListenerMotion.mc.inGameHasFocus) {
                    if (ListenerMotion.mc.player.movementInput.jump) {
                        ListenerMotion.mc.player.motionY += 0.3;
                    }
                    if (ListenerMotion.mc.player.movementInput.sneak) {
                        ListenerMotion.mc.player.motionY -= 0.3;
                    }
                }
                ListenerMotion.mc.player.noClip = true;
                break;
            }
            case Packet: {
                if (!ListenerMotion.mc.player.collidedHorizontally || !((Phase)this.module).timer.passed(200L)) break;
                ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY + 0.05, ListenerMotion.mc.player.posZ, true));
                ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX + xSpeed * ((Phase)this.module).speed.getValue(), ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ + zSpeed * ((Phase)this.module).speed.getValue(), true));
                ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ, true));
                ((Phase)this.module).timer.reset();
                break;
            }
            case Skip: {
                if (event.getStage() != Stage.PRE || !ListenerMotion.mc.player.collidedHorizontally) break;
                if (!this.timer.passed(((Phase)this.module).skipTime.getValue().intValue())) {
                    if (((Phase)this.module).cancel.getValue().booleanValue()) {
                        event.setCancelled(true);
                    }
                    return;
                }
                float direction = ListenerMotion.mc.player.rotationYaw;
                if (ListenerMotion.mc.player.moveForward < 0.0f) {
                    direction += 180.0f;
                }
                if (ListenerMotion.mc.player.moveStrafing > 0.0f) {
                    direction -= 90.0f * (ListenerMotion.mc.player.moveForward < 0.0f ? -0.5f : (ListenerMotion.mc.player.moveForward > 0.0f ? 0.5f : 1.0f));
                }
                if (ListenerMotion.mc.player.moveStrafing < 0.0f) {
                    direction += 90.0f * (ListenerMotion.mc.player.moveForward < 0.0f ? -0.5f : (ListenerMotion.mc.player.moveForward > 0.0f ? 0.5f : 1.0f));
                }
                double x = Math.cos(Math.toRadians(direction + 90.0f)) * 0.2;
                double z = Math.sin(Math.toRadians(direction + 90.0f)) * 0.2;
                if (((Phase)this.module).limit.getValue().booleanValue()) {
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ, ListenerMotion.mc.player.onGround));
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX + x * (double)0.001f, ListenerMotion.mc.player.posY + (double)0.1f, ListenerMotion.mc.player.posZ + z * (double)0.001f, ListenerMotion.mc.player.onGround));
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX + x * (double)0.03f, 0.0, ListenerMotion.mc.player.posZ + z * (double)0.03f, ListenerMotion.mc.player.onGround));
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX + x * (double)0.06f, ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ + z * (double)0.06f, ListenerMotion.mc.player.onGround));
                    event.setCancelled(true);
                    this.timer.reset();
                    return;
                }
                for (int index = 0; index < off.length; ++index) {
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY + off[index], ListenerMotion.mc.player.posZ, ListenerMotion.mc.player.onGround));
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ListenerMotion.mc.player.posX + x * (double)index, ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ + z * (double)index, ListenerMotion.mc.player.onGround));
                }
                event.setCancelled(true);
                this.timer.reset();
                break;
            }
        }
    }

    private boolean checkAutoClick() {
        RayTraceResult result;
        if (!((Phase)this.module).autoClick.getValue().booleanValue()) {
            return false;
        }
        if (((Phase)this.module).clickTimer.passed(((Phase)this.module).clickDelay.getValue().intValue())) {
            result = ListenerMotion.mc.objectMouseOver;
            if (((Phase)this.module).smartClick.getValue().booleanValue()) {
                EnumFacing facing = ListenerMotion.mc.player.getHorizontalFacing();
                BlockPos pos = PositionUtil.getPosition().offset(facing);
                if (!ListenerMotion.mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos))) {
                    pos = PositionUtil.getPosition();
                }
                if (ListenerMotion.mc.objectMouseOver != null && pos.equals((Object)ListenerMotion.mc.objectMouseOver.getBlockPos()) || pos.up().equals((Object)ListenerMotion.mc.objectMouseOver.getBlockPos())) {
                    result = ListenerMotion.mc.objectMouseOver;
                } else {
                    BlockPos target = pos.up();
                    if (ListenerMotion.mc.world.getBlockState(target).getMaterial() == Material.AIR) {
                        target = pos;
                    }
                    result = new RayTraceResult(new Vec3d(0.0, 0.5, 0.0), facing.getOpposite(), target);
                }
            }
            if (result == null || result.getBlockPos() == null) {
                return ((Phase)this.module).requireClick.getValue();
            }
        } else {
            return ((Phase)this.module).requireClick.getValue();
        }
        float[] r = RayTraceUtil.hitVecToPlaceVec(result.getBlockPos(), result.hitVec);
        EnumHand hand = ListenerMotion.mc.player.getHeldItemOffhand().getItem() instanceof ItemFood || ListenerMotion.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(result.getBlockPos(), result.sideHit, hand, r[0], r[1], r[2]);
        NetworkUtil.sendPacketNoEvent(packet);
        ((Phase)this.module).pos = result.getBlockPos();
        ((Phase)this.module).clickTimer.reset();
        return false;
    }
}

