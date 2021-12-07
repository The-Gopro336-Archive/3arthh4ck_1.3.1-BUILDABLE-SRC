package me.earth.earthhack.impl.managers.thread.connection;

import java.util.Objects;
import java.util.UUID;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;

public class ConnectionManager
extends SubscriberImpl
implements Globals {
    public ConnectionManager() {
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketPlayerListItem>>(PacketEvent.Receive.class, SPacketPlayerListItem.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketPlayerListItem> event) {
                ConnectionManager.this.onEvent(event);
            }
        });
    }

    private void onEvent(PacketEvent.Receive<SPacketPlayerListItem> event) {
        SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
        if (ConnectionManager.mc.world == null || SPacketPlayerListItem.Action.ADD_PLAYER != packet.getAction() && SPacketPlayerListItem.Action.REMOVE_PLAYER != packet.getAction()) {
            return;
        }
        packet.getEntries().stream().filter(Objects::nonNull).filter(data -> data.getProfile().getName() != null && !data.getProfile().getName().isEmpty() || data.getProfile().getId() != null).forEach(data -> {
            switch (packet.getAction()) {
                case ADD_PLAYER: {
                    this.onAdd((SPacketPlayerListItem.AddPlayerData)data);
                    break;
                }
                case REMOVE_PLAYER: {
                    this.onRemove((SPacketPlayerListItem.AddPlayerData)data);
                    break;
                }
            }
        });
    }

    private void onAdd(SPacketPlayerListItem.AddPlayerData data) {
        if (Bus.EVENT_BUS.hasSubscribers(ConnectionEvent.Join.class)) {
            UUID uuid = data.getProfile().getId();
            String packetName = data.getProfile().getName();
            EntityPlayer player = ConnectionManager.mc.world.getPlayerEntityByUUID(uuid);
            if (packetName == null && player == null) {
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.NAME, uuid){

                    @Override
                    public void onSuccess() {
                        Bus.EVENT_BUS.post(new ConnectionEvent.Join(this.name, this.uuid, null));
                    }

                    @Override
                    public void onFailure() {
                    }
                });
                return;
            }
            Bus.EVENT_BUS.post(new ConnectionEvent.Join(packetName, uuid, player));
        }
    }

    private void onRemove(SPacketPlayerListItem.AddPlayerData data) {
        if (Bus.EVENT_BUS.hasSubscribers(ConnectionEvent.Leave.class)) {
            UUID uuid = data.getProfile().getId();
            String packetName = data.getProfile().getName();
            EntityPlayer player = ConnectionManager.mc.world.getPlayerEntityByUUID(uuid);
            if (packetName == null && player == null) {
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.NAME, uuid){

                    @Override
                    public void onSuccess() {
                        Bus.EVENT_BUS.post(new ConnectionEvent.Leave(this.name, this.uuid, null));
                    }

                    @Override
                    public void onFailure() {
                    }
                });
                return;
            }
            Bus.EVENT_BUS.post(new ConnectionEvent.Leave(packetName, uuid, player));
        }
    }
}
