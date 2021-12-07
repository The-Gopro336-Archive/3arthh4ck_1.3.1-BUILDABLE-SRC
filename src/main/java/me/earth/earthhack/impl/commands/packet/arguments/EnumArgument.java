package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public class EnumArgument<T extends Enum<?>>
extends AbstractArgument<T> {
    private final Class<T> directType;

    public EnumArgument(Class<T> type) {
        super(type);
        this.directType = type;
    }

    @Override
    public T fromString(String argument) throws ArgParseException {
        T e = EnumHelper.fromString(this.directType, argument);
        if (e == null) {
            throw new ArgParseException("Could not find Enum: " + argument + " in " + this.getSimpleName() + ".");
        }
        return e;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument) {
        if (argument == null || argument.isEmpty()) {
            return super.getPossibleInputs(argument);
        }
        PossibleInputs inputs = PossibleInputs.empty();
        Enum<?> starting = EnumHelper.getEnumStartingWith(argument, this.directType);
        if (starting != null) {
            return inputs.setCompletion(TextUtil.substring(starting.name(), argument.length()));
        }
        return PossibleInputs.empty();
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer) {
        if (completer.isSame()) {
            Object e;
            String[] args = completer.getArgs();
            try {
                e = this.fromString(args[args.length - 1]);
            }
            catch (ArgParseException exception) {
                return CustomCompleterResult.RETURN;
            }
            String r = CommandUtil.concatenate(args, args.length - 1);
            Enum<?> next = EnumHelper.next(e);
            completer.setResult(Commands.getPrefix() + r + " " + next.name());
            return CustomCompleterResult.RETURN;
        }
        return CustomCompleterResult.PASS;
    }
}
