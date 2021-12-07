package me.earth.earthhack.impl.modules.misc.antivanish;

import java.util.UUID;
import java.util.concurrent.Future;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.misc.antivanish.AntiVanish;
import me.earth.earthhack.impl.modules.misc.antivanish.util.VanishedEntry;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.play.server.SPacketPlayerListItem;

final class ListenerLatency
extends ModuleListener<AntiVanish, PacketEvent.Receive<SPacketPlayerListItem>> {
    public ListenerLatency(AntiVanish module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerListItem.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerListItem> event) {
        SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
        if (packet.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
            for (SPacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                final UUID id = data.getProfile().getId();
                if (mc.getConnection().getPlayerInfo(id) != null) continue;
                if (!((AntiVanish)this.module).timer.passed(1000L)) {
                    return;
                }
                String name = data.getProfile().getName();
                if (name == null && ((AntiVanish)this.module).cache.containsKey(id)) {
                    VanishedEntry lookUp = ((AntiVanish)this.module).cache.get(id);
                    if (lookUp == null) {
                        this.sendUnknown();
                        return;
                    }
                    if (System.currentTimeMillis() - lookUp.getTime() < 5000L) {
                        return;
                    }
                    name = lookUp.getName();
                    if (name == null) {
                        this.sendUnknown();
                        return;
                    }
                }
                if (name == null) {
                    final int lookUpId = ((AntiVanish)this.module).ids.incrementAndGet();
                    Future<?> future = Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.NAME, id){

                        @Override
                        public void onSuccess() {
                            ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).futures.remove(lookUpId);
                            ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).cache.put(id, new VanishedEntry(this.name));
                            ListenerLatency.this.sendMessage(this.name);
                        }

                        @Override
                        public void onFailure() {
                            ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).futures.remove(lookUpId);
                            ((AntiVanish)((ListenerLatency)ListenerLatency.this).module).cache.put(id, null);
                            ListenerLatency.this.sendUnknown();
                        }
                    });
                    if (future == null) continue;
                    ((AntiVanish)this.module).futures.put(lookUpId, future);
                    continue;
                }
                this.sendMessage(name);
            }
        }
    }

    private void sendUnknown() {
        ((AntiVanish)this.module).timer.reset();
        mc.addScheduledTask(() -> ChatUtil.sendMessage("<" + ((AntiVanish)this.module).getDisplayName() + "> " + "\u00a7c" + "Someone just vanished."));
    }

    private void sendMessage(String name) {
        ((AntiVanish)this.module).timer.reset();
        mc.addScheduledTask(() -> Managers.CHAT.sendDeleteMessage("<" + ((AntiVanish)this.module).getDisplayName() + "> " + "\u00a7c" + name + " vanished.", name, 7000));
    }
}
