package me.earth.earthhack.impl.managers.minecraft;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;

public class BlockStateManager
extends SubscriberImpl
implements Globals {
    private final Map<BlockPos, Queue<Consumer<IBlockState>>> callbacks = new ConcurrentHashMap<BlockPos, Queue<Consumer<IBlockState>>>();

    public BlockStateManager() {
        this.listeners.add(new ReceiveListener<SPacketBlockChange>(SPacketBlockChange.class, event -> {
            SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
            this.process(packet.getBlockPosition(), packet.getBlockState());
        }));
        this.listeners.add(new ReceiveListener<SPacketMultiBlockChange>(SPacketMultiBlockChange.class, event -> {
            SPacketMultiBlockChange packet = (SPacketMultiBlockChange)event.getPacket();
            for (SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {
                this.process(data.getPos(), data.getBlockState());
            }
        }));
        this.listeners.add(new ReceiveListener<SPacketExplosion>(SPacketExplosion.class, event -> {
            SPacketExplosion packet = (SPacketExplosion)event.getPacket();
            for (BlockPos pos : packet.getAffectedBlockPositions()) {
                this.process(pos, Blocks.AIR.getDefaultState());
            }
        }));
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                BlockStateManager.this.callbacks.clear();
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Unload>(WorldClientEvent.Unload.class){

            @Override
            public void invoke(WorldClientEvent.Unload event) {
                BlockStateManager.this.callbacks.clear();
            }
        });
    }

    public void addCallback(BlockPos pos, Consumer<IBlockState> callback) {
        this.callbacks.computeIfAbsent(pos.toImmutable(), v -> new ConcurrentLinkedQueue()).add(callback);
    }

    private void process(BlockPos pos, IBlockState state) {
        Queue<Consumer<IBlockState>> cbs = this.callbacks.remove(pos);
        if (cbs != null) {
            CollectionUtil.emptyQueue(cbs, c -> c.accept(state));
        }
    }
}
