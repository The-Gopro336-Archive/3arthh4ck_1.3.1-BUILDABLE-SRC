/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ChatLine
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiNewChat
 *  net.minecraft.client.gui.GuiUtilRenderComponents
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentKeybind
 */
package me.earth.earthhack.impl.core.mixins.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.core.ducks.gui.IChatLine;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.core.ducks.util.IHoverable;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.misc.collections.ConvenientStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={GuiNewChat.class})
public abstract class MixinGuiNewChat
implements IGuiNewChat {
    private static final ModuleCache<Chat> CHAT = Caches.getModule(Chat.class);
    private static final ModuleCache<Media> MEDIA = Caches.getModule(Media.class);
    private static final SettingCache<Boolean, BooleanSetting, Chat> TIME_STAMPS = Caches.getSetting(Chat.class, BooleanSetting.class, "TimeStamps", false);
    private static final SettingCache<Boolean, BooleanSetting, Chat> CLEAN = Caches.getSetting(Chat.class, BooleanSetting.class, "Clean", false);
    private static final SettingCache<Boolean, BooleanSetting, Chat> INFINITE = Caches.getSetting(Chat.class, BooleanSetting.class, "Infinite", false);
    private static final ITextComponent INSTEAD = new TextComponentKeybind("");
    private ChatLine currentLine = null;
    private ChatLine currentHover = null;
    private int deleteChatLineID;
    @Shadow
    @Final
    private List<ChatLine> chatLines;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    @Final
    private List<String> sentMessages;
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;
    @Final
    @Shadow
    private Minecraft mc;
    private boolean first = true;

    @Shadow
    protected abstract void setChatLine(ITextComponent var1, int var2, int var3, boolean var4);

    @Shadow
    public abstract void deleteChatLine(int var1);

    @Shadow
    public abstract int getChatWidth();

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Override
    @Accessor(value="scrollPos")
    public abstract int getScrollPos();

    @Override
    @Accessor(value="scrollPos")
    public abstract void setScrollPos(int var1);

    @Override
    @Accessor(value="isScrolled")
    public abstract boolean getScrolled();

    @Override
    @Accessor(value="isScrolled")
    public abstract void setScrolled(boolean var1);

    @Override
    public void invokeSetChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        this.setChatLine(chatComponent, chatLineId, updateCounter, displayOnly);
    }

    @Override
    public void invokeClearChat(boolean sent) {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        if (sent) {
            this.sentMessages.clear();
        }
    }

    @Override
    public boolean replace(ITextComponent component, int id, boolean wrap, boolean returnFirst) {
        boolean set = this.setLine(component, id, this.chatLines, wrap, returnFirst);
        set = this.setLine(component, id, this.drawnChatLines, wrap, returnFirst) || set;
        return set;
    }

    boolean setLine(ITextComponent component, int id, List<ChatLine> list, boolean wrap, boolean returnFirst) {
        ConvenientStack wrapped = null;
        if (wrap) {
            int max = MathHelper.floor((float)((float)this.getChatWidth() / this.getChatScale()));
            wrapped = new ConvenientStack(GuiUtilRenderComponents.splitText((ITextComponent)component, (int)max, (FontRenderer)this.mc.fontRenderer, (boolean)false, (boolean)false));
        }
        int last = 0;
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for (int i2 = 0; i2 < list.size(); ++i2) {
            ChatLine line = list.get(i2);
            if (line.getChatLineID() != id) continue;
            if (wrap) {
                ITextComponent itc = (ITextComponent)((Stack)wrapped).pop();
                if (itc != null) {
                    ((IChatLine)line).setComponent(itc);
                    last = i2 + 1;
                    continue;
                }
                toRemove.add(i2);
                continue;
            }
            ((IChatLine)line).setComponent(component);
            if (!returnFirst) continue;
            return true;
        }
        if (toRemove.isEmpty()) {
            boolean infinite = INFINITE.getValue();
            while (infinite && wrap && !wrapped.empty()) {
                ITextComponent itc = (ITextComponent)((Stack)wrapped).pop();
                if (itc == null) continue;
                ChatLine newLine = new ChatLine(this.mc.ingameGUI.getUpdateCounter(), itc, id);
                ((Chat)MixinGuiNewChat.CHAT.get()).animationMap.put(newLine, new TimeAnimation(((Chat)MixinGuiNewChat.CHAT.get()).time.getValue().intValue(), -Minecraft.getMinecraft().fontRenderer.getStringWidth(newLine.getChatComponent().getFormattedText()), 0.0, false, AnimationMode.LINEAR));
                list.add(last, newLine);
                ++last;
            }
        } else {
            toRemove.forEach(i -> {
                ChatLine cfr_ignored_0 = list.set((int)i, (ChatLine)null);
            });
            list.removeIf(Objects::isNull);
        }
        return false;
    }

    @Inject(method={"drawChat"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/ChatLine;getUpdatedCounter()I")}, locals=LocalCapture.CAPTURE_FAILSOFT)
    public void drawChatHook(int updateCounter, CallbackInfo ci, int i, int j, float f, boolean flag, float f1, int k, int l, int il, ChatLine chatLine) {
        this.currentLine = chatLine;
    }

    @Redirect(method={"drawChat"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawStringWithShadowHook(FontRenderer renderer, String text, float x, float y, int color) {
        TimeAnimation animation = null;
        if (this.currentLine != null) {
            if (((Chat)MixinGuiNewChat.CHAT.get()).animationMap.containsKey((Object)this.currentLine)) {
                animation = ((Chat)MixinGuiNewChat.CHAT.get()).animationMap.get((Object)this.currentLine);
            }
            if (animation != null) {
                animation.add(this.mc.getRenderPartialTicks());
            }
        }
        String s = MEDIA.returnIfPresent(m -> m.convert(text), text);
        if (CHAT.isEnabled() && TIME_STAMPS.getValue().booleanValue() && this.currentLine != null) {
            String t = ((IChatLine)this.currentLine).getTimeStamp() + s;
            return renderer.drawStringWithShadow(t, (float)((double)x + (animation != null && ((Chat)MixinGuiNewChat.CHAT.get()).animated.getValue() != false ? animation.getCurrent() : 0.0)), y, color);
        }
        return renderer.drawStringWithShadow(s, (float)((double)x + (animation != null && ((Chat)MixinGuiNewChat.CHAT.get()).animated.getValue() != false ? animation.getCurrent() : 0.0)), y, color);
    }

    @Inject(method={"getChatOpen"}, at={@At(value="HEAD")}, cancellable=true)
    public void getChatOpenHook(CallbackInfoReturnable<Boolean> info) {
        if (this.mc.currentScreen instanceof CommandGui) {
            info.setReturnValue(true);
        }
    }

    @Inject(method={"clearChatMessages"}, at={@At(value="HEAD")}, cancellable=true)
    public void clearChatMessagesHook(boolean sent, CallbackInfo info) {
        ChatEvent.Clear event = new ChatEvent.Clear(this, sent);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Redirect(method={"drawChat"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal=0))
    private void drawRectHook(int left, int top, int right, int bottom, int c) {
        if (CHAT.isEnabled() && CLEAN.getValue().booleanValue()) {
            return;
        }
        Gui.drawRect((int)left, (int)top, (int)(right + (CHAT.isEnabled() && TIME_STAMPS.getValue() != false ? 40 : 0)), (int)bottom, (int)c);
    }

    @Redirect(method={"setChatLine"}, at=@At(value="INVOKE", target="Ljava/util/List;size()I", ordinal=0, remap=false))
    public int drawnChatLinesSize(List<ChatLine> list) {
        return this.getChatSize(list);
    }

    @Inject(method={"getChatHeight"}, at={@At(value="HEAD")}, cancellable=true)
    private void getChatHeightHook(CallbackInfoReturnable<Integer> info) {
        if (this.mc.currentScreen instanceof CommandGui) {
            info.setReturnValue(500);
        }
    }

    @Redirect(method={"setChatLine"}, at=@At(value="INVOKE", target="Ljava/util/List;size()I", ordinal=2, remap=false))
    public int chatLinesSize(List<ChatLine> list) {
        return this.getChatSize(list);
    }

    @Inject(method={"printChatMessageWithOptionalDeletion"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V", shift=At.Shift.AFTER)}, cancellable=true)
    private void loggerHook(ITextComponent chatComponent, int chatLineId, CallbackInfo ci) {
        ChatEvent.Log event = new ChatEvent.Log(this);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Redirect(method={"printChatMessageWithOptionalDeletion"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V"))
    public void setChatLineHook(GuiNewChat gui, ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        ChatEvent.Send event = new ChatEvent.Send(this, chatComponent, chatLineId, updateCounter, displayOnly);
        Bus.EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            this.setChatLine(event.getChatComponent(), event.getChatLineId(), event.getUpdateCounter(), event.isDisplayOnly());
        }
    }

    @Inject(method={"getChatComponent"}, at={@At(value="HEAD")})
    public void getChatComponentHook(int mouseX, int mouseY, CallbackInfoReturnable<ITextComponent> info) {
        this.first = true;
    }

    @Redirect(method={"getChatComponent"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/ChatLine;getChatComponent()Lnet/minecraft/util/text/ITextComponent;"))
    private ITextComponent getHook(ChatLine chatLine) {
        this.currentHover = chatLine;
        return chatLine.getChatComponent();
    }

    @Redirect(method={"getChatComponent"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiNewChat;getChatWidth()I"))
    private int getChatComponentChatWidthHook(GuiNewChat gnc) {
        return CHAT.isEnabled() && TIME_STAMPS.getValue() != false ? gnc.getChatWidth() + 40 : gnc.getChatWidth();
    }

    @Redirect(method={"getChatComponent"}, at=@At(value="INVOKE", target="Ljava/util/Iterator;next()Ljava/lang/Object;", remap=false))
    private Object getChatComponentInstanceOfHook(Iterator<ITextComponent> iterator) {
        ITextComponent component = iterator.next();
        if (component instanceof IHoverable && !((IHoverable)component).canBeHovered()) {
            return INSTEAD;
        }
        return component;
    }

    @Redirect(method={"getChatComponent"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"))
    public int getStringWidthHook(FontRenderer renderer, String text) {
        String s = MEDIA.returnIfPresent(m -> m.convert(text), text);
        if (CHAT.isEnabled() && TIME_STAMPS.getValue().booleanValue() && this.first && this.currentHover != null) {
            String t = ((IChatLine)this.currentHover).getTimeStamp() + s;
            this.first = false;
            return renderer.getStringWidth(t);
        }
        return renderer.getStringWidth(text);
    }

    @Redirect(method={"setChatLine"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiNewChat;scroll(I)V"))
    public void scrollHook(GuiNewChat gui, int amount) {
    }

    @Inject(method={"setChatLine"}, at={@At(value="HEAD")}, cancellable=true)
    public void setChatLineHookHead(ITextComponent chatComponent, int id, int updateCounter, boolean displayOnly, CallbackInfo info) {
        if (chatComponent instanceof AbstractTextComponent) {
            if (id != 0) {
                this.deleteChatLine(id);
            } else {
                id = -1;
            }
            AbstractTextComponent component = (AbstractTextComponent)chatComponent;
            if (component.isWrapping()) {
                int max = MathHelper.floor((float)((float)this.getChatWidth() / this.getChatScale()));
                List list = GuiUtilRenderComponents.splitText((ITextComponent)component, (int)max, (FontRenderer)this.mc.fontRenderer, (boolean)false, (boolean)false);
                boolean chatOpen = this.getChatOpen();
                ChatLine[] references = new ChatLine[list.size()];
                for (int i = 0; i < list.size(); ++i) {
                    ITextComponent itc = (ITextComponent)list.get(i);
                    if (chatOpen && this.scrollPos > 0) {
                        this.isScrolled = true;
                    }
                    ChatLine line = new ChatLine(updateCounter, itc, id);
                    ((Chat)MixinGuiNewChat.CHAT.get()).animationMap.put(line, new TimeAnimation(((Chat)MixinGuiNewChat.CHAT.get()).time.getValue().intValue(), -Minecraft.getMinecraft().fontRenderer.getStringWidth(line.getChatComponent().getFormattedText()), 0.0, false, AnimationMode.LINEAR));
                    this.drawnChatLines.add(0, line);
                    references[i] = line;
                }
                Managers.WRAP.registerComponent(component, references);
            } else {
                ChatLine newLine = new ChatLine(updateCounter, chatComponent, id);
                ((Chat)MixinGuiNewChat.CHAT.get()).animationMap.put(newLine, new TimeAnimation(((Chat)MixinGuiNewChat.CHAT.get()).time.getValue().intValue(), -Minecraft.getMinecraft().fontRenderer.getStringWidth(newLine.getChatComponent().getFormattedText()), 0.0, false, AnimationMode.LINEAR));
                this.drawnChatLines.add(0, newLine);
            }
            info.cancel();
        }
    }

    private int getChatSize(List<ChatLine> list) {
        return CHAT.isEnabled() && INFINITE.getValue() != false || this.mc.currentScreen instanceof CommandGui ? -2147483647 : list.size();
    }

    @Inject(method={"deleteChatLine"}, at={@At(value="HEAD")})
    private void deleteChatLineHook(int id, CallbackInfo info) {
        this.deleteChatLineID = id;
    }

    @Redirect(method={"deleteChatLine"}, at=@At(value="INVOKE", target="Ljava/util/List;iterator()Ljava/util/Iterator;", remap=false))
    private Iterator<ChatLine> iteratorHook(List<ChatLine> list) {
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    @Redirect(method={"deleteChatLine"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    private int getChatLineIDHook(ChatLine chatLine) {
        if (chatLine == null) {
            return this.deleteChatLineID + 1;
        }
        return chatLine.getChatLineID();
    }

    @Redirect(method={"deleteChatLine"}, at=@At(value="INVOKE", target="Ljava/util/Iterator;hasNext()Z", remap=false))
    private boolean hasNextHook(Iterator<ChatLine> iterator) {
        if (iterator == null) {
            return false;
        }
        return iterator.hasNext();
    }
}

