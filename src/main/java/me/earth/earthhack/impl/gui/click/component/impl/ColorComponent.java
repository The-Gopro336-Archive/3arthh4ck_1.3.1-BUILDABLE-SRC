/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class ColorComponent
extends Component {
    private final ColorSetting colorSetting;
    private boolean colorExtended;
    private boolean colorSelectorDragging;
    private boolean alphaSelectorDragging;
    private boolean hueSelectorDragging;
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;
    private boolean slidingSpeed;
    private boolean slidingSaturation;
    private boolean slidingBrightness;

    public ColorComponent(ColorSetting colorSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(colorSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.colorSetting = colorSetting;
        float[] hsb = Color.RGBtoHSB(this.getColorSetting().getRed(), this.getColorSetting().getGreen(), this.getColorSetting().getBlue(), null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = (float)this.getColorSetting().getAlpha() / 255.0f;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float[] hsb;
        super.drawScreen(mouseX, mouseY, partialTicks);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.getLabel(), this.getFinishedX() + 5.0f, this.getFinishedY() + 7.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), -1);
        Render2DUtil.drawBorderedRect(this.getFinishedX() + this.getWidth() - 20.0f, this.getFinishedY() + 4.0f, this.getFinishedX() + this.getWidth() - 5.0f, this.getFinishedY() + 11.0f, 0.5f, this.getColorSetting().getRGB(), -16777216);
        this.setHeight(this.isColorExtended() ? (float)((this.getColorSetting() == Managers.COLOR.getColorSetting() ? 134 : 154) + (this.getColorSetting().isRainbow() ? 56 : 0)) : 14.0f);
        if (this.isColorExtended()) {
            float expandedX = this.getFinishedX() + 1.0f;
            float expandedY = this.getFinishedY() + 14.0f;
            float colorPickerLeft = expandedX + 6.0f;
            float colorPickerTop = expandedY + 1.0f;
            float colorPickerRight = colorPickerLeft + (this.getWidth() - 20.0f);
            float colorPickerBottom = colorPickerTop + (this.getHeight() - (float)((this.getColorSetting() == Managers.COLOR.getColorSetting() ? 52 : 68) + (this.getColorSetting().isRainbow() ? 56 : 0)));
            int selectorWhiteOverlayColor = new Color(255, 255, 255, 180).getRGB();
            int colorMouseX = (int)MathUtil.clamp((float)mouseX, colorPickerLeft, colorPickerRight);
            int colorMouseY = (int)MathUtil.clamp((float)mouseY, colorPickerTop, colorPickerBottom);
            Render2DUtil.drawRect(colorPickerLeft - 0.5f, colorPickerTop - 0.5f, colorPickerRight + 0.5f, colorPickerBottom + 0.5f, -16777216);
            this.drawColorPickerRect(colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);
            float colorSelectorX = this.saturation * (colorPickerRight - colorPickerLeft);
            float colorSelectorY = (1.0f - this.brightness) * (colorPickerBottom - colorPickerTop);
            if (this.colorSelectorDragging) {
                float wWidth = colorPickerRight - colorPickerLeft;
                float xDif = (float)colorMouseX - colorPickerLeft;
                this.saturation = xDif / wWidth;
                colorSelectorX = xDif;
                float hHeight = colorPickerBottom - colorPickerTop;
                float yDif = (float)colorMouseY - colorPickerTop;
                this.brightness = 1.0f - yDif / hHeight;
                colorSelectorY = yDif;
                this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
            }
            float csLeft = colorPickerLeft + colorSelectorX - 0.5f;
            float csTop = colorPickerTop + colorSelectorY - 0.5f;
            float csRight = colorPickerLeft + colorSelectorX + 0.5f;
            float csBottom = colorPickerTop + colorSelectorY + 0.5f;
            Render2DUtil.drawRect(csLeft - 1.0f, csTop - 1.0f, csLeft, csBottom + 1.0f, -16777216);
            Render2DUtil.drawRect(csRight, csTop - 1.0f, csRight + 1.0f, csBottom + 1.0f, -16777216);
            Render2DUtil.drawRect(csLeft, csTop - 1.0f, csRight, csTop, -16777216);
            Render2DUtil.drawRect(csLeft, csBottom, csRight, csBottom + 1.0f, -16777216);
            Render2DUtil.drawRect(csLeft, csTop, csRight, csBottom, selectorWhiteOverlayColor);
            float hueSliderLeft = colorPickerRight + 2.0f;
            float hueSliderRight = hueSliderLeft + 4.0f;
            int hueMouseY = (int)MathUtil.clamp((float)mouseY, colorPickerTop, colorPickerBottom);
            float hueSliderYDif = colorPickerBottom - colorPickerTop;
            float hueSelectorY = (1.0f - this.hue) * hueSliderYDif;
            if (this.hueSelectorDragging) {
                float yDif = (float)hueMouseY - colorPickerTop;
                this.hue = 1.0f - yDif / hueSliderYDif;
                hueSelectorY = yDif;
                this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
            }
            Render2DUtil.drawRect(hueSliderLeft - 0.5f, colorPickerTop - 0.5f, hueSliderRight + 0.5f, colorPickerBottom + 0.5f, -16777216);
            float inc = 0.2f;
            float times = 5.0f;
            float sHeight = colorPickerBottom - colorPickerTop;
            float size = sHeight / 5.0f;
            float sY = colorPickerTop;
            int i = 0;
            while ((float)i < 5.0f) {
                boolean last = (float)i == 4.0f;
                Render2DUtil.drawGradientRect(hueSliderLeft, sY, hueSliderRight, sY + size, false, Color.HSBtoRGB(1.0f - 0.2f * (float)i, 1.0f, 1.0f), Color.HSBtoRGB(1.0f - 0.2f * (float)(i + 1), 1.0f, 1.0f));
                if (!last) {
                    sY += size;
                }
                ++i;
            }
            float hsTop = colorPickerTop + hueSelectorY - 0.5f;
            float hsBottom = colorPickerTop + hueSelectorY + 0.5f;
            Render2DUtil.drawRect(hueSliderLeft - 1.0f, hsTop - 1.0f, hueSliderLeft, hsBottom + 1.0f, -16777216);
            Render2DUtil.drawRect(hueSliderRight, hsTop - 1.0f, hueSliderRight + 1.0f, hsBottom + 1.0f, -16777216);
            Render2DUtil.drawRect(hueSliderLeft, hsTop - 1.0f, hueSliderRight, hsTop, -16777216);
            Render2DUtil.drawRect(hueSliderLeft, hsBottom, hueSliderRight, hsBottom + 1.0f, -16777216);
            Render2DUtil.drawRect(hueSliderLeft, hsTop, hueSliderRight, hsBottom, selectorWhiteOverlayColor);
            float alphaSliderTop = colorPickerBottom + 2.0f;
            float alphaSliderBottom = alphaSliderTop + 4.0f;
            int color = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            float hsHeight = colorPickerRight - colorPickerLeft;
            float alphaSelectorX = this.alpha * hsHeight;
            if (this.alphaSelectorDragging) {
                float xDif = (float)colorMouseX - colorPickerLeft;
                this.alpha = xDif / hsHeight;
                alphaSelectorX = xDif;
                this.updateColor(new Color(r, g, b, (int)(this.alpha * 255.0f)).getRGB());
            }
            Render2DUtil.drawRect(colorPickerLeft - 0.5f, alphaSliderTop - 0.5f, colorPickerRight + 0.5f, alphaSliderBottom + 0.5f, -16777216);
            Render2DUtil.drawCheckeredBackground(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);
            Render2DUtil.drawGradientRect(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom, true, new Color(r, g, b, 0).getRGB(), new Color(r, g, b, 255).getRGB());
            float asLeft = colorPickerLeft + alphaSelectorX - 0.5f;
            float asRight = colorPickerLeft + alphaSelectorX + 0.5f;
            Render2DUtil.drawRect(asLeft - 1.0f, alphaSliderTop, asRight + 1.0f, alphaSliderBottom, -16777216);
            Render2DUtil.drawRect(asLeft, alphaSliderTop, asRight, alphaSliderBottom, selectorWhiteOverlayColor);
            Render2DUtil.drawGradientRect(colorPickerLeft, alphaSliderBottom + 2.0f, colorPickerLeft + (this.getWidth() - 16.0f) / 2.0f, alphaSliderBottom + 14.0f, false, ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB(), ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().darker().darker().getRGB());
            Render2DUtil.drawBorderedRect(colorPickerLeft, alphaSliderBottom + 2.0f, colorPickerLeft + (this.getWidth() - 16.0f) / 2.0f, alphaSliderBottom + 14.0f, 0.5f, 0, -16777216);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Copy", colorPickerLeft + (this.getWidth() - 16.0f) / 2.0f / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.getStringWidth("Copy") >> 1), alphaSliderBottom + 8.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), -1);
            Render2DUtil.drawGradientRect(hueSliderRight - (this.getWidth() - 16.0f) / 2.0f, alphaSliderBottom + 2.0f, hueSliderRight, alphaSliderBottom + 14.0f, false, ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB(), ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().darker().darker().getRGB());
            Render2DUtil.drawBorderedRect(hueSliderRight - (this.getWidth() - 16.0f) / 2.0f, alphaSliderBottom + 2.0f, hueSliderRight, alphaSliderBottom + 14.0f, 0.5f, 0, -16777216);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Paste", hueSliderRight - (this.getWidth() - 16.0f) / 4.0f - (float)(Minecraft.getMinecraft().fontRenderer.getStringWidth("Paste") >> 1), alphaSliderBottom + 8.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), -1);
            if (this.getColorSetting() != Managers.COLOR.getColorSetting()) {
                boolean hoveredSync = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12.0f, alphaSliderBottom + 16.0f, 12.0, 12.0);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Sync", colorPickerLeft, alphaSliderBottom + 17.0f, this.getColorSetting().isSync() ? -1 : -5592406);
                Render2DUtil.drawBorderedRect(hueSliderRight - 12.0f, alphaSliderBottom + 16.0f, hueSliderRight, alphaSliderBottom + 28.0f, 0.5f, this.getColorSetting().isSync() ? (hoveredSync ? ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB()) : (hoveredSync ? 0x66333333 : 0), -16777216);
                if (this.getColorSetting().isSync()) {
                    Render2DUtil.drawCheckMark(hueSliderRight - 6.0f, alphaSliderBottom + 16.0f, 10, -1);
                }
            }
            boolean hoveredRainbow = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), 12.0, 12.0);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Rainbow", colorPickerLeft, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31), this.getColorSetting().isRainbow() ? -1 : -5592406);
            Render2DUtil.drawBorderedRect(hueSliderRight - 12.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), hueSliderRight, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 28 : 42), 0.5f, this.getColorSetting().isRainbow() ? (hoveredRainbow ? ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB()) : (hoveredRainbow ? 0x66333333 : 0), -16777216);
            if (this.getColorSetting().isRainbow()) {
                Render2DUtil.drawCheckMark(hueSliderRight - 6.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), 10, -1);
                float smallWidth = hueSliderRight - colorPickerLeft;
                float lengthSpeed = MathHelper.floor((float)(this.getColorSetting().getRainbowSpeed() / 200.0f * smallWidth));
                float lengthSaturation = MathHelper.floor((float)(this.getColorSetting().getRainbowSaturation() / 100.0f * smallWidth));
                float lengthBrightness = MathHelper.floor((float)(this.getColorSetting().getRainbowBrightness() / 100.0f * smallWidth));
                float offset = alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31);
                boolean hoveredStatic = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14.0f, 12.0, 12.0);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Static", colorPickerLeft, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31) + 14.0f, this.getColorSetting().isStaticRainbow() ? -1 : -5592406);
                Render2DUtil.drawBorderedRect(hueSliderRight - 12.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14.0f, hueSliderRight, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 28 : 42) + 14.0f, 0.5f, this.getColorSetting().isStaticRainbow() ? (hoveredStatic ? ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB()) : (hoveredStatic ? 0x66333333 : 0), -16777216);
                if (this.getColorSetting().isStaticRainbow()) {
                    Render2DUtil.drawCheckMark(hueSliderRight - 6.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14.0f, 10, -1);
                }
                boolean hoveredSpeed = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 28.0f, smallWidth, 12.0);
                boolean hoveredSaturation = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 42.0f, smallWidth, 12.0);
                boolean hoveredBrightness = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 56.0f, smallWidth, 12.0);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Speed: " + (Object)ChatFormatting.GRAY + this.getColorSetting().getRainbowSpeed(), colorPickerLeft, offset + 28.0f, -1);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Saturation: " + (Object)ChatFormatting.GRAY + this.getColorSetting().getRainbowSaturation(), colorPickerLeft, offset + 42.0f, -1);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Brightness: " + (Object)ChatFormatting.GRAY + this.getColorSetting().getRainbowBrightness(), colorPickerLeft, offset + 56.0f, -1);
                Render2DUtil.drawBorderedRect(colorPickerLeft, offset + 36.5f, colorPickerLeft + lengthSpeed, offset + 38.5f, 0.5f, hoveredSpeed ? ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB(), -16777216);
                if (this.slidingSpeed) {
                    float speedValue = ((float)mouseX - colorPickerLeft) * 200.0f / smallWidth;
                    this.getColorSetting().setRainbowSpeed(MathUtil.round(speedValue, 2, 0.0f, 200.0f));
                }
                Render2DUtil.drawBorderedRect(colorPickerLeft, offset + 50.5f, colorPickerLeft + lengthSaturation, offset + 52.5f, 0.5f, hoveredSaturation ? ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB(), -16777216);
                if (this.slidingSaturation) {
                    float saturationValue = ((float)mouseX - colorPickerLeft) * 100.0f / smallWidth;
                    this.getColorSetting().setRainbowSaturation(MathUtil.round(saturationValue, 2, 0.0f, 100.0f));
                }
                Render2DUtil.drawBorderedRect(colorPickerLeft, offset + 64.5f, colorPickerLeft + lengthBrightness, offset + 66.5f, 0.5f, hoveredBrightness ? ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ColorComponent.getClickGui().get()).color.getValue().getRGB(), -16777216);
                if (this.slidingBrightness) {
                    float brightnessValue = ((float)mouseX - colorPickerLeft) * 100.0f / smallWidth;
                    this.getColorSetting().setRainbowBrightness(MathUtil.round(brightnessValue, 2, 0.0f, 100.0f));
                }
            }
        }
        if ((this.getColorSetting().isSync() || this.getColorSetting().isRainbow()) && (this.hue != (hsb = Color.RGBtoHSB(this.getColorSetting().getRed(), this.getColorSetting().getGreen(), this.getColorSetting().getBlue(), null))[0] || this.saturation != hsb[1] || this.brightness != hsb[2] || this.alpha != (float)this.getColorSetting().getAlpha() / 255.0f)) {
            this.hue = hsb[0];
            this.saturation = hsb[1];
            this.brightness = hsb[2];
            this.alpha = (float)this.getColorSetting().getAlpha() / 255.0f;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX() + this.getWidth() - 20.0f, this.getFinishedY() + 4.0f, 15.0, 7.0);
            if (this.isColorExtended()) {
                float expandedX = this.getFinishedX() + 1.0f;
                float expandedY = this.getFinishedY() + 14.0f;
                float colorPickerLeft = expandedX + 6.0f;
                float colorPickerTop = expandedY + 1.0f;
                float colorPickerRight = colorPickerLeft + (this.getWidth() - 20.0f);
                float colorPickerBottom = colorPickerTop + (this.getHeight() - (float)((this.getColorSetting() == Managers.COLOR.getColorSetting() ? 52 : 68) + (this.getColorSetting().isRainbow() ? 56 : 0)));
                float alphaSliderTop = colorPickerBottom + 2.0f;
                float alphaSliderBottom = alphaSliderTop + 4.0f;
                float hueSliderLeft = colorPickerRight + 2.0f;
                float hueSliderRight = hueSliderLeft + 4.0f;
                boolean hoveredCopy = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, alphaSliderBottom + 2.0f, (this.getWidth() - 16.0f) / 2.0f, 12.0);
                boolean hoveredPaste = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - (this.getWidth() - 16.0f) / 2.0f, alphaSliderBottom + 2.0f, (this.getWidth() - 16.0f) / 2.0f, 12.0);
                boolean hoveredSync = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12.0f, alphaSliderBottom + 16.0f, 12.0, 12.0);
                boolean hoveredRainbow = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), 12.0, 12.0);
                float smallWidth = hueSliderRight - colorPickerLeft;
                float offset = alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31);
                boolean hoveredStatic = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12.0f, alphaSliderBottom + (float)(this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14.0f, 12.0, 12.0);
                boolean hoveredSpeed = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 28.0f, smallWidth, 12.0);
                boolean hoveredSaturation = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 42.0f, smallWidth, 12.0);
                boolean hoveredBrightness = RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 56.0f, smallWidth, 12.0);
                if (hoveredRainbow) {
                    this.getColorSetting().setRainbow(!this.getColorSetting().isRainbow());
                }
                if (this.getColorSetting() != Managers.COLOR.getColorSetting() && hoveredSync) {
                    this.getColorSetting().setSync(!this.getColorSetting().isSync());
                }
                if (hoveredCopy) {
                    StringSelection selection = new StringSelection(TextUtil.get32BitString(this.getColorSetting().getRGB()));
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                    ChatUtil.sendMessage("\u00a7aColor Copied: " + TextUtil.get32BitString(this.getColorSetting().getRGB()) + "!");
                }
                if (hoveredPaste && this.getClipBoard() != null) {
                    if (this.getColorSetting().fromString(this.getClipBoard()) == SettingResult.SUCCESSFUL) {
                        float[] hsb = Color.RGBtoHSB(this.getColorSetting().getRed(), this.getColorSetting().getGreen(), this.getColorSetting().getBlue(), null);
                        this.hue = hsb[0];
                        this.saturation = hsb[1];
                        this.brightness = hsb[2];
                        this.alpha = (float)this.getColorSetting().getAlpha() / 255.0f;
                        ChatUtil.sendMessage("\u00a7aColor Pasted: " + this.getClipBoard() + "!");
                    } else {
                        ChatUtil.sendMessage("\u00a7cInvalid Color!");
                    }
                }
                if (!(this.getColorSetting().isSync() || this.getColorSetting().isRainbow() && !this.getColorSetting().isStaticRainbow() || hoveredRainbow || hoveredSync)) {
                    if (RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, colorPickerTop - (float)((this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 32) + (this.getColorSetting().isRainbow() ? 56 : 0)), this.getWidth() - 20.0f, this.getHeight() - 36.0f)) {
                        this.colorSelectorDragging = true;
                    }
                    if (RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderLeft, colorPickerTop - (float)((this.getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 32) + (this.getColorSetting().isRainbow() ? 56 : 0)), 4.0, this.getHeight() - 36.0f)) {
                        this.hueSelectorDragging = true;
                    }
                }
                if (!hoveredRainbow && !hoveredSync && RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, alphaSliderTop, this.getWidth() - 20.0f, 4.0)) {
                    this.alphaSelectorDragging = true;
                }
                if (this.getColorSetting().isRainbow()) {
                    if (hoveredStatic) {
                        this.getColorSetting().setStaticRainbow(!this.getColorSetting().isStaticRainbow());
                    }
                    if (hoveredSpeed) {
                        this.setSlidingSpeed(true);
                    }
                    if (hoveredSaturation) {
                        this.setSlidingSaturation(true);
                    }
                    if (hoveredBrightness) {
                        this.setSlidingBrightness(true);
                    }
                }
            }
            if (hovered) {
                this.setColorExtended(!this.isColorExtended());
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (this.colorSelectorDragging) {
                this.colorSelectorDragging = false;
            }
            if (this.alphaSelectorDragging) {
                this.alphaSelectorDragging = false;
            }
            if (this.hueSelectorDragging) {
                this.hueSelectorDragging = false;
            }
            if (this.slidingSpeed) {
                this.slidingSpeed = false;
            }
            if (this.slidingSaturation) {
                this.slidingSaturation = false;
            }
            if (this.slidingBrightness) {
                this.slidingBrightness = false;
            }
        }
    }

    private String getClipBoard() {
        try {
            return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch (HeadlessException | UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateColor(int hex) {
        this.getColorSetting().setValue(new Color(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, (int)(this.alpha * 255.0f)));
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom) {
        int hueBasedColor = Color.HSBtoRGB(this.hue, 1.0f, 1.0f);
        Render2DUtil.drawGradientRect(left, top, right, bottom, true, -1, hueBasedColor);
        Render2DUtil.drawGradientRect(left, top, right, bottom, false, 0, -16777216);
    }

    public ColorSetting getColorSetting() {
        return this.colorSetting;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public boolean isColorExtended() {
        return this.colorExtended;
    }

    public void setColorExtended(boolean colorExtended) {
        this.colorExtended = colorExtended;
    }

    public void setSlidingSpeed(boolean slidingSpeed) {
        this.slidingSpeed = slidingSpeed;
    }

    public void setSlidingSaturation(boolean slidingSaturation) {
        this.slidingSaturation = slidingSaturation;
    }

    public void setSlidingBrightness(boolean slidingBrightness) {
        this.slidingBrightness = slidingBrightness;
    }
}

