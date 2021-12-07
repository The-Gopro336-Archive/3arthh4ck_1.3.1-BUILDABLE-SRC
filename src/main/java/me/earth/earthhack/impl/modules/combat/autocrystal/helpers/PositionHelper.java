package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.world.World;

public class PositionHelper
extends SubscriberImpl
implements Globals {
    private final AutoCrystal module;
    private final Map<Entity, MotionTracker> motionTrackerMap;

    public PositionHelper(AutoCrystal module) {
        this.module = module;
        this.motionTrackerMap = new HashMap<Entity, MotionTracker>();
        this.listeners.add(new EventListener<WorldClientEvent>(WorldClientEvent.class){

            @Override
            public void invoke(WorldClientEvent event) {
                PositionHelper.this.motionTrackerMap.clear();
            }
        });
        this.listeners.add(new ReceiveListener<SPacketSpawnPlayer>(SPacketSpawnPlayer.class, event -> event.addPostEvent(() -> {
            if (PositionHelper.mc.world.getEntityByID(((SPacketSpawnPlayer)event.getPacket()).getEntityID()) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)PositionHelper.mc.world.getEntityByID(((SPacketSpawnPlayer)event.getPacket()).getEntityID());
                this.motionTrackerMap.put(player, new MotionTracker((World)PositionHelper.mc.world, player));
            }
        })));
        this.listeners.add(new EventListener<UpdateEntitiesEvent>(UpdateEntitiesEvent.class){

            @Override
            public void invoke(UpdateEntitiesEvent event) {
                HashMap tempMap = new HashMap(PositionHelper.this.motionTrackerMap);
                for (Map.Entry entry : tempMap.entrySet()) {
                    if (EntityUtil.isDead((Entity)entry.getValue())) {
                        PositionHelper.this.motionTrackerMap.remove(entry.getValue());
                        continue;
                    }
                    ((MotionTracker)entry.getValue()).updateFromTrackedEntity();
                }
            }
        });
    }

    public MotionTracker getTrackerFromEntity(Entity player) {
        return this.motionTrackerMap.get(player);
    }
}
