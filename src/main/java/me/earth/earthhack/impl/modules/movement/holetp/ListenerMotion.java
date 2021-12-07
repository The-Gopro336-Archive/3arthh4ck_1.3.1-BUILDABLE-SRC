/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.movement.holetp;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.holetp.HoleTP;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion
extends ModuleListener<HoleTP, MotionUpdateEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY = Caches.getModule(PacketFly.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG = Caches.getModule(BlockLag.class);
    private static final ModuleCache<Speed> SPEED = Caches.getModule(Speed.class);
    private static final ModuleCache<LongJump> LONGJUMP = Caches.getModule(LongJump.class);
    private static final SettingCache<SpeedMode, EnumSetting<SpeedMode>, Speed> SPEED_MODE = Caches.getSetting(Speed.class, Setting.class, "Mode", SpeedMode.Instant);

    public ListenerMotion(HoleTP module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            if (PositionUtil.inLiquid(true) || PositionUtil.inLiquid(false) || PACKET_FLY.isEnabled() || BLOCK_LAG.isEnabled() || LONGJUMP.isEnabled() || SPEED.isEnabled() && SPEED_MODE.getValue() != SpeedMode.Instant) {
                return;
            }
            if (!ListenerMotion.mc.player.onGround) {
                if (ListenerMotion.mc.gameSettings.keyBindJump.isKeyDown()) {
                    ((HoleTP)this.module).jumped = true;
                }
            } else {
                ((HoleTP)this.module).jumped = false;
            }
            if (!((HoleTP)this.module).jumped && (double)ListenerMotion.mc.player.fallDistance < 0.5 && ((HoleTP)this.module).isInHole() && ListenerMotion.mc.player.posY - ((HoleTP)this.module).getNearestBlockBelow() > 0.8 && ListenerMotion.mc.player.posY - ((HoleTP)this.module).getNearestBlockBelow() <= 1.125) {
                if (!ListenerMotion.mc.player.onGround) {
                    ++((HoleTP)this.module).packets;
                }
                if (!(ListenerMotion.mc.player.onGround || ListenerMotion.mc.player.isOnLadder() || ListenerMotion.mc.player.isEntityInsideOpaqueBlock() || ListenerMotion.mc.gameSettings.keyBindJump.isKeyDown() || ((HoleTP)this.module).packets <= 0)) {
                    BlockPos pos = new BlockPos(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ);
                    for (double position : HoleTP.OFFSETS) {
                        ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position((double)((float)pos.getX() + 0.5f), ListenerMotion.mc.player.posY - position, (double)((float)pos.getZ() + 0.5f), true));
                    }
                    ListenerMotion.mc.player.setPosition((double)((float)pos.getX() + 0.5f), ((HoleTP)this.module).getNearestBlockBelow() + 0.1, (double)((float)pos.getZ() + 0.5f));
                    ((HoleTP)this.module).packets = 0;
                }
            }
        }
    }
}

