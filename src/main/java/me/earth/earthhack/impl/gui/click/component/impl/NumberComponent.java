package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class NumberComponent
extends Component {
    private final NumberSetting<Number> numberSetting;
    private boolean sliding;

    public NumberComponent(NumberSetting<Number> numberSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(numberSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.numberSetting = numberSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX(), this.getFinishedY(), this.getWidth(), this.getHeight());
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.getLabel() + ": " + ChatFormatting.GRAY + this.getNumberSetting().getValue(), this.getFinishedX() + 5.0f, this.getFinishedY() + this.getHeight() / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), -1);
        float length = MathHelper.floor((float)((((Number)this.getNumberSetting().getValue()).floatValue() - this.getNumberSetting().getMin().floatValue()) / (this.getNumberSetting().getMax().floatValue() - this.getNumberSetting().getMin().floatValue()) * (this.getWidth() - 10.0f)));
        Render2DUtil.drawBorderedRect(this.getFinishedX() + 5.0f, this.getFinishedY() + this.getHeight() - 2.5f, this.getFinishedX() + 5.0f + length, this.getFinishedY() + this.getHeight() - 0.5f, 0.5f, hovered ? ((ClickGui)NumberComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)NumberComponent.getClickGui().get()).color.getValue().getRGB(), -16777216);
        if (this.sliding) {
            double val = (double)((float)mouseX - (this.getFinishedX() + 5.0f)) * (this.getNumberSetting().getMax().doubleValue() - this.getNumberSetting().getMin().doubleValue()) / (double)(this.getWidth() - 10.0f) + this.getNumberSetting().getMin().doubleValue();
            this.getNumberSetting().setValue(this.getNumberSetting().numberToValue(MathUtil.round(val, 2)));
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX(), this.getFinishedY(), this.getWidth(), this.getHeight());
        if (hovered && mouseButton == 0) {
            this.setSliding(true);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (this.isSliding()) {
            this.setSliding(false);
        }
    }

    public NumberSetting<Number> getNumberSetting() {
        return this.numberSetting;
    }

    public boolean isSliding() {
        return this.sliding;
    }

    public void setSliding(boolean sliding) {
        this.sliding = sliding;
    }
}
