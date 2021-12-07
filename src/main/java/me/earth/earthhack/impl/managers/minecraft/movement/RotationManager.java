package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.PositionManager;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

public class RotationManager
extends SubscriberImpl
implements Globals {
    private final PositionManager positionManager;
    private boolean blocking;
    private volatile float last_yaw;
    private volatile float last_pitch;
    private float renderYaw;
    private float renderPitch;
    private float renderYawOffset;
    private float prevYaw;
    private float prevPitch;
    private float prevRenderYawOffset;
    private float prevRotationYawHead;
    private float rotationYawHead;
    private int ticksExisted;

    public RotationManager() {
        this(Managers.POSITION);
    }

    public RotationManager(PositionManager positionManager) {
        this.positionManager = positionManager;
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketPlayerPosLook>>(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketPlayerPosLook.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
                float yaw = packet.getYaw();
                float pitch = packet.getPitch();
                if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
                    yaw += Globals.mc.player.rotationYaw;
                }
                if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
                    pitch += Globals.mc.player.rotationPitch;
                }
                if (Globals.mc.player != null) {
                    RotationManager.this.setServerRotations(yaw, pitch);
                }
            }
        });
        this.listeners.add(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class, Integer.MIN_VALUE){

            @Override
            public void invoke(MotionUpdateEvent event) {
                if (event.getStage() == Stage.PRE) {
                    RotationManager.this.set(event.getYaw(), event.getPitch());
                }
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer>>(PacketEvent.Post.class, CPacketPlayer.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer> event) {
                RotationManager.this.readCPacket((CPacketPlayer)event.getPacket());
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.Position>>(PacketEvent.Post.class, CPacketPlayer.Position.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Position> event) {
                RotationManager.this.readCPacket((CPacketPlayer)event.getPacket());
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.Rotation>>(PacketEvent.Post.class, CPacketPlayer.Rotation.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Rotation> event) {
                RotationManager.this.readCPacket((CPacketPlayer)event.getPacket());
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.PositionRotation>>(PacketEvent.Post.class, CPacketPlayer.PositionRotation.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.PositionRotation> event) {
                RotationManager.this.readCPacket((CPacketPlayer)event.getPacket());
            }
        });
    }

    public float getServerYaw() {
        return this.last_yaw;
    }

    public float getServerPitch() {
        return this.last_pitch;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    public void setServerRotations(float yaw, float pitch) {
        this.last_yaw = yaw;
        this.last_pitch = pitch;
    }

    public void readCPacket(CPacketPlayer packetIn) {
        ((IEntityPlayerSP)((Object)RotationManager.mc.player)).setLastReportedYaw(packetIn.getYaw(((IEntityPlayerSP)((Object)RotationManager.mc.player)).getLastReportedYaw()));
        ((IEntityPlayerSP)((Object)RotationManager.mc.player)).setLastReportedPitch(packetIn.getPitch(((IEntityPlayerSP)((Object)RotationManager.mc.player)).getLastReportedPitch()));
        this.setServerRotations(packetIn.getYaw(this.last_yaw), packetIn.getPitch(this.last_pitch));
        this.positionManager.setOnGround(packetIn.isOnGround());
    }

    private void set(float yaw, float pitch) {
        if (RotationManager.mc.player.ticksExisted == this.ticksExisted) {
            return;
        }
        this.ticksExisted = RotationManager.mc.player.ticksExisted;
        this.prevYaw = this.renderYaw;
        this.prevPitch = this.renderPitch;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.renderYawOffset = this.getRenderYawOffset(yaw, this.prevRenderYawOffset);
        this.prevRotationYawHead = this.rotationYawHead;
        this.rotationYawHead = yaw;
        this.renderYaw = yaw;
        this.renderPitch = pitch;
    }

    public float getRenderYaw() {
        return this.renderYaw;
    }

    public float getRenderPitch() {
        return this.renderPitch;
    }

    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    public float getRenderYawOffset() {
        return this.renderYawOffset;
    }

    public float getPrevYaw() {
        return this.prevYaw;
    }

    public float getPrevPitch() {
        return this.prevPitch;
    }

    public float getPrevRotationYawHead() {
        return this.prevRotationYawHead;
    }

    public float getPrevRenderYawOffset() {
        return this.prevRenderYawOffset;
    }

    private float getRenderYawOffset(float yaw, float offsetIn) {
        float offset;
        float result = offsetIn;
        double xDif = RotationManager.mc.player.posX - RotationManager.mc.player.prevPosX;
        double zDif = RotationManager.mc.player.posZ - RotationManager.mc.player.prevPosZ;
        if (xDif * xDif + zDif * zDif > 0.002500000176951289) {
            offset = (float)MathHelper.atan2((double)zDif, (double)xDif) * 57.295776f - 90.0f;
            float wrap = MathHelper.abs((float)(MathHelper.wrapDegrees((float)yaw) - offset));
            result = 95.0f < wrap && wrap < 265.0f ? offset - 180.0f : offset;
        }
        if (RotationManager.mc.player.swingProgress > 0.0f) {
            result = yaw;
        }
        if ((offset = MathHelper.wrapDegrees((float)(yaw - (result = offsetIn + MathHelper.wrapDegrees((float)(result - offsetIn)) * 0.3f)))) < -75.0f) {
            offset = -75.0f;
        } else if (offset >= 75.0f) {
            offset = 75.0f;
        }
        result = yaw - offset;
        if (offset * offset > 2500.0f) {
            result += offset * 0.2f;
        }
        return result;
    }
}
