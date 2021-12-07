package me.earth.earthhack.impl.core.mixins.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.gui.IChatLine;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChatLine.class})
public abstract class MixinChatLine
implements IChatLine {
    private static final ModuleCache<Chat> CHAT = Caches.getModule(Chat.class);
    private static final SettingCache<Boolean, BooleanSetting, Chat> TIME_STAMPS = Caches.getSetting(Chat.class, BooleanSetting.class, "TimeStamps", false);
    private static final DateFormat FORMAT = new SimpleDateFormat("k:mm");
    private static final Minecraft MC = Minecraft.getMinecraft();
    private String timeStamp;

    @Override
    public String getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    @Accessor(value="lineString")
    public abstract void setComponent(ITextComponent var1);

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    public void constructorHook(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn, CallbackInfo ci) {
        this.timeStamp = "\u00a7+<" + FORMAT.format(new Date()) + "> " + "\u00a7r";
        String t = lineStringIn.getFormattedText();
        if (CHAT.isEnabled() && TIME_STAMPS.getValue().booleanValue()) {
            t = this.timeStamp + t;
        }
        ((Chat)MixinChatLine.CHAT.get()).animationMap.put((ChatLine)ChatLine.class.cast(this), new TimeAnimation(((Chat)MixinChatLine.CHAT.get()).time.getValue().intValue(), -MixinChatLine.MC.fontRenderer.getStringWidth(t), 0.0, false, AnimationMode.LINEAR));
    }
}
