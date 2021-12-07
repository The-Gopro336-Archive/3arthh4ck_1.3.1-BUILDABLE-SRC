/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  net.minecraft.network.EnumConnectionState
 *  net.minecraft.network.INetHandler
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.Packet
 *  net.minecraft.network.ThreadQuickExitException
 *  net.minecraft.util.text.ITextComponent
 *  org.apache.logging.log4j.Logger
 */
package me.earth.earthhack.impl.core.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.INetworkManager;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.logger.util.LoggerMode;
import me.earth.earthhack.impl.modules.misc.packetdelay.PacketDelay;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NetworkManager.class})
public abstract class MixinNetworkManager
implements INetworkManager {
    private static final ModuleCache<me.earth.earthhack.impl.modules.misc.logger.Logger> LOGGER_MODULE = Caches.getModule(me.earth.earthhack.impl.modules.misc.logger.Logger.class);
    private static final ModuleCache<PacketDelay> PACKET_DELAY = Caches.getModule(PacketDelay.class);
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private Channel channel;
    @Shadow
    private INetHandler packetListener;

    @Shadow
    public abstract boolean isChannelOpen();

    @Shadow
    protected abstract void flushOutboundQueue();

    @Shadow
    protected abstract void dispatchPacket(Packet<?> var1, GenericFutureListener<? extends Future<? super Void>>[] var2);

    @Shadow
    public abstract void setConnectionState(EnumConnectionState var1);

    @Shadow
    public abstract void sendPacket(Packet<?> var1);

    @Override
    public Packet<?> sendPacketNoEvent(Packet<?> packetIn) {
        return this.sendPacketNoEvent(packetIn, true);
    }

    @Override
    public Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post) {
        if (LOGGER_MODULE.isEnabled() && ((me.earth.earthhack.impl.modules.misc.logger.Logger)LOGGER_MODULE.get()).getMode() == LoggerMode.Normal) {
            ((me.earth.earthhack.impl.modules.misc.logger.Logger)LOGGER_MODULE.get()).logPacket(packet, "Sending (No Event) Post: " + post + ", ", false);
        }
        PacketEvent.NoEvent event = new PacketEvent.NoEvent(packet, post);
        Bus.EVENT_BUS.post(event, packet.getClass());
        if (event.isCancelled()) {
            return packet;
        }
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            if (post) {
                this.dispatchPacket(packet, null);
            } else {
                this.dispatchSilently(packet);
            }
            return packet;
        }
        return null;
    }

    @Inject(method={"sendPacket(Lnet/minecraft/network/Packet;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        if (PACKET_DELAY.isEnabled() && !((PacketDelay)MixinNetworkManager.PACKET_DELAY.get()).packets.contains(packet) && ((PacketDelay)PACKET_DELAY.get()).isPacketValid(MappingProvider.simpleName(packet.getClass()))) {
            info.cancel();
            ((PacketDelay)MixinNetworkManager.PACKET_DELAY.get()).service.schedule(() -> {
                ((PacketDelay)MixinNetworkManager.PACKET_DELAY.get()).packets.add(packet);
                this.sendPacket(packet);
                ((PacketDelay)MixinNetworkManager.PACKET_DELAY.get()).packets.remove(packet);
            }, (long)((PacketDelay)PACKET_DELAY.get()).getDelay(), TimeUnit.MILLISECONDS);
            return;
        }
        PacketEvent.Send event = new PacketEvent.Send(packet);
        Bus.EVENT_BUS.post(event, packet.getClass());
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"dispatchPacket"}, at={@At(value="RETURN")})
    private void onSendPacketPost(Packet<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>>[] futureListeners, CallbackInfo info) {
        PacketEvent.Post event = new PacketEvent.Post(packetIn);
        Bus.EVENT_BUS.post(event, packetIn.getClass());
    }

    @Inject(method={"channelRead0"}, at={@At(value="INVOKE", target="Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V", shift=At.Shift.BEFORE)}, cancellable=true)
    private void onChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        try {
            Bus.EVENT_BUS.post(event, packet.getClass());
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        if (event.isCancelled()) {
            info.cancel();
        } else if (!event.getPostEvents().isEmpty()) {
            try {
                packet.processPacket(this.packetListener);
            }
            catch (ThreadQuickExitException threadQuickExitException) {
                // empty catch block
            }
            for (Runnable runnable : event.getPostEvents()) {
                Scheduler.getInstance().scheduleAsynchronously(runnable);
            }
            info.cancel();
        }
    }

    @Inject(method={"closeChannel"}, at={@At(value="INVOKE", target="Lio/netty/channel/Channel;isOpen()Z", remap=false)})
    private void onDisconnectHook(ITextComponent component, CallbackInfo info) {
        if (this.isChannelOpen()) {
            Bus.EVENT_BUS.post(new DisconnectEvent(component));
        }
    }

    private void dispatchSilently(Packet<?> inPacket) {
        EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
        EnumConnectionState protocolConnectionState = (EnumConnectionState)this.channel.attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();
        if (protocolConnectionState != enumconnectionstate) {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != protocolConnectionState) {
                this.setConnectionState(enumconnectionstate);
            }
            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
            channelfuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                if (enumconnectionstate != protocolConnectionState) {
                    this.setConnectionState(enumconnectionstate);
                }
                ChannelFuture channelfuture1 = this.channel.writeAndFlush((Object)inPacket);
                channelfuture1.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    @Inject(method={"exceptionCaught"}, at={@At(value="RETURN")})
    private void onExceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_, CallbackInfo ci) {
        p_exceptionCaught_2_.printStackTrace();
        System.out.println("----------------------------------------------");
        Thread.dumpStack();
    }
}

