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

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.gui.chat.components.values.ValueComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class NumberComponent<N extends Number, E extends NumberSetting<N>>
extends SettingComponent<N, NumberSetting<N>> {
    public static final IComponentFactory<?, ?> FACTORY = new NumberComponentFactory();

    public NumberComponent(E setting) {
        super(setting);
        HoverEvent minus;
        HoverEvent plus;
        if (!(((Setting)setting).getContainer() instanceof Module)) {
            this.appendSibling((ITextComponent)new ValueComponent((Setting<?>)setting));
            return;
        }
        final Module module = (Module)((Setting)setting).getContainer();
        HoverEvent numberHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new SuppliedComponent(() -> setting.getName() + " <" + ((Number)setting.getValue()).toString() + "> " + setting.getInputs(null)));
        if (((NumberSetting)setting).isFloating()) {
            plus = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new SuppliedComponent(() -> "Increment " + setting.getName() + " to " + "\u00a7b" + this.getNewValue(true) + "\u00a7f" + " by 0.1. Hold: " + "\u00a7c" + "ALT " + "\u00a7f" + ": 1.0, " + "\u00a7c" + "RCTRL " + "\u00a7f" + ": Max, " + "\u00a7c" + "LCTRL " + "\u00a7f" + ": 5%, " + "\u00a7c" + "LCTRL + ALT " + "\u00a7f" + ": 10%"));
            minus = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new SuppliedComponent(() -> "Decrement " + setting.getName() + " to " + "\u00a7b" + this.getNewValue(false) + "\u00a7f" + " by 0.1. Hold: " + "\u00a7c" + "ALT " + "\u00a7f" + ": 1.0, " + "\u00a7c" + "RCTRL " + "\u00a7f" + ": Min, " + "\u00a7c" + "LCTRL " + "\u00a7f" + ": 5%, " + "\u00a7c" + "LCTRL + ALT " + "\u00a7f" + ": 10%"));
        } else {
            plus = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new SuppliedComponent(() -> "Increment " + setting.getName() + " to " + "\u00a7b" + this.getNewValue(true) + "\u00a7f" + " by 1. Hold: " + "\u00a7c" + "ALT " + "\u00a7f" + ": 10, " + "\u00a7c" + "RCTRL " + "\u00a7f" + ": Max, " + "\u00a7c" + "LCTRL " + "\u00a7f" + ": 5%, " + "\u00a7c" + "LCTRL + ALT " + "\u00a7f" + ": 10%"));
            minus = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new SuppliedComponent(() -> "Decrement " + setting.getName() + " to " + "\u00a7b" + this.getNewValue(false) + "\u00a7f" + " by 1. Hold: " + "\u00a7c" + "ALT " + "\u00a7f" + ": 10, " + "\u00a7c" + "RCTRL " + "\u00a7f" + ": Min, " + "\u00a7c" + "LCTRL " + "\u00a7f" + ": 5%, " + "\u00a7c" + "LCTRL + ALT " + "\u00a7f" + ": 10%"));
        }
        this.appendSibling(new TextComponentString("\u00a77 + \u00a7f").setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(plus)).setClickEvent((ClickEvent)new SmartClickEvent(ClickEvent.Action.RUN_COMMAND, (NumberSetting)setting){
            final /* synthetic */ NumberSetting val$setting;
            {
                this.val$setting = numberSetting;
                super(theAction);
            }

            @Override
            public String getValue() {
                return Commands.getPrefix() + "hiddensetting " + module.getName() + " \"" + this.val$setting.getName() + "\" " + NumberComponent.this.getNewValue(true);
            }
        })));
        this.appendSibling(new ValueComponent((Setting<?>)setting).setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(numberHover)).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Commands.getPrefix() + "hiddensetting " + module.getName() + " \"" + ((Setting)setting).getName() + "\""))));
        this.appendSibling(new TextComponentString("\u00a77 - \u00a7r").setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(minus)).setClickEvent((ClickEvent)new SmartClickEvent(ClickEvent.Action.RUN_COMMAND, (NumberSetting)setting){
            final /* synthetic */ NumberSetting val$setting;
            {
                this.val$setting = numberSetting;
                super(theAction);
            }

            @Override
            public String getValue() {
                return Commands.getPrefix() + "hiddensetting " + module.getName() + " \"" + this.val$setting.getName() + "\" " + NumberComponent.this.getNewValue(false);
            }
        })));
    }

    private String getNewValue(boolean plus) {
        String value = ((NumberSetting)this.setting).isFloating() ? IncrementationUtil.crD(((NumberSetting)this.setting).getValue() + "", ((NumberSetting)this.setting).getMin() + "", ((NumberSetting)this.setting).getMax() + "", !plus) : IncrementationUtil.crL(((Number)((NumberSetting)this.setting).getValue()).longValue(), ((Number)((NumberSetting)this.setting).getMin()).longValue(), ((Number)((NumberSetting)this.setting).getMax()).longValue(), !plus) + "";
        return value;
    }

    private static final class NumberComponentFactory<F extends Number>
    implements IComponentFactory<F, NumberSetting<F>> {
        private NumberComponentFactory() {
        }

        @Override
        public SettingComponent<F, NumberSetting<F>> create(NumberSetting<F> setting) {
            return new NumberComponent(setting);
        }
    }
}

