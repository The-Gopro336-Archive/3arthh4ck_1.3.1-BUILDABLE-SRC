/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.event.ClickEvent
 */
package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ClickEvent.class})
public abstract class MixinClickEvent
implements IClickEvent {
    private Runnable runnable;

    @Override
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public Runnable getRunnable() {
        return this.runnable;
    }
}

