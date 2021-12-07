/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDisconnected
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.core.mixins.gui.util;

import java.util.List;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={GuiDisconnected.class})
public interface IGuiDisconnected {
    @Accessor(value="parentScreen")
    public GuiScreen getParentScreen();

    @Accessor(value="reason")
    public String getReason();

    @Accessor(value="message")
    public ITextComponent getMessage();

    @Accessor(value="multilineMessage")
    public List<String> getMultilineMessage();
}

