package me.earth.earthhack.impl.modules.misc.mobowner;

import java.util.UUID;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.misc.mobowner.ListenerTick;
import me.earth.earthhack.impl.modules.misc.mobowner.MobOwner;

class ListenerTick$1
extends LookUp {
    final UUID val$id;

    ListenerTick$1(LookUp.Type type, UUID uuid, UUID uUID) {
        this.val$id = uUID;
        super(type, uuid);
    }

    @Override
    public void onSuccess() {
        Globals.mc.addScheduledTask(() -> ((MobOwner)((ListenerTick)ListenerTick.this).module).cache.put(this.val$id, this.name));
    }

    @Override
    public void onFailure() {
        Globals.mc.addScheduledTask(() -> ((MobOwner)((ListenerTick)ListenerTick.this).module).cache.put(this.val$id, null));
    }
}
