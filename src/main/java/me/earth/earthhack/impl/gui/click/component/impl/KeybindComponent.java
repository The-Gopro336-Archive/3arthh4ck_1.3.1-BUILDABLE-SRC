package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class KeybindComponent
extends Component {
    private final BindSetting bindSetting;
    private boolean binding;

    public KeybindComponent(BindSetting bindSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(bindSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.bindSetting = bindSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX() + 5.0f, this.getFinishedY() + 1.0f, this.getWidth() - 10.0f, this.getHeight() - 2.0f);
        Render2DUtil.drawBorderedRect(this.getFinishedX() + 4.5f, this.getFinishedY() + 1.0f, this.getFinishedX() + this.getWidth() - 4.5f, this.getFinishedY() + this.getHeight() - 0.5f, 0.5f, hovered ? 0x66333333 : 0, -16777216);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.isBinding() ? "Press a key..." : this.getBindSetting().getName() + ": " + ChatFormatting.GRAY + this.getBindSetting().getValue(), this.getFinishedX() + 6.5f, this.getFinishedY() + this.getHeight() - (float)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 1.0f, -1);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (this.isBinding()) {
            Bind bind = Bind.fromKey(keyCode == 1 || keyCode == 57 || keyCode == 211 ? 0 : keyCode);
            this.getBindSetting().setValue(bind);
            this.setBinding(false);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX() + 5.0f, this.getFinishedY() + 1.0f, this.getWidth() - 10.0f, this.getHeight() - 2.0f);
        if (hovered && mouseButton == 0) {
            this.setBinding(!this.isBinding());
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public BindSetting getBindSetting() {
        return this.bindSetting;
    }

    public boolean isBinding() {
        return this.binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
