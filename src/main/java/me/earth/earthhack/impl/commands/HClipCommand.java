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
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.entity.EntityPlayerSP;

public class HClipCommand
extends Command
implements Globals {
    public HClipCommand() {
        super(new String[][]{{"hclip"}, {"amount"}});
        CommandDescriptions.register(this, "Teleports you horizontally.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cPlease specify an amount to be teleported by.");
            return;
        }
        if (HClipCommand.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be ingame to use this command.");
            return;
        }
        try {
            double h = Double.parseDouble(args[1]);
            EntityPlayerSP entity = HClipCommand.mc.player.getRidingEntity() != null ? HClipCommand.mc.player.getRidingEntity() : HClipCommand.mc.player;
            double yaw = Math.cos(Math.toRadians(HClipCommand.mc.player.rotationYaw + 90.0f));
            double pit = Math.sin(Math.toRadians(HClipCommand.mc.player.rotationYaw + 90.0f));
            entity.setPosition(entity.posX + h * yaw, entity.posY, entity.posZ + h * pit);
            ChatUtil.sendMessage("\u00a7aHClipped you \u00a7f" + args[1] + "\u00a7a" + " blocks.");
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

