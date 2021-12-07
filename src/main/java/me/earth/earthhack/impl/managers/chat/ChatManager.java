/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.managers.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.util.misc.SkippingCounter;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.text.ITextComponent;

public class ChatManager
extends SubscriberImpl
implements Globals {
    private final Map<Integer, Map<String, Integer>> message_ids;
    private final SkippingCounter counter = new SkippingCounter(1337, i -> i != -1);

    public ChatManager() {
        this.message_ids = new ConcurrentHashMap<Integer, Map<String, Integer>>();
        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class){

            @Override
            public void invoke(DisconnectEvent event) {
                Globals.mc.addScheduledTask(() -> ChatManager.this.clear());
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                Globals.mc.addScheduledTask(() -> ChatManager.this.clear());
            }
        });
    }

    public void clear() {
        if (ChatManager.mc.ingameGUI != null) {
            this.message_ids.values().forEach(m -> m.values().forEach(id -> ChatManager.mc.ingameGUI.getChatGUI().deleteChatLine(id.intValue())));
        }
        this.message_ids.clear();
        this.counter.reset();
    }

    public void sendDeleteMessageScheduled(String message, String uniqueWord, int senderID) {
        Integer id = this.message_ids.computeIfAbsent(senderID, v -> new ConcurrentHashMap()).computeIfAbsent(uniqueWord, v -> this.counter.next());
        mc.addScheduledTask(() -> ChatUtil.sendMessage(message, id));
    }

    public void sendDeleteMessage(String message, String uniqueWord, int senderID) {
        Integer id = this.message_ids.computeIfAbsent(senderID, v -> new ConcurrentHashMap()).computeIfAbsent(uniqueWord, v -> this.counter.next());
        ChatUtil.sendMessage(message, id);
    }

    public void deleteMessage(String uniqueWord, int senderID) {
        Integer id;
        Map<String, Integer> map = this.message_ids.get(senderID);
        if (map != null && (id = map.remove(uniqueWord)) != null) {
            ChatUtil.deleteMessage(id);
        }
    }

    public void sendDeleteComponent(ITextComponent component, String uniqueWord, int senderID) {
        Integer id = this.message_ids.computeIfAbsent(senderID, v -> new ConcurrentHashMap()).computeIfAbsent(uniqueWord, v -> this.counter.next());
        ChatUtil.sendComponent(component, id);
    }

    public int getId(String uniqueWord, int senderID) {
        Integer id;
        Map<String, Integer> map = this.message_ids.get(senderID);
        if (map != null && (id = map.get(uniqueWord)) != null) {
            return id;
        }
        return -1;
    }

    public void replace(ITextComponent component, String uniqueWord, int senderID, boolean wrap, boolean multiple, boolean sendIfAbsent) {
        IGuiNewChat gui;
        Integer id;
        Map<String, Integer> map = this.message_ids.get(senderID);
        if (map != null && (id = map.get(uniqueWord)) != null && ChatManager.mc.ingameGUI != null && (gui = (IGuiNewChat)ChatManager.mc.ingameGUI.getChatGUI()).replace(component, senderID, wrap, !multiple)) {
            return;
        }
        if (sendIfAbsent) {
            this.sendDeleteComponent(component, uniqueWord, senderID);
        }
    }
}

