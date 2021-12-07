/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class BooleanComponent
extends Component {
    private final BooleanSetting booleanSetting;

    public BooleanComponent(BooleanSetting booleanSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(booleanSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.booleanSetting = booleanSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX() + this.getWidth() - 17.0f, this.getFinishedY() + 1.0f, 12.0, this.getHeight() - 2.0f);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.getLabel(), this.getFinishedX() + 5.0f, this.getFinishedY() + this.getHeight() / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), (Boolean)this.getBooleanSetting().getValue() != false ? -1 : -5592406);
        Render2DUtil.drawBorderedRect(this.getFinishedX() + this.getWidth() - 17.0f, this.getFinishedY() + 1.0f, this.getFinishedX() + this.getWidth() - 5.0f, this.getFinishedY() + this.getHeight() - 1.0f, 0.5f, ((Boolean)this.getBooleanSetting().getValue()).booleanValue() ? (hovered ? ((ClickGui)BooleanComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)BooleanComponent.getClickGui().get()).color.getValue().getRGB()) : (hovered ? 0x66333333 : 0), -16777216);
        if (((Boolean)this.getBooleanSetting().getValue()).booleanValue()) {
            Render2DUtil.drawCheckMark(this.getFinishedX() + this.getWidth() - 11.0f, this.getFinishedY() + 1.0f, 10, -1);
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX() + this.getWidth() - 17.0f, this.getFinishedY() + 1.0f, 12.0, this.getHeight() - 2.0f);
        if (hovered && mouseButton == 0) {
            this.getBooleanSetting().setValue((Boolean)this.getBooleanSetting().getValue() == false);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public BooleanSetting getBooleanSetting() {
        return this.booleanSetting;
    }
}

