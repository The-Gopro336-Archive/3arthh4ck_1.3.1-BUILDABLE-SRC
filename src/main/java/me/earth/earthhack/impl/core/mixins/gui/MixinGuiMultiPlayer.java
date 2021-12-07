/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiMultiplayer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.ServerData
 */
package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiAddPingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiButtonPingBypassOptions;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiMultiplayer.class})
public abstract class MixinGuiMultiPlayer
extends GuiScreen {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private static final ModuleCache<AutoConfig> CONFIG = Caches.getModule(AutoConfig.class);
    private GuiButton pingBypassButton;

    @Inject(method={"createButtons"}, at={@At(value="HEAD")})
    public void createButtonsHook(CallbackInfo info) {
        this.buttonList.add(new GuiButtonPingBypassOptions(1339, this.width - 24, 5));
        this.pingBypassButton = this.addButton(new GuiButton(1336, this.width - 126, 5, 100, 20, this.getDisplayString()));
    }

    @Inject(method={"actionPerformed"}, at={@At(value="HEAD")}, cancellable=true)
    protected void actionPerformed(GuiButton button, CallbackInfo info) {
        if (button.enabled) {
            if (button.id == this.pingBypassButton.id) {
                PINGBYPASS.toggle();
                this.pingBypassButton.displayString = this.getDisplayString();
                info.cancel();
            } else if (button.id == 1339) {
                this.mc.displayGuiScreen((GuiScreen)new GuiAddPingBypass(this));
                info.cancel();
            }
        }
    }

    @Inject(method={"confirmClicked"}, at={@At(value="HEAD")}, cancellable=true)
    public void confirmClickedHook(boolean result, int id, CallbackInfo info) {
        if (id == this.pingBypassButton.id) {
            this.mc.displayGuiScreen((GuiScreen)this);
            info.cancel();
        }
    }

    @Inject(method={"connectToServer"}, at={@At(value="HEAD")}, cancellable=true)
    public void connectToServerHook(ServerData data, CallbackInfo info) {
        if (CONFIG.isEnabled()) {
            ((AutoConfig)CONFIG.get()).onConnect(data.serverIP);
        }
        if (PINGBYPASS.isEnabled()) {
            this.mc.displayGuiScreen((GuiScreen)new GuiConnectingPingBypass(this, this.mc, data));
            info.cancel();
        }
    }

    private String getDisplayString() {
        return "PingBypass: " + (PINGBYPASS.isEnabled() ? "\u00a7aOn" : "\u00a7cOff");
    }
}

