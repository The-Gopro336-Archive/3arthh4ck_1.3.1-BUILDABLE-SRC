package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.NumberComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.player.cleaner.RemovingInteger;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class RemovingIntegerComponent
extends NumberComponent<Integer, RemovingInteger> {
    protected static final IComponentFactory<Integer, RemovingInteger> FACTORY = new RemovingIntegerFactory();

    public RemovingIntegerComponent(final RemovingInteger setting) {
        super(setting);
        if (setting.getContainer() instanceof Module) {
            final Module module = (Module)setting.getContainer();
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Removes this Setting"));
            this.appendSibling(new TextComponentString("\u00a7cRemove ").setStyle(new Style().setHoverEvent(event).setClickEvent(new SmartClickEvent(ClickEvent.Action.RUN_COMMAND){

                @Override
                public String getValue() {
                    return Commands.getPrefix() + "hiddensetting " + module.getName() + " \"" + setting.getName() + "\" remove";
                }
            })));
        }
    }

    private static final class RemovingIntegerFactory
    implements IComponentFactory<Integer, RemovingInteger> {
        private RemovingIntegerFactory() {
        }

        @Override
        public SettingComponent<Integer, RemovingInteger> create(RemovingInteger setting) {
            return new RemovingIntegerComponent(setting);
        }
    }
}
