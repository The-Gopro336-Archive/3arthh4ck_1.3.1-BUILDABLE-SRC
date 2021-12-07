package me.earth.earthhack.impl.commands;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class KitCommand
extends AbstractStackCommand
implements Globals {
    private static final ItemStack KIT;

    public KitCommand() {
        super("kit", "kit");
        CommandDescriptions.register(this, "Gives you a kit.");
    }

    @Override
    protected ItemStack getStack(String[] args) {
        return KIT.copy();
    }

    /*
     * Opcode count of 16429 triggered aggressive code reduction.  Override with --aggressivesizethreshold.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
        ItemStack stack = new ItemStack(Blocks.RED_SHULKER_BOX);
        stack.setStackDisplayName("\u00a7cERROR");
        KIT = stack;
    }
}
