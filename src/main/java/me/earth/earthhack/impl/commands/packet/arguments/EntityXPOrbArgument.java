package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.item.EntityXPOrb;

public class EntityXPOrbArgument
extends AbstractArgument<EntityXPOrb>
implements Globals {
    public EntityXPOrbArgument() {
        super(EntityXPOrb.class);
    }

    @Override
    public EntityXPOrb fromString(String argument) throws ArgParseException {
        int id;
        if (EntityXPOrbArgument.mc.world == null || EntityXPOrbArgument.mc.player == null) {
            throw new ArgParseException("Minecraft.World was null!");
        }
        String[] split = argument.split(",");
        if (split.length == 0) {
            throw new ArgParseException("XP-Orb was empty!");
        }
        try {
            id = Integer.parseInt(split[0]);
        }
        catch (NumberFormatException e) {
            throw new ArgParseException("Could not parse XP-ID from " + split[0] + "!");
        }
        int amount = 1;
        if (split.length > 1) {
            try {
                amount = Integer.parseInt(split[1]);
            }
            catch (NumberFormatException e) {
                throw new ArgParseException("Could not parse XP-Amount from " + split[1] + "!");
            }
        }
        double x = split.length > 2 ? this.tryParse(split[2], "x") : EntityXPOrbArgument.mc.player.posX;
        double y = split.length > 3 ? this.tryParse(split[3], "y") : EntityXPOrbArgument.mc.player.posY;
        double z = split.length > 4 ? this.tryParse(split[4], "z") : EntityXPOrbArgument.mc.player.posZ;
        EntityXPOrb entity = new EntityXPOrb(EntityXPOrbArgument.mc.world, x, y, z, amount);
        entity.func_145769_d(id);
        return entity;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument) {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty()) {
            return inputs.setRest("<XP-Orb:id,amount,x,y,z>");
        }
        String[] split = argument.split(",");
        switch (split.length) {
            case 0: {
                return inputs.setRest("<XP-Orb:id,amount,x,y,z>");
            }
            case 1: {
                return inputs.setCompletion(",").setRest("amount,x,y,z>");
            }
            case 2: {
                return inputs.setCompletion(",").setRest("x,y,z>");
            }
            case 3: {
                return inputs.setCompletion(",").setRest("y,z>");
            }
            case 4: {
                return inputs.setCompletion(",").setRest("z>");
            }
        }
        return inputs;
    }

    private double tryParse(String string, String message) throws ArgParseException {
        try {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException e) {
            throw new ArgParseException("Could not parse " + message + " from " + string);
        }
    }
}
