package me.earth.earthhack.impl.modules.misc.antiaim;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.antiaim.AntiAim;

final class ListenerMotion
extends ModuleListener<AntiAim, MotionUpdateEvent> {
    private static final Random RANDOM = new Random();
    private int skip;

    public ListenerMotion(AntiAim module) {
        super(module, MotionUpdateEvent.class, 2147482647);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST || ((AntiAim)this.module).dontRotate()) {
            return;
        }
        if (((AntiAim)this.module).sneak.getValue().booleanValue() && !ListenerMotion.mc.gameSettings.keyBindSneak.isKeyDown() && ((AntiAim)this.module).timer.passed(((AntiAim)this.module).sneakDelay.getValue().intValue())) {
            ListenerMotion.mc.player.setSneaking(!ListenerMotion.mc.player.isSneaking());
            ((AntiAim)this.module).timer.reset();
        }
        if (((AntiAim)this.module).skip.getValue() != 1 && this.skip++ % ((AntiAim)this.module).skip.getValue() == 0) {
            event.setYaw(((AntiAim)this.module).lastYaw);
            event.setPitch(((AntiAim)this.module).lastPitch);
            return;
        }
        switch (((AntiAim)this.module).mode.getValue()) {
            case Random: {
                ((AntiAim)this.module).lastYaw = (float)ThreadLocalRandom.current().nextDouble(-180.0, 180.0);
                ((AntiAim)this.module).lastPitch = -90.0f + RANDOM.nextFloat() * 180.0f;
                break;
            }
            case Spin: {
                ((AntiAim)this.module).lastYaw = (((AntiAim)this.module).lastYaw + ((AntiAim)this.module).hSpeed.getValue().floatValue()) % 360.0f;
                ((AntiAim)this.module).lastPitch += ((AntiAim)this.module).vSpeed.getValue().floatValue();
                break;
            }
            case Down: {
                ((AntiAim)this.module).lastYaw = event.getYaw();
                ((AntiAim)this.module).lastPitch = 90.0f;
                break;
            }
            case Headbang: {
                ((AntiAim)this.module).lastYaw = event.getYaw();
                ((AntiAim)this.module).lastPitch += ((AntiAim)this.module).vSpeed.getValue().floatValue();
                break;
            }
            case Horizontal: {
                ((AntiAim)this.module).lastPitch = event.getPitch();
                ((AntiAim)this.module).lastYaw = (((AntiAim)this.module).lastYaw + ((AntiAim)this.module).hSpeed.getValue().floatValue()) % 360.0f;
                break;
            }
            case Constant: {
                event.setYaw(((AntiAim)this.module).yaw.getValue().floatValue());
                event.setPitch(((AntiAim)this.module).pitch.getValue().floatValue());
                return;
            }
        }
        if (((AntiAim)this.module).lastPitch > 90.0f && ((AntiAim)this.module).lastPitch != event.getPitch()) {
            ((AntiAim)this.module).lastPitch = -90.0f;
        }
        event.setYaw(((AntiAim)this.module).lastYaw);
        event.setPitch(((AntiAim)this.module).lastPitch);
    }
}
