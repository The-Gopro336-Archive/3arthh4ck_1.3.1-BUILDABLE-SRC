/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.arguments.AbstractEntityArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyEntity;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityArgument
extends AbstractArgument<Entity>
implements Globals {
    public EntityArgument() {
        super(Entity.class);
    }

    @Override
    public Entity fromString(String arg) throws ArgParseException {
        if (EntityArgument.mc.world == null) {
            throw new ArgParseException("Minecraft.world was null!");
        }
        Entity entity = AbstractEntityArgument.getEntity(arg, Entity.class);
        if (entity == null) {
            int id = -1337;
            try {
                id = (int)Long.parseLong(arg);
            }
            catch (Exception exception) {
                // empty catch block
            }
            entity = new DummyEntity((World)EntityArgument.mc.world);
            entity.setEntityId(id);
        }
        return entity;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument) {
        if (argument == null || argument.isEmpty()) {
            return new PossibleInputs("", "<" + this.getSimpleName() + ">");
        }
        PossibleInputs inputs = PossibleInputs.empty();
        if (TextUtil.startsWith("$closest", argument)) {
            return inputs.setCompletion(TextUtil.substring("$closest", argument.length()));
        }
        String name = LookUpUtil.findNextPlayerName(argument);
        if (name == null) {
            return inputs;
        }
        return inputs.setCompletion(TextUtil.substring(name, argument.length()));
    }
}

