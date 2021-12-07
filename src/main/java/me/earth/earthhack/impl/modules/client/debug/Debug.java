/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.client.debug;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.client.DebugUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class Debug
extends Module {
    private final Setting<Boolean> debugPlace = this.register(new BooleanSetting("DebugPlacePing", false));
    private final Setting<Boolean> debugBreak = this.register(new BooleanSetting("DebugBreakPing", false));
    private final Map<BlockPos, Long> times = new ConcurrentHashMap<BlockPos, Long>();
    private final Map<BlockPos, Long> attack = new ConcurrentHashMap<BlockPos, Long>();
    private final Map<Integer, BlockPos> ids = new ConcurrentHashMap<Integer, BlockPos>();

    public Debug() {
        super("Debug", Category.Client);
        SimpleData data = new SimpleData(this, "An empty module for debugging.");
        BooleanSetting s = this.register(new BooleanSetting("SlowUpdates", false));
        data.register(s, "Makes all Chunk Updates happen on a separate Thread. Might increase FPS, but could cause Render lag.");
        this.setData(data);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class){

            @Override
            public void invoke(TickEvent event) {
            }
        });
        this.listeners.add(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class){

            @Override
            public void invoke(MotionUpdateEvent event) {
            }
        });
        this.listeners.add(new EventListener<UpdateEntitiesEvent>(UpdateEntitiesEvent.class){

            @Override
            public void invoke(UpdateEntitiesEvent event) {
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent>(WorldClientEvent.class){

            @Override
            public void invoke(WorldClientEvent event) {
                Debug.this.reset();
            }
        });
        this.listeners.add(new ReceiveListener<SPacketSoundEffect>(SPacketSoundEffect.class, e -> {
            BlockPos pos;
            Long l;
            SPacketSoundEffect p = (SPacketSoundEffect)e.getPacket();
            if (this.debugBreak.getValue().booleanValue() && p.getCategory() == SoundCategory.BLOCKS && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && (l = this.attack.remove((Object)(pos = new BlockPos(p.getX(), p.getY() - 1.0, p.getZ())))) != null) {
                ChatUtil.sendMessageScheduled("Attack took " + (System.currentTimeMillis() - l) + "ms.");
            }
        }));
        this.listeners.add(new PostSendListener<CPacketUseEntity>(CPacketUseEntity.class, e -> {
            if (!this.debugBreak.getValue().booleanValue()) {
                return;
            }
            int entityId = ((ICPacketUseEntity)e.getPacket()).getEntityID();
            Entity entity = Debug.mc.world.getEntityByID(entityId);
            BlockPos pos = entity == null ? this.ids.get(entityId) : entity.getPosition().down();
            if (pos != null) {
                this.attack.put(pos, System.currentTimeMillis());
            }
        }));
        this.listeners.add(new PostSendListener<CPacketPlayerTryUseItemOnBlock>(CPacketPlayerTryUseItemOnBlock.class, e -> {
            if (Debug.mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock)e.getPacket()).getHand()).getItem() == Items.END_CRYSTAL && !this.times.containsKey((Object)((CPacketPlayerTryUseItemOnBlock)e.getPacket()).getPos())) {
                this.times.put(((CPacketPlayerTryUseItemOnBlock)e.getPacket()).getPos(), System.currentTimeMillis());
            }
        }));
        this.listeners.add(new ReceiveListener<SPacketSpawnObject>(SPacketSpawnObject.class, Integer.MAX_VALUE, e -> {
            if (((SPacketSpawnObject)e.getPacket()).getType() == 51) {
                Long l;
                BlockPos pos = new BlockPos(((SPacketSpawnObject)e.getPacket()).getX(), ((SPacketSpawnObject)e.getPacket()).getY() - 1.0, ((SPacketSpawnObject)e.getPacket()).getZ());
                if (this.debugPlace.getValue().booleanValue() && (l = this.times.remove((Object)pos)) != null) {
                    long curr = System.currentTimeMillis();
                    mc.addScheduledTask(() -> DebugUtil.debug(pos, "Crystal took " + (curr - l) + "ms to spawn."));
                }
                if (this.debugBreak.getValue().booleanValue()) {
                    this.ids.put(((SPacketSpawnObject)e.getPacket()).getEntityID(), pos);
                }
            }
        }));
        this.listeners.addAll(new CPacketPlayerListener(){

            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
            }

            @Override
            protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
            }

            @Override
            protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
            }
        }.getListeners());
    }

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    private void reset() {
        this.times.clear();
        this.attack.clear();
        this.ids.clear();
    }
}

