/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.world.GameType
 */
package me.earth.earthhack.impl.commands;

import java.util.Objects;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;

public class GameModeCommand
extends Command
implements Globals {
    public GameModeCommand() {
        super(new String[][]{{"gamemode"}, {"survival", "creative", "adventure", "spectator"}, {"fake"}});
        CommandDescriptions.register(this, "Allows you to change or, if the 3rd argument is \"fake\", fake your gamemode.");
    }

    @Override
    public void execute(String[] args) {
        if (GameModeCommand.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be ingame to use this command!");
            return;
        }
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cSpecify a gamemode.");
        } else {
            GameType gameType = this.tryToParseFromID(args[1]);
            if (gameType == null && (gameType = this.getGameTypeStartingWith(args[1])) == null) {
                ChatUtil.sendMessage("\u00a7cGameType \u00a7f" + args[1] + "\u00a7c" + " not found.");
                return;
            }
            boolean fake = false;
            if (args.length > 2) {
                fake = args[2].equalsIgnoreCase("fake");
            }
            if (fake && GameModeCommand.mc.playerController != null) {
                ChatUtil.sendMessage("\u00a7aSetting your Client GameType to: \u00a7b" + TextUtil.capitalize(gameType.getName()));
                GameModeCommand.mc.playerController.setGameType(gameType);
            } else if (GameModeCommand.mc.player != null) {
                EntityPlayerMP player;
                if (mc.isSingleplayer() && (player = Objects.requireNonNull(mc.getIntegratedServer()).getPlayerList().getPlayerByUUID(GameModeCommand.mc.player.getUniqueID())) != null) {
                    player.setGameType(gameType);
                    ChatUtil.sendMessage("\u00a7aGamemode set to \u00a7f" + gameType.getName() + "\u00a7a" + ".");
                    return;
                }
                String message = "/gamemode " + gameType.getName();
                ChatUtil.sendMessage("\u00a7a" + message);
                GameModeCommand.mc.player.sendChatMessage(message);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 2) {
            boolean isNumber = true;
            GameType gameType = this.tryToParseFromID(args[1]);
            if (gameType == null) {
                isNumber = false;
                gameType = this.getGameTypeStartingWith(args[1]);
            }
            if (gameType == null) {
                return inputs.setCompletion("").setRest("\u00a7c not found");
            }
            if (isNumber) {
                return inputs.setRest(" (" + gameType.getName() + ") <fake>");
            }
            if (gameType.getName().equalsIgnoreCase(args[1])) {
                return inputs.setRest(" <fake>");
            }
            return inputs.setCompletion(TextUtil.substring(gameType.getName(), args[1].length())).setRest("");
        }
        return inputs;
    }

    private GameType getGameTypeStartingWith(String arg) {
        for (GameType gameType : GameType.values()) {
            if (!gameType.getName().startsWith(arg.toLowerCase())) continue;
            return gameType;
        }
        return null;
    }

    private GameType tryToParseFromID(String idString) {
        try {
            int id = Integer.parseInt(idString);
            return GameType.parseGameTypeWithDefault((int)id, null);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }
}

