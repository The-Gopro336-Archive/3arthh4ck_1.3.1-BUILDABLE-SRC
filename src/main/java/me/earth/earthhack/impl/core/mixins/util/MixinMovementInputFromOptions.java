/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.MovementInputFromOptions
 */
package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={MovementInputFromOptions.class})
public abstract class MixinMovementInputFromOptions {
    @Redirect(method={"updatePlayerMoveState"}, at=@At(value="FIELD", target="Lnet/minecraft/util/MovementInputFromOptions;sneak:Z", ordinal=1))
    private boolean sneakHook(MovementInputFromOptions input) {
        boolean sneak = input.sneak;
        MovementInputEvent event = new MovementInputEvent((MovementInput)input);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            return false;
        }
        return sneak;
    }
}

