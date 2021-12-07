package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.arguments.AbstractEntityArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractEntityPlayerArgument<T extends EntityPlayer>
extends AbstractEntityArgument<T> {
    public AbstractEntityPlayerArgument(Class<T> type) {
        super(type);
    }

    protected abstract T create();

    @Override
    public T fromString(String arg) throws ArgParseException {
        if (AbstractEntityPlayerArgument.mc.world == null) {
            throw new ArgParseException("Minecraft.world was null!");
        }
        EntityPlayer entity = (EntityPlayer)AbstractEntityArgument.getEntity(arg, this.directType);
        if (entity == null) {
            int id = -1337;
            try {
                id = (int)Long.parseLong(arg);
            }
            catch (Exception exception) {
                // empty catch block
            }
            entity = this.create();
            entity.setEntityId(id);
        }
        return (T)entity;
    }
}
