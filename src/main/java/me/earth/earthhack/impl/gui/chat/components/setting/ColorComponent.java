/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.event.ClickEvent
 *  net.minecraft.util.text.event.ClickEvent$Action
 *  net.minecraft.util.text.event.HoverEvent
 *  net.minecraft.util.text.event.HoverEvent$Action
 */
package me.earth.earthhack.impl.gui.chat.components.setting;

import java.awt.Color;
import java.util.function.Supplier;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.gui.chat.clickevents.SuppliedRunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedHoverableComponent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.gui.chat.util.ColorEnum;
import me.earth.earthhack.impl.gui.chat.util.RainbowEnum;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ColorComponent
extends SettingComponent<Color, ColorSetting> {
    private int otherSettings;

    public ColorComponent(ColorSetting setting) {
        super(setting);
        if (!(setting.getContainer() instanceof Module)) {
            return;
        }
        Module m = (Module)setting.getContainer();
        for (ColorEnum colorEnum : ColorEnum.values()) {
            this.appendSibling(this.supply(() -> "\u00a77 +" + e.getTextColor(), 0).setStyle(new Style().setHoverEvent(this.getHoverEvent(colorEnum.name(), true)).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> e.getCommand(setting, true, m)))));
            this.appendSibling(this.supply(() -> e.getValue(setting) + "", 0).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString(colorEnum.name() + " <0 - 255>"))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Commands.getPrefix() + "hiddensetting " + m.getName() + " \"" + setting.getName() + "\""))));
            this.appendSibling(this.supply(() -> "\u00a77- \u00a7r", 0).setStyle(new Style().setHoverEvent(this.getHoverEvent(colorEnum.name(), false)).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> e.getCommand(setting, false, m)))));
        }
        this.appendSibling(this.supply(() -> (setting.isSync() ? "\u00a7a" : "\u00a7c") + " Sync", 1).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString("Un/Sync this color."))).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> () -> setting.setSync(!setting.isSync())))));
        this.appendSibling(this.supply(() -> (setting.isRainbow() ? "\u00a7a" : "\u00a7c") + " Rainbow", 1).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString("Make this color rainbow."))).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> () -> setting.setRainbow(!setting.isRainbow())))));
        this.appendSibling(this.supply(() -> (setting.isStaticRainbow() ? "\u00a7a" : "\u00a7c") + " Static", 1).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString("Make this color a static rainbow."))).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> () -> setting.setStaticRainbow(!setting.isStaticRainbow())))));
        for (Enum enum_ : RainbowEnum.values()) {
            this.appendSibling(this.supply(() -> ColorComponent.lambda$new$14((RainbowEnum)enum_), 2).setStyle(new Style().setHoverEvent(this.getFloatEvent(enum_.name(), true)).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> ColorComponent.lambda$new$15((RainbowEnum)enum_, setting, m)))));
            this.appendSibling(this.supply(() -> ColorComponent.lambda$new$16((RainbowEnum)enum_, setting), 2).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString(enum_.name() + " " + ((RainbowEnum)enum_).getRange()))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Commands.getPrefix() + "hiddensetting " + m.getName() + " \"" + setting.getName() + "\""))));
            this.appendSibling(this.supply(() -> "\u00a77- \u00a7r", 2).setStyle(new Style().setHoverEvent(this.getFloatEvent(enum_.name(), false)).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> ColorComponent.lambda$new$18((RainbowEnum)enum_, setting, m)))));
        }
        this.appendSibling(new TextComponentString("\u00a77 \u2699").setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new SuppliedComponent(() -> {
            switch (this.otherSettings) {
                case 0: {
                    return "Show more settings";
                }
                case 1: {
                    return "Show rainbow settings.";
                }
                case 2: {
                    return "Show r,g,b settings.";
                }
            }
            throw new IllegalStateException();
        }))).setClickEvent((ClickEvent)new SuppliedRunnableClickEvent(() -> () -> {
            ++this.otherSettings;
            this.otherSettings %= 3;
        }))));
    }

    @Override
    public String getText() {
        if (((ColorSetting)this.setting).isRainbow() || ((ColorSetting)this.setting).isStaticRainbow()) {
            return super.getText() + "\u00a7y" + "\u2588";
        }
        return super.getText() + "\u00a7z" + TextUtil.get32BitString(((Color)((ColorSetting)this.setting).getValue()).getRGB()) + "\u2588";
    }

    private HoverEvent getHoverEvent(String color, boolean incr) {
        return ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString((incr ? "In" : "De") + "crement " + color.toLowerCase() + " value by 1. Hold: " + "\u00a7c" + "ALT " + "\u00a7f" + ": 10," + "\u00a7c" + " RCTRL" + "\u00a7f" + " : " + (incr ? "Max" : "Min") + "\u00a7c" + " LCTRL " + "\u00a7f" + ": 5%, " + "\u00a7c" + "LCTRL + ALT " + "\u00a7f" + ": 10%")));
    }

    private HoverEvent getFloatEvent(String color, boolean incr) {
        return ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString((incr ? "In" : "De") + "crement " + color.toLowerCase() + " value by 0.1. Hold: " + "\u00a7c" + "ALT " + "\u00a7f" + ": 1.0, " + "\u00a7c" + "RCTRL " + "\u00a7f" + ": Max, " + "\u00a7c" + "LCTRL " + "\u00a7f" + ": 5%, " + "\u00a7c" + "LCTRL + ALT " + "\u00a7f" + ": 10%")));
    }

    private SuppliedComponent supply(Supplier<String> s, int isOtherSettings) {
        return new SuppliedHoverableComponent(() -> {
            if (this.otherSettings == isOtherSettings) {
                return (String)s.get();
            }
            return "";
        }, () -> this.otherSettings == isOtherSettings);
    }

    @Override
    public TextComponentString createCopy() {
        ColorComponent component = new ColorComponent((ColorSetting)this.setting);
        component.otherSettings = this.otherSettings;
        return component;
    }

    private static /* synthetic */ Runnable lambda$new$18(RainbowEnum r, ColorSetting setting, Module m) {
        return r.getCommand(setting, false, m);
    }

    private static /* synthetic */ String lambda$new$16(RainbowEnum r, ColorSetting setting) {
        return r.getValue(setting) + "";
    }

    private static /* synthetic */ Runnable lambda$new$15(RainbowEnum r, ColorSetting setting, Module m) {
        return r.getCommand(setting, true, m);
    }

    private static /* synthetic */ String lambda$new$14(RainbowEnum r) {
        return "\u00a77 +" + r.getColor();
    }
}

