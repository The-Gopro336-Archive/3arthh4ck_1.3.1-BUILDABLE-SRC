package me.earth.earthhack.impl.commands.packet.arguments;

import java.util.Iterator;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.potion.Potion;

public class PotionArgument
extends AbstractArgument<Potion> {
    public PotionArgument() {
        super(Potion.class);
    }

    @Override
    public Potion fromString(String argument) throws ArgParseException {
        Potion potion = PotionArgument.getPotionStartingWith(argument);
        if (potion == null) {
            try {
                int id = Integer.parseInt(argument);
                potion = Potion.getPotionById((int)id);
            }
            catch (NumberFormatException e) {
                potion = null;
            }
            if (potion == null) {
                throw new ArgParseException("Could not parse potion from name or id: " + argument + "!");
            }
        }
        return potion;
    }

    @Override
    public PossibleInputs getPossibleInputs(String arg) {
        if (arg == null || arg.isEmpty()) {
            return PossibleInputs.empty().setRest("<potion>");
        }
        PossibleInputs inputs = PossibleInputs.empty();
        Potion potion = PotionArgument.getPotionStartingWith(arg);
        if (potion != null) {
            return inputs.setCompletion(TextUtil.substring(potion.getName(), arg.length()));
        }
        return inputs;
    }

    public static Potion getPotionStartingWith(String argument) {
        Iterator iterator = Potion.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Potion potion = (Potion)iterator.next();
            String name = potion.getName();
            if (!TextUtil.startsWith(name, argument)) continue;
            return potion;
        }
        return null;
    }
}
