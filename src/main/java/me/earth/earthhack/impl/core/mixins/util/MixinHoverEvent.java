/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.event.HoverEvent
 */
package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.IHoverEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={HoverEvent.class})
public abstract class MixinHoverEvent
implements IHoverEvent {
    private boolean offset = true;

    @Override
    public HoverEvent setOffset(boolean offset) {
        this.offset = offset;
        return (HoverEvent)HoverEvent.class.cast(this);
    }

    @Override
    public boolean hasOffset() {
        return this.offset;
    }
}

