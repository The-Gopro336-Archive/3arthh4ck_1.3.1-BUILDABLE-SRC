package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

final class ListenerKeyboard
extends ModuleListener<Offhand, KeyboardEvent> {
    public ListenerKeyboard(Offhand module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (event.getEventState()) {
            if (event.getKey() == ((Offhand)this.module).gappleBind.getValue().getKey()) {
                if (((Offhand)this.module).cToTotem.getValue().booleanValue() && (!((Offhand)this.module).crystalsIfNoTotem.getValue().booleanValue() || InventoryUtil.getCount(Items.TOTEM_OF_UNDYING) != 0 || !((Offhand)this.module).setSlotTimer.passed(250L)) && OffhandMode.CRYSTAL.equals(((Offhand)this.module).getMode())) {
                    ((Offhand)this.module).setMode(OffhandMode.TOTEM);
                } else {
                    ((Offhand)this.module).setMode(((Offhand)this.module).getMode() == OffhandMode.GAPPLE ? OffhandMode.TOTEM : OffhandMode.GAPPLE);
                }
            } else if (event.getKey() == ((Offhand)this.module).crystalBind.getValue().getKey()) {
                ((Offhand)this.module).setMode(OffhandMode.CRYSTAL.equals(((Offhand)this.module).getMode()) ? OffhandMode.TOTEM : OffhandMode.CRYSTAL);
            }
        }
    }
}
