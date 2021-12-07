package me.earth.earthhack.impl.util.network;

import java.util.Set;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.core.ducks.network.INetHandlerPlayClient;
import me.earth.earthhack.impl.core.mixins.network.IEnumConnectionState;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PacketUtil
implements Globals {
    public static Set<Class<? extends Packet<?>>> getAllPackets() {
        return ((IEnumConnectionState)((Object)EnumConnectionState.HANDSHAKING)).getStatesByClass().keySet();
    }

    public static void handlePosLook(SPacketPlayerPosLook packetIn, Entity entity, boolean noRotate) {
        PacketUtil.handlePosLook(packetIn, entity, noRotate, false);
    }

    public static void handlePosLook(SPacketPlayerPosLook packet, Entity entity, boolean noRotate, boolean event) {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
            x += entity.posX;
        } else {
            entity.motionX = 0.0;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
            y += entity.posY;
        } else {
            entity.motionY = 0.0;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
            z += entity.posZ;
        } else {
            entity.motionZ = 0.0;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += entity.rotationPitch;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += entity.rotationYaw;
        }
        entity.setPositionAndRotation(x, y, z, noRotate ? entity.rotationYaw : yaw, noRotate ? entity.rotationPitch : pitch);
        CPacketConfirmTeleport confirm = new CPacketConfirmTeleport(packet.getTeleportId());
        CPacketPlayer posRot = PacketUtil.positionRotation(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ, yaw, pitch, false);
        if (event) {
            NetworkUtil.send(confirm);
            Managers.ROTATION.setBlocking(true);
            NetworkUtil.send(posRot);
            Managers.ROTATION.setBlocking(false);
        } else {
            NetworkUtil.sendPacketNoEvent(confirm);
            NetworkUtil.sendPacketNoEvent(posRot);
        }
        mc.addScheduledTask(PacketUtil::loadTerrain);
    }

    public static void startDigging(BlockPos pos, EnumFacing facing) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
    }

    public static void stopDigging(BlockPos pos, EnumFacing facing) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
    }

    public static void loadTerrain() {
        mc.addScheduledTask(() -> {
            if (!((INetHandlerPlayClient)((Object)PacketUtil.mc.player.connection)).isDoneLoadingTerrain()) {
                PacketUtil.mc.player.prevPosX = PacketUtil.mc.player.posX;
                PacketUtil.mc.player.prevPosY = PacketUtil.mc.player.posY;
                PacketUtil.mc.player.prevPosZ = PacketUtil.mc.player.posZ;
                ((INetHandlerPlayClient)((Object)PacketUtil.mc.player.connection)).setDoneLoadingTerrain(true);
                mc.displayGuiScreen(null);
            }
        });
    }

    public static CPacketUseEntity attackPacket(int id) {
        CPacketUseEntity packet = new CPacketUseEntity();
        ((ICPacketUseEntity)((Object)packet)).setEntityId(id);
        ((ICPacketUseEntity)((Object)packet)).setAction(CPacketUseEntity.Action.ATTACK);
        return packet;
    }

    public static void sneak(boolean sneak) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketEntityAction(PacketUtil.mc.player, sneak ? CPacketEntityAction.Action.START_SNEAKING : CPacketEntityAction.Action.STOP_SNEAKING));
    }

    public static void attack(Entity entity) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        PacketUtil.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
    }

    public static void attack(int id) {
        PacketUtil.mc.player.connection.sendPacket(PacketUtil.attackPacket(id));
        PacketUtil.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
    }

    public static void swing(int slot) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketAnimation(InventoryUtil.getHand(slot)));
    }

    public static void place(BlockPos on, EnumFacing facing, int slot, float x, float y, float z) {
        PacketUtil.place(on, facing, InventoryUtil.getHand(slot), x, y, z);
    }

    public static void place(BlockPos on, EnumFacing facing, EnumHand hand, float x, float y, float z) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(on, facing, hand, x, y, z));
    }

    public static void teleport(int id) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketConfirmTeleport(id));
    }

    public static void sendAction(CPacketEntityAction.Action action) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketEntityAction(PacketUtil.mc.player, action));
    }

    public static void click(int windowIdIn, int slotIdIn, int usedButtonIn, ClickType modeIn, ItemStack clickedItemIn, short actionNumberIn) {
        PacketUtil.mc.player.connection.sendPacket(new CPacketClickWindow(windowIdIn, slotIdIn, usedButtonIn, modeIn, clickedItemIn, actionNumberIn));
    }

    public static CPacketPlayer onGround(boolean onGround) {
        return new CPacketPlayer(onGround);
    }

    public static CPacketPlayer position(double x, double y, double z) {
        return PacketUtil.position(x, y, z, PacketUtil.mc.player.onGround);
    }

    public static CPacketPlayer position(double x, double y, double z, boolean onGround) {
        return new CPacketPlayer.Position(x, y, z, onGround);
    }

    public static CPacketPlayer rotation(float yaw, float pitch, boolean onGround) {
        return new CPacketPlayer.Rotation(yaw, pitch, onGround);
    }

    public static CPacketPlayer positionRotation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
    }

    public static void doOnGround(boolean onGround) {
        PacketUtil.mc.player.connection.sendPacket(PacketUtil.onGround(onGround));
    }

    public static void doY(double y, boolean onGround) {
        PacketUtil.doY(PacketUtil.mc.player, y, onGround);
    }

    public static void doY(Entity entity, double y, boolean onGround) {
        PacketUtil.doPosition(entity.posX, y, entity.posZ, onGround);
    }

    public static void doPosition(double x, double y, double z, boolean onGround) {
        PacketUtil.mc.player.connection.sendPacket(PacketUtil.position(x, y, z, onGround));
    }

    public static void doPositionNoEvent(double x, double y, double z, boolean onGround) {
        NetworkUtil.sendPacketNoEvent(PacketUtil.position(x, y, z, onGround));
    }

    public static void doRotation(float yaw, float pitch, boolean onGround) {
        PacketUtil.mc.player.connection.sendPacket(PacketUtil.rotation(yaw, pitch, onGround));
    }

    public static void doPosRot(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        PacketUtil.mc.player.connection.sendPacket(PacketUtil.positionRotation(x, y, z, yaw, pitch, onGround));
    }

    public static void doPosRotNoEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        NetworkUtil.sendPacketNoEvent(PacketUtil.positionRotation(x, y, z, yaw, pitch, onGround));
    }
}
