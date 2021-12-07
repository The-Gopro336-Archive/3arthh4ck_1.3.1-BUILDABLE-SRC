package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ComponentBuilder {
    private final ITextComponent base;
    private ITextComponent current;

    public ComponentBuilder(String start) {
        this(new TextComponentString(start));
    }

    public ComponentBuilder(ITextComponent base) {
        this.base = base;
        this.current = base;
    }

    public ComponentBuilder append() {
        this.base.appendSibling(this.current);
        return this;
    }

    public ComponentBuilder sibling(String siblingText) {
        return this.sibling(new TextComponentString(siblingText));
    }

    public ComponentBuilder sibling(ITextComponent sibling) {
        this.current = sibling;
        return this;
    }

    public ComponentBuilder addHover(String hoverText) {
        return this.addHover(HoverEvent.Action.SHOW_TEXT, new TextComponentString(hoverText));
    }

    public ComponentBuilder addHover(HoverEvent.Action actionIn, ITextComponent valueIn) {
        return this.addHover(new HoverEvent(actionIn, valueIn));
    }

    public ComponentBuilder addHover(HoverEvent event) {
        this.current.getStyle().setHoverEvent(event);
        return this;
    }

    public ComponentBuilder addSmartClickEvent(final String command) {
        return this.addClickEvent(new SmartClickEvent(ClickEvent.Action.RUN_COMMAND){

            @Override
            public String getValue() {
                return Commands.getPrefix() + command;
            }
        });
    }

    public ComponentBuilder addClickEvent(ClickEvent event) {
        this.current.getStyle().setClickEvent(event);
        return this;
    }

    public ITextComponent build() {
        return this.base;
    }
}
