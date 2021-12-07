/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.packet;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.mcp.MappingProvider;

public abstract class AbstractArgument<T>
implements PacketArgument<T> {
    protected final Class<? super T> type;

    public AbstractArgument(Class<? super T> type) {
        this.type = type;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument) {
        if (argument == null || argument.isEmpty()) {
            return new PossibleInputs("", "<" + this.getSimpleName() + ">");
        }
        if (TextUtil.startsWith(this.getSimpleName(), argument)) {
            return PossibleInputs.empty().setCompletion(TextUtil.substring(this.getSimpleName(), argument.length()));
        }
        return PossibleInputs.empty();
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer) {
        return CustomCompleterResult.RETURN;
    }

    @Override
    public Class<? super T> getType() {
        return this.type;
    }

    @Override
    public String getSimpleName() {
        return MappingProvider.simpleName(this.type);
    }
}

