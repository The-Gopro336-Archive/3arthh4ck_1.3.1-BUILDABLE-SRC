package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.legswitch.ConstellationFactory;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

final class ListenerMotion
extends ModuleListener<LegSwitch, MotionUpdateEvent> {
    public ListenerMotion(LegSwitch module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (!((LegSwitch)this.module).isStackValid(ListenerMotion.mc.player.getHeldItemOffhand()) && !((LegSwitch)this.module).isStackValid(ListenerMotion.mc.player.getHeldItemMainhand())) {
            ((LegSwitch)this.module).active = false;
            return;
        }
        if (!(InventoryUtil.isHolding(Items.END_CRYSTAL) || ((LegSwitch)this.module).autoSwitch.getValue() != LegAutoSwitch.None && InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]) != -1)) {
            ((LegSwitch)this.module).active = false;
            return;
        }
        if (event.getStage() == Stage.PRE) {
            if (((LegSwitch)this.module).constellation == null || !((LegSwitch)this.module).constellation.isValid((LegSwitch)this.module, ListenerMotion.mc.player, ListenerMotion.mc.world)) {
                ((LegSwitch)this.module).constellation = ConstellationFactory.create((LegSwitch)this.module, ListenerMotion.mc.world.playerEntities);
                if (((LegSwitch)this.module).constellation != null && !((LegSwitch)this.module).obsidian.getValue().booleanValue() && (((LegSwitch)this.module).constellation.firstNeedsObby || ((LegSwitch)this.module).constellation.secondNeedsObby)) {
                    ((LegSwitch)this.module).constellation = null;
                }
            }
            if (((LegSwitch)this.module).constellation == null) {
                ((LegSwitch)this.module).active = false;
                return;
            }
            ((LegSwitch)this.module).active = true;
            ((LegSwitch)this.module).prepare();
            if (((LegSwitch)this.module).rotations != null) {
                event.setYaw(((LegSwitch)this.module).rotations[0]);
                event.setPitch(((LegSwitch)this.module).rotations[1]);
            }
        } else {
            ((LegSwitch)this.module).execute();
        }
    }
}
