/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.settings.KeyBinding
 */
package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.IKeyBinding;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={KeyBinding.class})
public abstract class MixinKeyBinding
implements IKeyBinding {
    @Shadow
    private boolean pressed;

    @Override
    @Accessor(value="pressed")
    public abstract void setPressed(boolean var1);

    @Inject(method={"isKeyDown"}, at={@At(value="RETURN")}, cancellable=true)
    private void isKeyDownHook(CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue().booleanValue()) {
            info.setReturnValue(this.pressed);
        }
    }
}

