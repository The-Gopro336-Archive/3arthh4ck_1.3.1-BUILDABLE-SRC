/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.movement.packetfly;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerBlockPush;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerCPacket;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerMove;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerOverlay;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerPosLook;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerTick;
import me.earth.earthhack.impl.modules.movement.packetfly.ListenerWorldClient;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFlyData;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Phase;
import me.earth.earthhack.impl.modules.movement.packetfly.util.TimeVec;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Type;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class PacketFly
extends Module {
    protected final Setting<Mode> mode = this.register(new EnumSetting<Mode>("Mode", Mode.Factor));
    protected final Setting<Float> factor = this.register(new NumberSetting<Float>("Factor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Phase> phase = this.register(new EnumSetting<Phase>("Phase", Phase.Full));
    protected final Setting<Type> type = this.register(new EnumSetting<Type>("Type", Type.Up));
    protected final Setting<Boolean> antiKick = this.register(new BooleanSetting("AntiKick", true));
    protected final Setting<Boolean> answer = this.register(new BooleanSetting("Answer", false));
    protected final Setting<Boolean> bbOffset = this.register(new BooleanSetting("BB-Offset", false));
    protected final Setting<Integer> invalidY = this.register(new NumberSetting<Integer>("Invalid-Offset", 1337, 0, 1337));
    protected final Setting<Integer> invalids = this.register(new NumberSetting<Integer>("Invalids", 1, 0, 10));
    protected final Setting<Integer> sendTeleport = this.register(new NumberSetting<Integer>("Teleport", 1, 0, 10));
    protected final Setting<Double> concealY = this.register(new NumberSetting<Double>("C-Y", 0.0, -256.0, 256.0));
    protected final Setting<Double> conceal = this.register(new NumberSetting<Double>("C-Multiplier", 1.0, 0.0, 2.0));
    protected final Setting<Double> ySpeed = this.register(new NumberSetting<Double>("Y-Multiplier", 1.0, 0.0, 2.0));
    protected final Setting<Double> xzSpeed = this.register(new NumberSetting<Double>("X/Z-Multiplier", 1.0, 0.0, 2.0));
    protected final Setting<Boolean> positionRotation = this.register(new BooleanSetting("Position-Rotation", false));
    protected final Setting<Boolean> elytra = this.register(new BooleanSetting("Elytra", false));
    protected final Setting<Boolean> xzJitter = this.register(new BooleanSetting("Jitter-XZ", false));
    protected final Setting<Boolean> yJitter = this.register(new BooleanSetting("Jitter-Y", false));
    protected final Setting<Boolean> setPos = this.register(new BooleanSetting("Set-Pos", false));
    protected final Setting<Boolean> zeroSpeed = this.register(new BooleanSetting("Zero-Speed", false));
    protected final Setting<Boolean> zeroY = this.register(new BooleanSetting("Zero-Y", false));
    protected final Setting<Boolean> fixPosition = this.register(new BooleanSetting("FixPosition", true));
    protected final Setting<Boolean> zeroTeleport = this.register(new BooleanSetting("Zero-Teleport", true));
    protected final Setting<Integer> zoomer = this.register(new NumberSetting<Integer>("Zoomies", 3, 0, 10));
    protected final Map<Integer, TimeVec> posLooks = new ConcurrentHashMap<Integer, TimeVec>();
    protected final Set<CPacketPlayer> playerPackets = new HashSet<CPacketPlayer>();
    protected final AtomicInteger teleportID = new AtomicInteger();
    protected Vec3d vecDelServer;
    protected int packetCounter;
    protected boolean zoomies;
    protected float lastFactor;
    protected int ticks;
    protected int zoomTimer = 0;

    public PacketFly() {
        super("PacketFly", Category.Movement);
        this.listeners.add(new ListenerOverlay(this));
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
        this.setData(new PacketFlyData(this));
    }

    @Override
    protected void onEnable() {
        this.clearValues();
        if (PacketFly.mc.player == null) {
            this.disable();
            return;
        }
        if (mc.isSingleplayer()) {
            ModuleUtil.disable(this, "\u00a7cCan't enable PacketFly in SinglePlayer!");
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().toString();
    }

    protected void clearValues() {
        this.lastFactor = 1.0f;
        this.packetCounter = 0;
        this.teleportID.set(0);
        this.playerPackets.clear();
        this.posLooks.clear();
        this.vecDelServer = null;
    }

    protected void onPacketSend(PacketEvent<? extends CPacketPlayer> event) {
        if (!this.playerPackets.remove((Object)event.getPacket())) {
            event.setCancelled(true);
        }
    }

    protected boolean isPlayerCollisionBoundingBoxEmpty() {
        double o = this.bbOffset.getValue() != false ? -0.0625 : 0.0;
        return !PacketFly.mc.world.getCollisionBoxes((Entity)PacketFly.mc.player, PacketFly.mc.player.getEntityBoundingBox().expand(o, o, o)).isEmpty();
    }

    protected boolean checkPackets(int amount) {
        if (++this.packetCounter >= amount) {
            this.packetCounter = 0;
            return true;
        }
        return false;
    }

    protected void sendPackets(double x, double y, double z, boolean confirm) {
        int i;
        Vec3d vec;
        Vec3d offset = new Vec3d(x, y, z);
        this.vecDelServer = vec = PacketFly.mc.player.getPositionVector().add(offset);
        Vec3d oOB = this.type.getValue().createOutOfBounds(vec, this.invalidY.getValue());
        if (this.positionRotation.getValue().booleanValue()) {
            this.sendCPacket(PacketUtil.positionRotation(vec.x, vec.y, vec.z, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch(), PacketFly.mc.player.onGround));
        } else {
            this.sendCPacket(PacketUtil.position(vec.x, vec.y, vec.z));
        }
        double lastX = Managers.POSITION.getX();
        double lastY = Managers.POSITION.getY();
        double lastZ = Managers.POSITION.getZ();
        boolean last = Managers.POSITION.isOnGround();
        for (i = 0; i < this.invalids.getValue(); ++i) {
            this.sendCPacket(PacketUtil.position(oOB.x, oOB.y, oOB.z));
            oOB = this.type.getValue().createOutOfBounds(oOB, this.invalidY.getValue());
        }
        if (this.fixPosition.getValue().booleanValue()) {
            Managers.POSITION.set(lastX, lastY, lastZ);
            Managers.POSITION.setOnGround(last);
        }
        if (confirm && (this.zeroTeleport.getValue().booleanValue() || this.teleportID.get() != 0)) {
            for (i = 0; i < this.sendTeleport.getValue(); ++i) {
                this.sendConfirmTeleport(vec);
            }
        }
        if (this.elytra.getValue().booleanValue()) {
            PacketUtil.sendAction(CPacketEntityAction.Action.START_FALL_FLYING);
        }
    }

    protected void sendConfirmTeleport(Vec3d vec) {
        int id = this.teleportID.incrementAndGet();
        PacketUtil.teleport(id);
        this.posLooks.put(id, new TimeVec(vec));
    }

    protected void sendCPacket(CPacketPlayer packet) {
        this.playerPackets.add(packet);
        PacketFly.mc.player.connection.sendPacket((Packet)packet);
    }
}

