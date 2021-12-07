/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.arguments.UUIDArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public class GameProfileArgument
extends AbstractArgument<GameProfile> {
    private static final UUIDArgument UUID_ARGUMENT = new UUIDArgument();

    public GameProfileArgument() {
        super(GameProfile.class);
    }

    @Override
    public GameProfile fromString(String argument) throws ArgParseException {
        String[] split = argument.split(",");
        if (split.length != 2) {
            throw new ArgParseException("GameProfile takes 2 arguments: UUID and String!");
        }
        UUID uuid = UUID_ARGUMENT.fromString(split[0]);
        return new GameProfile(uuid, split[1]);
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument) {
        if (argument == null || argument.isEmpty()) {
            return new PossibleInputs("", "<" + this.getSimpleName() + ">");
        }
        String[] split = argument.split(",");
        if (split.length > 2) {
            return PossibleInputs.empty();
        }
        if (split.length == 1) {
            PossibleInputs inputs = UUID_ARGUMENT.getPossibleInputs(argument);
            return inputs.setRest(",name");
        }
        return PossibleInputs.empty();
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer) {
        return CustomCompleterResult.PASS;
    }
}

