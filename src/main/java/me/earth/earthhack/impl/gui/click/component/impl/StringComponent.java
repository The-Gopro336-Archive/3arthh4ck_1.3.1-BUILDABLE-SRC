/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.ChatAllowedCharacters
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.impl.gui.click.component.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

public class StringComponent
extends Component {
    private final StringSetting stringSetting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");
    private boolean idling;
    private final StopWatch idleTimer = new StopWatch();

    public StringComponent(StringSetting stringSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(stringSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.stringSetting = stringSetting;
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
        if (this.isListening) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.currentString.getString() + this.getIdleSign(), this.getFinishedX() + 6.5f, this.getFinishedY() + this.getHeight() - (float)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 1.0f, this.getState() ? -1 : -5592406);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow((String)this.getStringSetting().getValue(), this.getFinishedX() + 6.5f, this.getFinishedY() + this.getHeight() - (float)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 1.0f, this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (this.isListening) {
            if (keyCode == 1) {
                return;
            }
            if (keyCode == 28) {
                this.enterString();
                this.setListening(false);
            } else if (keyCode == 14) {
                this.setString(StringComponent.removeLastChar(this.currentString.getString()));
            } else {
                if (keyCode == 47 && (Keyboard.isKeyDown((int)157) || Keyboard.isKeyDown((int)29))) {
                    try {
                        this.setString(this.currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (ChatAllowedCharacters.isAllowedCharacter((char)character)) {
                    this.setString(this.currentString.getString() + character);
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX() + 5.0f, this.getFinishedY() + 1.0f, this.getWidth() - 10.0f, this.getHeight() - 2.0f);
        if (hovered && mouseButton == 0) {
            this.toggle();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public String getIdleSign() {
        if (this.idleTimer.passed(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.getStringSetting().setValue(this.getStringSetting().getInitial());
        } else {
            this.getStringSetting().setValue(this.currentString.getString());
        }
        this.setString("");
    }

    public StringSetting getStringSetting() {
        return this.stringSetting;
    }

    public void toggle() {
        this.isListening = !this.isListening;
    }

    public boolean getState() {
        return !this.isListening;
    }

    public void setListening(boolean listening) {
        this.isListening = listening;
    }

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}

