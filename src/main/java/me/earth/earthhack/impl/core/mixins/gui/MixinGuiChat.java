/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiTextField
 *  net.minecraft.util.TabCompleter
 *  net.minecraft.util.text.ITextComponent
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.impl.core.ducks.gui.IGuiChat;
import me.earth.earthhack.impl.core.mixins.gui.MixinGuiScreen;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiChat.class})
public abstract class MixinGuiChat
extends MixinGuiScreen
implements IGuiChat {
    @Shadow
    protected GuiTextField inputField;

    @Override
    public void accessSetText(String text, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(text);
        } else {
            this.inputField.writeText(text);
        }
    }

    @Inject(method={"drawScreen(IIF)V"}, at={@At(value="HEAD")})
    public void drawScreenHook(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        Managers.COMMANDS.renderCommandGui(this.inputField.getText(), this.inputField.x, this.inputField.y);
    }

    @Redirect(method={"keyTyped"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/TabCompleter;complete()V"))
    protected void completerHook(TabCompleter completer) {
        if (Managers.COMMANDS.onTabComplete(this.inputField)) {
            completer.complete();
        }
    }

    @Inject(method={"mouseClicked"}, at={@At(value="HEAD")}, cancellable=true)
    protected void mouseClickedHook(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        ITextComponent tc;
        if ((mouseButton == 1 || mouseButton == 2) && this.handleClick(tc = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY()), mouseButton)) {
            info.cancel();
        }
    }
}

