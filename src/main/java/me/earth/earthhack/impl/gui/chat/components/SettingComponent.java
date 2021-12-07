package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
import me.earth.earthhack.impl.core.util.SimpleTextFormatHook;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

public abstract class SettingComponent<T, S extends Setting<T>>
extends AbstractTextComponent {
    protected final S setting;

    public SettingComponent(S setting) {
        super(((Setting)setting).getName());
        this.setting = setting;
        this.setStyle(new Style().setHoverEvent(ComponentFactory.getHoverEvent(setting)));
        ((ITextComponentBase)((Object)this)).setFormattingHook(new SimpleTextFormatHook(this));
        ((ITextComponentBase)((Object)this)).setUnFormattedHook(new SimpleTextFormatHook(this));
        if (((Setting)setting).getContainer() instanceof Module) {
            this.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Commands.getPrefix() + "hiddensetting " + ((Module)((Setting)setting).getContainer()).getName() + " \"" + ((Setting)setting).getName() + "\""));
        }
    }

    @Override
    public String getText() {
        return ((Setting)this.setting).getName() + "\u00a77" + " : " + "\u00a7f";
    }

    @Override
    public String getUnformattedComponentText() {
        return this.getText();
    }

    @Override
    public TextComponentString createCopy() {
        return ComponentFactory.create(this.setting);
    }
}
