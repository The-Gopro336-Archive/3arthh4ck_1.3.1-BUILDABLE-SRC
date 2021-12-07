package me.earth.earthhack.impl.modules.misc.mobowner;

import java.util.UUID;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.misc.mobowner.MobOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;

final class ListenerTick
extends ModuleListener<MobOwner, TickEvent> {
    public ListenerTick(MobOwner module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (ListenerTick.mc.world != null) {
            for (Entity entity : ListenerTick.mc.world.getLoadedEntityList()) {
                AbstractHorse horse;
                if (entity == null || entity.getAlwaysRenderNameTag()) continue;
                if (entity instanceof EntityTameable) {
                    EntityTameable tameable = (EntityTameable)((Object)entity);
                    if (!tameable.isTamed()) continue;
                    this.renderNametag(entity, tameable.getOwnerId());
                    continue;
                }
                if (!(entity instanceof AbstractHorse) || !(horse = (AbstractHorse)((Object)entity)).isTame()) continue;
                this.renderNametag(entity, horse.getOwnerUniqueId());
            }
        }
    }

    private void renderNametag(Entity entity, final UUID id) {
        if (id != null) {
            if (((MobOwner)this.module).cache.containsKey(id)) {
                String owner = ((MobOwner)this.module).cache.get(id);
                if (owner != null) {
                    entity.setAlwaysRenderNameTag(true);
                    entity.setCustomNameTag(owner);
                }
            } else {
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.NAME, id){

                    @Override
                    public void onSuccess() {
                        Globals.mc.addScheduledTask(() -> ((MobOwner)((ListenerTick)ListenerTick.this).module).cache.put(id, this.name));
                    }

                    @Override
                    public void onFailure() {
                        Globals.mc.addScheduledTask(() -> ((MobOwner)((ListenerTick)ListenerTick.this).module).cache.put(id, null));
                    }
                });
            }
        }
    }
}
