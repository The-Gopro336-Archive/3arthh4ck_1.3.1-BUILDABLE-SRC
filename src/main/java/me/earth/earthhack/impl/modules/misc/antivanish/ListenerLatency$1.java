package me.earth.earthhack.impl.modules.misc.antivanish;

import java.util.UUID;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.misc.antivanish.AntiVanish;
import me.earth.earthhack.impl.modules.misc.antivanish.ListenerLatency;
import me.earth.earthhack.impl.modules.misc.antivanish.util.VanishedEntry;

class ListenerLatency$1
extends LookUp {
    final int val$lookUpId;
    final UUID val$id;

    ListenerLatency$1(LookUp.Type type, UUID uuid, int n, UUID uUID) {
        this.val$lookUpId = n;
        this.val$id = uUID;
        super(type, uuid);
    }

    @Override
    public void onSuccess() {
        ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).futures.remove(this.val$lookUpId);
        ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).cache.put(this.val$id, new VanishedEntry(this.name));
        ListenerLatency.this.sendMessage(this.name);
    }

    @Override
    public void onFailure() {
        ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).futures.remove(this.val$lookUpId);
        ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).cache.put(this.val$id, null);
        ListenerLatency.this.sendUnknown();
    }
}
