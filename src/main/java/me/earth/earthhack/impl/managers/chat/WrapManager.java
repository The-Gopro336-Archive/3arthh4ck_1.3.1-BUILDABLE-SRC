/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ChatLine
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.managers.chat;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

public class WrapManager
extends SubscriberImpl
implements Globals {
    private final Map<ChatLineReferenceMap, AbstractTextComponent> components = new ConcurrentHashMap<ChatLineReferenceMap, AbstractTextComponent>();

    public WrapManager() {
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class){

            @Override
            public void invoke(TickEvent event) {
                WrapManager.this.onTick();
            }
        });
        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class){

            @Override
            public void invoke(DisconnectEvent event) {
                Globals.mc.addScheduledTask(() -> WrapManager.this.clear());
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                Globals.mc.addScheduledTask(() -> WrapManager.this.clear());
            }
        });
    }

    private void clear() {
        if (WrapManager.mc.ingameGUI != null) {
            for (Map.Entry<ChatLineReferenceMap, AbstractTextComponent> entry : this.components.entrySet()) {
                WrapManager.mc.ingameGUI.getChatGUI().deleteChatLine(entry.getKey().getId());
            }
        }
        this.components.clear();
    }

    private void onTick() {
        for (Map.Entry<ChatLineReferenceMap, AbstractTextComponent> entry : this.components.entrySet()) {
            if (entry.getKey().isEmpty() || !entry.getValue().isWrapping() || WrapManager.mc.ingameGUI == null) {
                this.components.remove(entry.getKey());
                continue;
            }
            ((IGuiNewChat)WrapManager.mc.ingameGUI.getChatGUI()).replace((ITextComponent)entry.getValue(), entry.getKey().getId(), true, false);
        }
    }

    public void registerComponent(AbstractTextComponent component, ChatLine ... references) {
        this.components.put(new ChatLineReferenceMap(references), component);
    }

    private static class ChatLineReferenceMap
    extends WeakHashMap<ChatLine, Boolean> {
        private int id = -1;

        public ChatLineReferenceMap(ChatLine ... references) {
            if (references != null) {
                for (ChatLine line : references) {
                    if (line == null) continue;
                    super.put(line, true);
                    this.id = line.getChatLineID();
                }
            }
        }

        public int getId() {
            return this.id;
        }

        @Override
        public int hashCode() {
            return this.id;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ChatLineReferenceMap) {
                return ((ChatLineReferenceMap)o).id == this.id;
            }
            return false;
        }
    }
}

