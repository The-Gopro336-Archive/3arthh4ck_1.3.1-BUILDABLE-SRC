/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ChunkProviderClient
 */
package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChunkProviderClient.class})
public abstract class MixinChunkProviderClient {
    @Inject(method={"unloadChunk"}, at={@At(value="RETURN")})
    private void onUnloadChunkHook(int x, int z, CallbackInfo ci) {
        Bus.EVENT_BUS.post(new UnloadChunkEvent());
    }
}

