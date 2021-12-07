/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.TabCompleter
 */
package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.TabCompleter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TabCompleter.class})
public abstract class MixinTabCompleter {
    @Inject(method={"requestCompletions"}, at={@At(value="HEAD")}, cancellable=true)
    private void requestCompletionsHook(String prefix, CallbackInfo ci) {
        if (Minecraft.getMinecraft().player == null) {
            ci.cancel();
        }
    }
}

