package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.ncptweaks.NCPTweaks;
import net.minecraft.item.ItemFood;

final class ListenerInput
extends ModuleListener<NCPTweaks, MovementInputEvent> {
    public ListenerInput(NCPTweaks module) {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        if (((NCPTweaks)this.module).sneakEat.getValue().booleanValue() && ListenerInput.mc.gameSettings.keyBindUseItem.isKeyDown() && ListenerInput.mc.player.getActiveItemStack().getItem() instanceof ItemFood) {
            event.getInput().sneak = true;
            if (((NCPTweaks)this.module).stopSpeed.getValue().booleanValue()) {
                ((NCPTweaks)this.module).speedStopped = true;
            }
            return;
        }
        ((NCPTweaks)this.module).speedStopped = false;
    }
}
