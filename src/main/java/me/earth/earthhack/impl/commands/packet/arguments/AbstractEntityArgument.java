package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyEntity;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractEntityArgument<T extends Entity>
extends AbstractArgument<T>
implements Globals {
    protected final Class<T> directType;

    public AbstractEntityArgument(Class<T> type) {
        super(type);
        this.directType = type;
    }

    @Override
    public T fromString(String argument) throws ArgParseException {
        if (AbstractEntityArgument.mc.world == null) {
            throw new ArgParseException("Minecraft.world was null!");
        }
        Entity entity = null;
        if (EntityPlayer.class.isAssignableFrom(this.type)) {
            entity = "$closest".equalsIgnoreCase(argument) ? EntityUtil.getClosestEnemy() : AbstractEntityArgument.mc.world.getPlayerEntityByName(argument);
        }
        if (entity == null) {
            try {
                int id = (int)Long.parseLong(argument);
                entity = AbstractEntityArgument.mc.world.getEntityByID(id);
                if (entity == null) {
                    entity = new DummyEntity(AbstractEntityArgument.mc.world);
                    entity.setEntityId(id);
                    ((IEntity)((Object)entity)).setDummy(true);
                }
            }
            catch (Exception e) {
                throw new ArgParseException("Couldn't parse Entity from name or id!");
            }
        }
        return (T)entity;
    }

    public static <E extends Entity> E getEntity(String argument, Class<E> type) {
        Entity entity = null;
        if (type.isAssignableFrom(EntityPlayer.class)) {
            entity = argument.equalsIgnoreCase("$closest") ? EntityUtil.getClosestEnemy() : AbstractEntityArgument.mc.world.getPlayerEntityByName(argument);
        }
        if (entity == null) {
            try {
                int id = (int)Long.parseLong(argument);
                entity = AbstractEntityArgument.mc.world.getEntityByID(id);
                if (!type.isInstance(entity)) {
                    return null;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return (E)entity;
    }
}
