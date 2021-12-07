/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpam;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

final class ListenerMove
extends ModuleListener<BowSpam, MoveEvent> {
    private float lastTimer = -1.0f;

    public ListenerMove(BowSpam module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        ItemStack stack = this.getStack();
        if (((BowSpam)this.module).rape.getValue().booleanValue() && ListenerMove.mc.player.onGround && stack != null && !ListenerMove.mc.player.getActiveItemStack().isEmpty() && ListenerMove.mc.player.getItemInUseCount() > 0) {
            event.setX(0.0);
            event.setY(0.0);
            event.setZ(0.0);
            ListenerMove.mc.player.setVelocity(0.0, 0.0, 0.0);
        }
    }

    private ItemStack getStack() {
        ItemStack mainHand = ListenerMove.mc.player.getHeldItemMainhand();
        if (mainHand.getItem() instanceof ItemBow) {
            return mainHand;
        }
        ItemStack offHand = ListenerMove.mc.player.getHeldItemOffhand();
        if (offHand.getItem() instanceof ItemBow) {
            return offHand;
        }
        return null;
    }
}

