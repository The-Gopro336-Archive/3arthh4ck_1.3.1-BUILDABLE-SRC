/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.EatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.item.ItemStack;

final class ListenerEat
extends ModuleListener<Announcer, EatEvent> {
    public ListenerEat(Announcer module) {
        super(module, EatEvent.class);
    }

    @Override
    public void invoke(EatEvent event) {
        if (((Announcer)this.module).eat.getValue().booleanValue() && event.getEntity().equals((Object)ListenerEat.mc.player)) {
            ItemStack stack = event.getStack();
            ((Announcer)this.module).addWordAndIncrement(AnnouncementType.Eat, stack.getDisplayName());
        }
    }
}

