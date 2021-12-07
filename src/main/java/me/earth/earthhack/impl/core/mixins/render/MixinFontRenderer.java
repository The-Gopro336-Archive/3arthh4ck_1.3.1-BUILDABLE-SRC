/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.renderer.GlStateManager
 */
package me.earth.earthhack.impl.core.mixins.render;

import java.awt.Color;
import java.util.regex.Pattern;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    private static final SettingCache<Boolean, BooleanSetting, HUD> SHADOW = Caches.getSetting(HUD.class, BooleanSetting.class, "Shadow", false);
    private static final String COLOR_CODES = "0123456789abcdefklmnorzy+-";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("(?i)\u00a7Z[0-9A-F]{8}");
    @Shadow
    private boolean randomStyle;
    @Shadow
    private boolean boldStyle;
    @Shadow
    private boolean italicStyle;
    @Shadow
    private boolean underlineStyle;
    @Shadow
    private boolean strikethroughStyle;
    @Shadow
    private int textColor;
    @Shadow
    protected float posX;
    @Shadow
    protected float posY;
    @Shadow
    private float alpha;
    private int skip;
    private int currentIndex;
    private boolean currentShadow;
    private String currentText;
    private boolean rainbowPlus;
    private boolean rainbowMinus;

    @Shadow
    protected abstract int renderString(String var1, float var2, float var3, int var4, boolean var5);

    @Redirect(method={"drawString(Ljava/lang/String;FFIZ)I"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"))
    public int renderStringHook(FontRenderer fontrenderer, String text, float x, float y, int color, boolean dropShadow) {
        if (dropShadow && SHADOW.getValue().booleanValue()) {
            return this.renderString(text, x - 0.4f, y - 0.4f, color, true);
        }
        return this.renderString(text, x, y, color, dropShadow);
    }

    @Inject(method={"renderStringAtPos"}, at={@At(value="HEAD")})
    public void resetSkip(String text, boolean shadow, CallbackInfo info) {
        this.skip = 0;
        this.currentIndex = 0;
        this.currentText = text;
        this.currentShadow = shadow;
    }

    @Redirect(method={"renderStringAtPos"}, at=@At(value="INVOKE", target="Ljava/lang/String;charAt(I)C", ordinal=0))
    public char charAtHook(String text, int index) {
        this.currentIndex = index;
        return this.getCharAt(text, index);
    }

    @Redirect(method={"renderStringAtPos"}, at=@At(value="INVOKE", target="Ljava/lang/String;charAt(I)C", ordinal=1))
    public char charAtHook1(String text, int index) {
        return this.getCharAt(text, index);
    }

    @Redirect(method={"renderStringAtPos"}, at=@At(value="INVOKE", target="Ljava/lang/String;length()I", ordinal=0))
    public int lengthHook(String string) {
        return string.length() - this.skip;
    }

    @Redirect(method={"renderStringAtPos"}, at=@At(value="INVOKE", target="Ljava/lang/String;length()I", ordinal=1))
    public int lengthHook1(String string) {
        return string.length() - this.skip;
    }

    @Redirect(method={"renderStringAtPos"}, at=@At(value="INVOKE", target="Ljava/lang/String;indexOf(I)I", ordinal=0))
    public int colorCodeHook(String colorCode, int ch) {
        int result = "0123456789abcdefklmnorzy+-".indexOf(String.valueOf(this.currentText.charAt(this.currentIndex + this.skip + 1)).toLowerCase().charAt(0));
        if (result == 22) {
            this.randomStyle = false;
            this.boldStyle = false;
            this.strikethroughStyle = false;
            this.underlineStyle = false;
            this.italicStyle = false;
            this.rainbowPlus = false;
            this.rainbowMinus = false;
            char[] h = new char[8];
            try {
                for (int j = 0; j < 8; ++j) {
                    h[j] = this.currentText.charAt(this.currentIndex + this.skip + j + 2);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return result;
            }
            int colorcode = -1;
            try {
                colorcode = (int)Long.parseLong(new String(h), 16);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.textColor = colorcode;
            GlStateManager.color((float)((float)(colorcode >> 16 & 0xFF) / 255.0f / (float)(this.currentShadow ? 4 : 1)), (float)((float)(colorcode >> 8 & 0xFF) / 255.0f / (float)(this.currentShadow ? 4 : 1)), (float)((float)(colorcode & 0xFF) / 255.0f / (float)(this.currentShadow ? 4 : 1)), (float)((float)(colorcode >> 24 & 0xFF) / 255.0f));
            this.skip += 8;
        } else if (result == 23) {
            this.randomStyle = false;
            this.boldStyle = false;
            this.strikethroughStyle = false;
            this.underlineStyle = false;
            this.italicStyle = false;
            this.rainbowPlus = false;
            this.rainbowMinus = false;
            int rainbow = Color.HSBtoRGB(Managers.COLOR.getHue(), 1.0f, 1.0f);
            GlStateManager.color((float)((float)(rainbow >> 16 & 0xFF) / 255.0f / (float)(this.currentShadow ? 4 : 1)), (float)((float)(rainbow >> 8 & 0xFF) / 255.0f / (float)(this.currentShadow ? 4 : 1)), (float)((float)(rainbow & 0xFF) / 255.0f / (float)(this.currentShadow ? 4 : 1)), (float)((float)(rainbow >> 24 & 0xFF) / 255.0f));
        } else if (result == 24) {
            this.randomStyle = false;
            this.boldStyle = false;
            this.strikethroughStyle = false;
            this.underlineStyle = false;
            this.italicStyle = false;
            this.rainbowPlus = true;
            this.rainbowMinus = false;
        } else if (result == 25) {
            this.randomStyle = false;
            this.boldStyle = false;
            this.strikethroughStyle = false;
            this.underlineStyle = false;
            this.italicStyle = false;
            this.rainbowPlus = false;
            this.rainbowMinus = true;
        } else {
            this.rainbowPlus = false;
            this.rainbowMinus = false;
        }
        return result;
    }

    @Inject(method={"resetStyles"}, at={@At(value="HEAD")})
    public void resetStylesHook(CallbackInfo info) {
        this.rainbowPlus = false;
        this.rainbowMinus = false;
    }

    @Inject(method={"renderStringAtPos"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderChar(CZ)F", shift=At.Shift.BEFORE, ordinal=0)})
    public void renderCharHook(String text, boolean shadow, CallbackInfo info) {
        if (this.rainbowPlus || this.rainbowMinus) {
            int rainbow = Color.HSBtoRGB(Managers.COLOR.getHueByPosition(this.rainbowMinus ? (double)this.posY : (double)this.posX), 1.0f, 1.0f);
            GlStateManager.color((float)((float)(rainbow >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(rainbow >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(rainbow & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)this.alpha);
        }
    }

    @ModifyVariable(method={"getStringWidth"}, at=@At(value="HEAD"), ordinal=0)
    private String setText(String text) {
        return text == null ? null : CUSTOM_PATTERN.matcher(text).replaceAll("\u00a7b");
    }

    private char getCharAt(String text, int index) {
        if (index + this.skip >= text.length()) {
            return text.charAt(text.length() - 1);
        }
        return text.charAt(index + this.skip);
    }
}

