package me.earth.earthhack.impl.modules.client.commands;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.gui.GuiChat;

final class KeyboardListener
extends ModuleListener<Commands, KeyboardEvent> {
    public KeyboardListener(Commands module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (((Commands)this.module).prefixBind.getValue().booleanValue() && event.getEventState() && event.getCharacter() == ((Commands)this.module).prefixChar) {
            Scheduler.getInstance().schedule(() -> mc.displayGuiScreen(new GuiChat(Commands.getPrefix())));
        }
    }
}
