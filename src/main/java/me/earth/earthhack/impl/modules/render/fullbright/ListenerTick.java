package me.earth.earthhack.impl.modules.render.fullbright;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.fullbright.Fullbright;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

final class ListenerTick
extends ModuleListener<Fullbright, TickEvent> {
    public ListenerTick(Fullbright module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe()) {
            switch (((Fullbright)this.module).mode.getValue()) {
                case Gamma: {
                    ListenerTick.mc.gameSettings.gammaSetting = 1000.0f;
                    break;
                }
                case Potion: {
                    ListenerTick.mc.gameSettings.gammaSetting = 1.0f;
                    ListenerTick.mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 1215, 0));
                }
            }
        }
    }
}
