package me.earth.earthhack.impl.modules.movement.reversestep;

import java.util.List;
import java.util.stream.Collectors;
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
import me.earth.earthhack.impl.modules.movement.reversestep.ReverseStep;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.item.EntityEnderPearl;

final class ListenerMotion
extends ModuleListener<ReverseStep, MotionUpdateEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY = Caches.getModule(PacketFly.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG = Caches.getModule(BlockLag.class);
    private static final ModuleCache<Speed> SPEED = Caches.getModule(Speed.class);
    private static final ModuleCache<LongJump> LONGJUMP = Caches.getModule(LongJump.class);
    private static final ModuleCache<HoleTP> HOLETP = Caches.getModule(HoleTP.class);
    private static final SettingCache<SpeedMode, EnumSetting<SpeedMode>, Speed> SPEED_MODE = Caches.getSetting(Speed.class, Setting.class, "Mode", SpeedMode.Instant);
    private boolean Reset = false;

    public ListenerMotion(ReverseStep module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            if (PositionUtil.inLiquid(true) || PositionUtil.inLiquid(false) || PACKET_FLY.isEnabled() || BLOCK_LAG.isEnabled() || LONGJUMP.isEnabled() || ((HoleTP)HOLETP.get()).isInHole() && HOLETP.isEnabled() || SPEED.isEnabled() && SPEED_MODE.getValue() != SpeedMode.Instant) {
                this.Reset = true;
                return;
            }
            List pearls = ListenerMotion.mc.world.loadedEntityList.stream().filter(EntityEnderPearl.class::isInstance).map(EntityEnderPearl.class::cast).collect(Collectors.toList());
            if (!pearls.isEmpty()) {
                ((ReverseStep)this.module).waitForOnGround = true;
            }
            if (!ListenerMotion.mc.player.onGround) {
                if (ListenerMotion.mc.gameSettings.keyBindJump.isKeyDown()) {
                    ((ReverseStep)this.module).jumped = true;
                }
            } else {
                ((ReverseStep)this.module).jumped = false;
                this.Reset = false;
                ((ReverseStep)this.module).waitForOnGround = false;
            }
            if (!((ReverseStep)this.module).jumped && (double)ListenerMotion.mc.player.fallDistance < 0.5 && ListenerMotion.mc.player.posY - ((ReverseStep)this.module).getNearestBlockBelow() > 0.625 && ListenerMotion.mc.player.posY - ((ReverseStep)this.module).getNearestBlockBelow() <= 3.125 && !this.Reset && !((ReverseStep)this.module).waitForOnGround) {
                if (!ListenerMotion.mc.player.onGround) {
                    ++((ReverseStep)this.module).packets;
                }
                if (!(ListenerMotion.mc.player.onGround || !(ListenerMotion.mc.player.motionY < 0.0) || ListenerMotion.mc.player.isOnLadder() || ListenerMotion.mc.player.isEntityInsideOpaqueBlock() || ListenerMotion.mc.gameSettings.keyBindJump.isKeyDown() || ((ReverseStep)this.module).packets <= 0)) {
                    ListenerMotion.mc.player.motionY -= 1.0;
                    ((ReverseStep)this.module).packets = 0;
                }
            }
        }
    }
}
