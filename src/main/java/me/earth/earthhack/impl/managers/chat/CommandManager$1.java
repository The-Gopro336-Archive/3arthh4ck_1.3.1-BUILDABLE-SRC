package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.network.play.client.CPacketChatMessage;

class CommandManager$1
extends EventListener<PacketEvent.Send<CPacketChatMessage>> {
    CommandManager$1(Class target, Class type) {
        super(target, type);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketChatMessage> event) {
        if (((CPacketChatMessage)event.getPacket()).getMessage().startsWith(Commands.getPrefix())) {
            CommandManager.this.applyCommand(((CPacketChatMessage)event.getPacket()).getMessage());
            if (!((CPacketChatMessage)event.getPacket()).getMessage().toLowerCase().startsWith(Commands.getPrefix() + "last ") && !((CPacketChatMessage)event.getPacket()).getMessage().equalsIgnoreCase(Commands.getPrefix() + "last")) {
                CommandManager.this.lastMessage = ((CPacketChatMessage)event.getPacket()).getMessage();
            }
            event.setCancelled(true);
        }
    }
}
