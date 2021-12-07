/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiNewChat
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package me.earth.earthhack.impl.util.text;

import java.util.Random;
import java.util.function.Consumer;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil
implements Globals {
    private static final Random RND = new Random();

    public static void sendMessage(String message) {
        ChatUtil.sendMessage(message, 0);
    }

    public static void sendMessage(String message, int id) {
        ChatUtil.sendComponent((ITextComponent)new TextComponentString(message == null ? "null" : message), id);
    }

    public static void deleteMessage(int id) {
        ChatUtil.applyIfPresent(g -> g.deleteChatLine(id));
    }

    public static void sendComponent(ITextComponent component) {
        ChatUtil.sendComponent(component, 0);
    }

    public static void sendComponent(ITextComponent c, int id) {
        ChatUtil.applyIfPresent(g -> g.printChatMessageWithOptionalDeletion(c, id));
    }

    public static void applyIfPresent(Consumer<GuiNewChat> consumer) {
        GuiNewChat chat = ChatUtil.getChatGui();
        if (chat != null) {
            consumer.accept(chat);
        }
    }

    public static GuiNewChat getChatGui() {
        if (ChatUtil.mc.ingameGUI != null) {
            return ChatUtil.mc.ingameGUI.getChatGUI();
        }
        return null;
    }

    public static void sendMessageScheduled(String message) {
        mc.addScheduledTask(() -> ChatUtil.sendMessage(message));
    }

    public static String generateRandomHexSuffix(int places) {
        return "[" + Integer.toHexString((RND.nextInt() + 11) * RND.nextInt()).substring(0, places) + "]";
    }
}

