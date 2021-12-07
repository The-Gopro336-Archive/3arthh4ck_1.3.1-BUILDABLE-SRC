package me.earth.earthhack.impl.modules.movement.boatfly;

import java.util.HashSet;
import java.util.Set;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketVehicleMove;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerCPackets;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerDismount;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerEntityLook;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerEntityLookMove;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerEntityRelativeMove;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerEntityTeleport;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerGameLoop;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerPlayerPosLook;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerPostVehicleMove;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerServerVehicleMove;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerSteer;
import me.earth.earthhack.impl.modules.movement.boatfly.ListenerVehicleMove;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class BoatFly
extends Module {
    protected final Setting<Boolean> bypass = this.register(new BooleanSetting("Bypass", false));
    protected final Setting<Boolean> postBypass = this.register(new BooleanSetting("PostBypass", false));
    protected final Setting<Integer> ticks = this.register(new NumberSetting<Integer>("Ticks", 2, 0, 20));
    protected final Setting<Integer> packets = this.register(new NumberSetting<Integer>("Packets", 1, 1, 20));
    protected final Setting<Boolean> noVehicleMove = this.register(new BooleanSetting("NoVehicleMove", false));
    protected final Setting<Boolean> noSteer = this.register(new BooleanSetting("NoSteer", false));
    protected final Setting<Boolean> noPosUpdate = this.register(new BooleanSetting("NoPosUpdate", false));
    protected final Setting<Boolean> noForceRotate = this.register(new BooleanSetting("NoForceRotate", false));
    protected final Setting<Boolean> remount = this.register(new BooleanSetting("Remount", false));
    protected final Setting<Boolean> remountPackets = this.register(new BooleanSetting("RemountPackets", false));
    protected final Setting<Boolean> noForceBoatMove = this.register(new BooleanSetting("NoForceBoatMove", false));
    protected final Setting<Boolean> invalid = this.register(new BooleanSetting("Invalid", false));
    protected final Setting<Boolean> boatInvalid = this.register(new BooleanSetting("BoatInvalid", false));
    protected final Setting<Type> invalidMode = this.register(new EnumSetting<Type>("BoatInvalid", Type.Up));
    protected final Setting<Integer> invalidTicks = this.register(new NumberSetting<Integer>("InvalidTicks", 1, 0, 10));
    protected final Setting<Double> upSpeed = this.register(new NumberSetting<Double>("Up-Speed", 2.0, 0.0, 10.0));
    protected final Setting<Double> downSpeed = this.register(new NumberSetting<Double>("Down-Speed", 2.0, 0.0, 10.0));
    protected final Setting<Float> glide = this.register(new NumberSetting<Float>("Glide", Float.valueOf(1.0E-4f), Float.valueOf(0.0f), Float.valueOf(0.2f)));
    protected final Setting<Boolean> fixYaw = this.register(new BooleanSetting("Yaw", false));
    protected final Setting<Bind> downBind = this.register(new BindSetting("Down-Bind"));
    protected final Setting<Boolean> schedule = this.register(new BooleanSetting("Schedule", false));
    protected int tickCount = 0;
    protected int invalidTickCount = 0;
    protected Set<Packet<?>> packetSet = new HashSet();

    public BoatFly() {
        super("BoatFly", Category.Movement);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerDismount(this));
        this.listeners.add(new ListenerPlayerPosLook(this));
        this.listeners.add(new ListenerServerVehicleMove(this));
        this.listeners.add(new ListenerSteer(this));
        this.listeners.add(new ListenerVehicleMove(this));
        this.listeners.add(new ListenerPostVehicleMove(this));
        this.listeners.add(new ListenerEntityLook(this));
        this.listeners.add(new ListenerEntityLookMove(this));
        this.listeners.add(new ListenerEntityRelativeMove(this));
        this.listeners.add(new ListenerEntityTeleport(this));
        this.listeners.addAll(new ListenerCPackets(this).getListeners());
        SimpleData data = new SimpleData(this, "Fly while riding entities.");
        data.register(this.bypass, "Bypasses NCP BoatFly patch.");
        data.register(this.postBypass, "Sends interact packets after vehicle move packets.");
        data.register(this.ticks, "Ticks to wait between sending interact packets.");
        data.register(this.packets, "Number of interact packets to send.");
        data.register(this.noVehicleMove, "Cancels SPacketMoveVehicle, allowing for smoother flight.");
        data.register(this.noSteer, "Cancels CPacketSteerBoat, bypassing some patches.");
        data.register(this.noPosUpdate, "Does not update the player's position along with the boat's (Cancels CPacketPlayer).");
        data.register(this.noForceRotate, "Prevents the server from forcing your rotations.");
        data.register(this.remount, "Automatically remounts the boat after being removed.");
        data.register(this.remountPackets, "Sends extra packets after being dismounted.");
        data.register(this.upSpeed, "Speed to fly upwards with.");
        data.register(this.downSpeed, "Speed to fly downwards with.");
        data.register(this.glide, "Glides down with this speed.");
        data.register(this.fixYaw, "Makes the boat rotate with you.");
        data.register(this.noForceBoatMove, "Prevents the server from forcing your entity to move or rotate.");
        this.setData(data);
    }

    public double getGlideSpeed() {
        return this.glide.getValue().floatValue();
    }

    protected void sendPackets(Entity riding) {
        BoatFly.mc.player.connection.sendPacket(new CPacketUseEntity(riding, EnumHand.MAIN_HAND));
        BoatFly.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (this.invalid.getValue().booleanValue() && this.invalidTickCount++ >= this.invalidTicks.getValue()) {
            Vec3d playerVec = this.invalidMode.getValue().createOutOfBounds(BoatFly.mc.player.getPositionVector(), 1337);
            PacketUtil.doPosition(playerVec.x, playerVec.y, playerVec.z, false);
            if (this.boatInvalid.getValue().booleanValue() && BoatFly.mc.player.getRidingEntity() != null) {
                CPacketVehicleMove packet = new CPacketVehicleMove();
                Vec3d vec = this.invalidMode.getValue().createOutOfBounds(BoatFly.mc.player.getRidingEntity().getPositionVector(), 1337);
                ((ICPacketVehicleMove)((Object)packet)).setY(vec.y);
                ((ICPacketVehicleMove)((Object)packet)).setX(vec.x);
                ((ICPacketVehicleMove)((Object)packet)).setZ(vec.z);
                this.packetSet.add(packet);
                NetworkUtil.sendPacketNoEvent(packet);
            }
            this.invalidTickCount = 0;
        }
    }
}
