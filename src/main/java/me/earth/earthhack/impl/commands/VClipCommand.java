/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.entity.EntityPlayerSP;

public class VClipCommand
extends Command
implements Globals {
    public VClipCommand() {
        super(new String[][]{{"vclip"}, {"amount"}});
        CommandDescriptions.register(this, "Teleports you vertically.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cPlease specify an amount to be vclipped by.");
            return;
        }
        if (VClipCommand.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be ingame to use this command.");
            return;
        }
        try {
            double amount = Double.parseDouble(args[1]);
            EntityPlayerSP entity = VClipCommand.mc.player.getRidingEntity() != null ? VClipCommand.mc.player.getRidingEntity() : VClipCommand.mc.player;
            entity.setPosition(entity.posX, entity.posY + amount, entity.posZ);
            PacketUtil.doY(entity.posY + amount, VClipCommand.mc.player.onGround);
            ChatUtil.sendMessage("\u00a7aVClipped you \u00a7f" + args[1] + "\u00a7a" + " blocks.");
        }
        catch (Exception e) {
            ChatUtil.sendMessage("\u00a7cCouldn't parse \u00a7f" + args[1] + "\u00a7c" + ", a number (can be a floating point one) is required.");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length > 1) {
            return PossibleInputs.empty();
        }
        return super.getPossibleInputs(args);
    }
}

