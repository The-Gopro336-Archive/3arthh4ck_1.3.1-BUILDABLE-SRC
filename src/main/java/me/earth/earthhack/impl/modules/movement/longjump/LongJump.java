/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.AxisAlignedBB
 */
package me.earth.earthhack.impl.modules.movement.longjump;

import java.util.List;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.longjump.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.longjump.ListenerMove;
import me.earth.earthhack.impl.modules.movement.longjump.ListenerPosLook;
import me.earth.earthhack.impl.modules.movement.longjump.ListenerTick;
import me.earth.earthhack.impl.modules.movement.longjump.LongJumpData;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class LongJump
extends DisablingModule {
    protected final Setting<JumpMode> mode = this.register(new EnumSetting<JumpMode>("Mode", JumpMode.Normal));
    protected final Setting<Double> boost = this.register(new NumberSetting<Double>("Boost", 4.5, 0.1, 20.0));
    protected final Setting<Boolean> noKick = this.register(new BooleanSetting("AntiKick", true));
    protected final Setting<Bind> invalidBind = this.register(new BindSetting("Invalid", Bind.fromKey(50)));
    protected int stage;
    protected int airTicks;
    protected int groundTicks;
    protected double speed;
    protected double distance;

    public LongJump() {
        super("LongJump", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPosLook(this));
        this.setData(new LongJumpData(this));
    }

    @Override
    protected void onEnable() {
        if (LongJump.mc.player != null) {
            this.distance = MovementUtil.getDistance2D();
            this.speed = MovementUtil.getSpeed();
        }
        this.stage = 0;
        this.airTicks = 0;
        this.groundTicks = 0;
    }

    @Override
    protected void onDisable() {
        Managers.TIMER.reset();
    }

    protected void invalidPacket() {
        this.updatePosition(0.0, 2.147483647E9, 0.0);
    }

    protected void updatePosition(double x, double y, double z) {
        LongJump.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, LongJump.mc.player.onGround));
    }

    protected double getDistance(EntityPlayer player, double distance) {
        List boundingBoxes = player.world.getCollisionBoxes((Entity)player, player.getEntityBoundingBox().offset(0.0, -distance, 0.0));
        if (boundingBoxes.isEmpty()) {
            return 0.0;
        }
        double y = 0.0;
        for (AxisAlignedBB boundingBox : boundingBoxes) {
            if (!(boundingBox.maxY > y)) continue;
            y = boundingBox.maxY;
        }
        return player.posY - y;
    }
}

