package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.impl.commands.gui.EarthhackButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiMainMenu.class})
public abstract class MixinGuiMainMenu
extends GuiScreen {
    private EarthhackButton earthhackButton;

    @Inject(method={"initGui"}, at={@At(value="INVOKE", target="Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal=2, shift=At.Shift.AFTER, remap=false)})
    private void buttonHook(CallbackInfo info) {
        int x = 2;
        int y = 0;
        int w = 2;
        for (GuiButton button : this.buttonList) {
            if (button.id != 4) continue;
            x = button.x;
            y = button.y;
            w = button.width;
            break;
        }
        this.earthhackButton = (EarthhackButton)this.addButton(new EarthhackButton(2500, x + w + 4, y));
    }

    @Inject(method={"actionPerformed"}, at={@At(value="HEAD")}, cancellable=true)
    private void actionPerformedHook(GuiButton button, CallbackInfo info) {
        if (button.id == this.earthhackButton.id) {
            this.earthhackButton.onClick(this, this.earthhackButton.id);
            info.cancel();
        }
    }

    @Inject(method={"confirmClicked"}, at={@At(value="HEAD")}, cancellable=true)
    public void confirmClickedHook(boolean result, int id, CallbackInfo info) {
        if (id == this.earthhackButton.id) {
            this.mc.displayGuiScreen(this);
            info.cancel();
        }
    }
}
