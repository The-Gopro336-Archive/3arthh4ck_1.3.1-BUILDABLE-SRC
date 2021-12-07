/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BufferBuilder
 */
package me.earth.earthhack.impl.core.mixins.render;

import java.nio.IntBuffer;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BufferBuilder.class})
public abstract class MixinBufferBuilder {
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);

    @Redirect(method={"putColorMultiplier"}, at=@At(value="INVOKE", remap=false, target="java/nio/IntBuffer.put(II)Ljava/nio/IntBuffer;"))
    private IntBuffer putColorMultiplierHook(IntBuffer buffer, int index, int i) {
        if (XRAY.isEnabled() && ((XRay)XRAY.get()).getMode() == XrayMode.Opacity) {
            i = ((XRay)XRAY.get()).getOpacity() << 24 | i & 0xFFFFFF;
        }
        return buffer.put(index, i);
    }
}

