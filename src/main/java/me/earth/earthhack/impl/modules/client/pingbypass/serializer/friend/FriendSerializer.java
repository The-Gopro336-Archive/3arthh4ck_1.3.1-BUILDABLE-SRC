/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketChatMessage
 */
package me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.Serializer;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.ListenerDisconnect;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.ListenerFriends;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.ListenerTick;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;

public class FriendSerializer
extends SubscriberImpl
implements Serializer<PlayerEvent>,
Globals {
    private final Observer<PlayerEvent> observer = new ListenerFriends(this);
    private final Set<PlayerEvent> changed = new LinkedHashSet<PlayerEvent>();
    private boolean cleared;

    public FriendSerializer() {
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerDisconnect(this));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Set<PlayerEvent> set = this.changed;
        synchronized (set) {
            this.changed.clear();
            Managers.FRIENDS.getPlayersWithUUID().forEach((k, v) -> {
                PlayerEvent event = new PlayerEvent(PlayerEventType.ADD, (String)k, (UUID)v);
                this.changed.add(event);
            });
            this.cleared = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void onChange(PlayerEvent event) {
        if (!event.isCancelled()) {
            Set<PlayerEvent> set = this.changed;
            synchronized (set) {
                this.changed.add(event);
            }
        }
    }

    protected void onTick() {
        if (FriendSerializer.mc.player != null && mc.getConnection() != null && !this.changed.isEmpty()) {
            PlayerEvent friend;
            if (this.cleared) {
                mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@ServerFriend clear"));
                this.cleared = false;
            }
            if ((friend = this.pollFriend()) != null) {
                this.serializeAndSend(friend);
            }
        }
    }

    @Override
    public void serializeAndSend(PlayerEvent event) {
        String command = "@ServerFriend";
        command = event.getType() == PlayerEventType.ADD ? command + " add " + event.getName() + " " + event.getUuid() : command + " del " + event.getName();
        Earthhack.getLogger().info(command);
        CPacketChatMessage packet = new CPacketChatMessage(command);
        Objects.requireNonNull(mc.getConnection()).sendPacket((Packet)packet);
    }

    private PlayerEvent pollFriend() {
        if (!this.changed.isEmpty()) {
            PlayerEvent friend = this.changed.iterator().next();
            this.changed.remove(friend);
            return friend;
        }
        return null;
    }

    public Observer<PlayerEvent> getObserver() {
        return this.observer;
    }
}

