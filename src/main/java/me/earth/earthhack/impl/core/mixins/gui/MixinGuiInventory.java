/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiInventory.class})
public abstract class MixinGuiInventory {
    private static final ModuleCache<XCarry> XCARRY = Caches.getModule(XCarry.class);

    @Inject(method={"onGuiClosed"}, at={@At(value="HEAD")}, cancellable=true)
    private void onGuiClosedHook(CallbackInfo info) {
        if (XCARRY.isEnabled()) {
            Keyboard.enableRepeatEvents((boolean)false);
            info.cancel();
        }
    }
}

