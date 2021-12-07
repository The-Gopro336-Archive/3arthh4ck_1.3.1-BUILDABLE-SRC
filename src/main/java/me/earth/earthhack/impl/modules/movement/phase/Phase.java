/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ConcurrentSet
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.modules.movement.phase;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.movement.phase.ListenerBlockPush;
import me.earth.earthhack.impl.modules.movement.phase.ListenerCPackets;
import me.earth.earthhack.impl.modules.movement.phase.ListenerCollision;
import me.earth.earthhack.impl.modules.movement.phase.ListenerInput;
import me.earth.earthhack.impl.modules.movement.phase.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.phase.ListenerMove;
import me.earth.earthhack.impl.modules.movement.phase.ListenerRender;
import me.earth.earthhack.impl.modules.movement.phase.ListenerSneak;
import me.earth.earthhack.impl.modules.movement.phase.ListenerSuffocation;
import me.earth.earthhack.impl.modules.movement.phase.ListenerTick;
import me.earth.earthhack.impl.modules.movement.phase.ListenerUpdate;
import me.earth.earthhack.impl.modules.movement.phase.PhaseData;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Phase
extends BlockESPModule
implements CollisionEvent.Listener {
    protected final Setting<PhaseMode> mode = this.register(new EnumSetting<PhaseMode>("Mode", PhaseMode.Sand));
    protected final Setting<Boolean> autoClip = this.register(new BooleanSetting("AutoClip", false));
    protected final Setting<Double> blocks = this.register(new NumberSetting<Double>("Blocks", 0.003, 0.001, 10.0));
    protected final Setting<Double> distance = this.register(new NumberSetting<Double>("Distance", 0.2, 0.0, 10.0));
    protected final Setting<Double> speed = this.register(new NumberSetting<Double>("Speed", 4.0, 0.1, 10.0));
    protected final Setting<Double> constSpeed = this.register(new NumberSetting<Double>("ConstSpeed", 1.0, 0.1, 10.0));
    protected final Setting<Boolean> constStrafe = this.register(new BooleanSetting("ConstStrafe", false));
    protected final Setting<Boolean> constTeleport = this.register(new BooleanSetting("ConstTeleport", false));
    protected final Setting<Boolean> sneakCheck = this.register(new BooleanSetting("SneakCheck", false));
    protected final Setting<Boolean> cancel = this.register(new BooleanSetting("Cancel", false));
    protected final Setting<Boolean> limit = this.register(new BooleanSetting("Limit", true));
    protected final Setting<Integer> skipTime = this.register(new NumberSetting<Integer>("Skip-Time", 150, 0, 1000));
    protected final Setting<Boolean> onlyBlock = this.register(new BooleanSetting("OnlyInBlock", false));
    protected final Setting<Boolean> cancelSneak = this.register(new BooleanSetting("CancelSneak", false));
    protected final Setting<Boolean> autoSneak = this.register(new BooleanSetting("AutoSneak", false));
    protected final Setting<Boolean> autoClick = this.register(new BooleanSetting("AutoClick", false));
    protected final Setting<Integer> clickDelay = this.register(new NumberSetting<Integer>("Click-Delay", 250, 0, 1000));
    protected final Setting<Boolean> requireClick = this.register(new BooleanSetting("RequireClick", false));
    protected final Setting<Boolean> clickBB = this.register(new BooleanSetting("Click-BB", false));
    protected final Setting<Boolean> requireForward = this.register(new BooleanSetting("RequireForward", false));
    protected final Setting<Boolean> forwardBB = this.register(new BooleanSetting("Forward-BB", false));
    protected final Setting<Boolean> smartClick = this.register(new BooleanSetting("SmartClick", false));
    protected final Setting<Boolean> esp = this.register(new BooleanSetting("ESP", false));
    protected final ListenerCollision listenerCollision;
    protected final Set<Packet<?>> packets = new ConcurrentSet();
    protected final StopWatch clickTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected BlockPos pos;
    protected int delay;

    public Phase() {
        super("Phase", Category.Movement);
        this.listenerCollision = new ListenerCollision(this);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSuffocation(this));
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerSneak(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.addAll(new ListenerCPackets(this).getListeners());
        this.unregister(this.color);
        this.unregister(this.outline);
        this.unregister(this.lineWidth);
        this.unregister(this.height);
        this.height.addObserver(e -> {
            e.setValue(Float.valueOf(1.0f));
            e.setCancelled(true);
        });
        this.register(this.color);
        this.register(this.outline);
        this.register(this.lineWidth);
        this.setData(new PhaseData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name();
    }

    @Override
    protected void onEnable() {
        this.delay = 0;
        EntityPlayerSP player = Phase.mc.player;
        if (player != null && this.autoClip.getValue().booleanValue()) {
            double yawCos = Math.cos(Math.toRadians(player.rotationYaw + 90.0f));
            double yawSin = Math.sin(Math.toRadians(player.rotationYaw + 90.0f));
            player.setPosition(player.posX + 1.0 * this.blocks.getValue() * yawCos + 0.0 * this.blocks.getValue() * yawSin, player.posY, player.posZ + (1.0 * this.blocks.getValue() * yawSin - 0.0 * this.blocks.getValue() * yawCos));
        }
    }

    @Override
    protected void onDisable() {
        if (Phase.mc.player != null) {
            Phase.mc.player.noClip = false;
        }
    }

    @Override
    public void onCollision(CollisionEvent event) {
        if (this.isEnabled()) {
            this.listenerCollision.invoke(event);
        }
    }

    public boolean isPhasing() {
        AxisAlignedBB bb = Phase.mc.player.getEntityBoundingBox();
        for (int x = MathHelper.floor((double)bb.minX); x < MathHelper.floor((double)bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor((double)bb.minY); y < MathHelper.floor((double)bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor((double)bb.minZ); z < MathHelper.floor((double)bb.maxZ) + 1; ++z) {
                    if (!Phase.mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || !bb.intersects(new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1)))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    protected void send(Packet<?> packet) {
        this.packets.add(packet);
        Phase.mc.player.connection.sendPacket(packet);
    }

    protected void onPacket(PacketEvent.Send<? extends CPacketPlayer> event) {
        if (this.mode.getValue() == PhaseMode.ConstantiamNew && !MovementUtil.isMoving() && Phase.mc.player.posY == Phase.mc.player.lastTickPosY) {
            event.setCancelled(true);
        }
    }
}

