/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.NettyPacketDecoder
 *  net.minecraft.network.Packet
 *  net.minecraft.network.PacketBuffer
 */
package me.earth.earthhack.impl.core.mixins.network;

import java.io.IOException;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={NettyPacketDecoder.class})
public abstract class MixinNettyPacketDecoder {
    private static final ModuleCache<Packets> PACKETS = Caches.getModule(Packets.class);

    @Redirect(method={"decode"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/Packet;readPacketData(Lnet/minecraft/network/PacketBuffer;)V"))
    private void readPacketDataHook(Packet<?> packet, PacketBuffer buf) throws IOException {
        packet.readPacketData(buf);
        int readable = buf.readableBytes();
        if (readable > 0 && PACKETS.returnIfPresent(Packets::isNoKickActive, false).booleanValue()) {
            ChatUtil.sendMessage("<Packets>\u00a7c (" + packet.getClass().getSimpleName() + ") was larger than expected, found " + readable + " bytes extra whilst reading packet.");
            buf.readerIndex(buf.writerIndex());
        }
    }
}

