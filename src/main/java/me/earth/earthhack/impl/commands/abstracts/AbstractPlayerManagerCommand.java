/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.event.ClickEvent
 *  net.minecraft.util.text.event.HoverEvent
 *  net.minecraft.util.text.event.HoverEvent$Action
 */
package me.earth.earthhack.impl.commands.abstracts;

import java.util.Iterator;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.gui.chat.clickevents.RunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.PlayerManager;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public abstract class AbstractPlayerManagerCommand
extends Command
implements Globals {
    private final PlayerManager manager;
    private final String listingName;
    private final String added;
    private final String verb;
    private final String color;

    public AbstractPlayerManagerCommand(PlayerManager manager, String name, String listingName, String verb, String added, String color) {
        super(new String[][]{{name}, {"add", "del", "list"}, {"name"}});
        this.added = added;
        this.manager = manager;
        this.listingName = listingName;
        this.verb = verb;
        this.color = color;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1 || args.length == 2 && args[1].equalsIgnoreCase("list")) {
            Managers.CHAT.sendDeleteComponent(this.getComponent(), this.verb, 5000);
        } else if (args.length == 2) {
            boolean isAdded = this.manager.contains(args[1]);
            ChatUtil.sendMessage(args[1] + (isAdded ? "\u00a7a" : "\u00a7c") + " is " + (isAdded ? "" : "not ") + this.verb + ".");
        } else {
            String name = args[2];
            if (args[1].equalsIgnoreCase("add")) {
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.UUID, name){

                    @Override
                    public void onSuccess() {
                        AbstractPlayerManagerCommand.this.manager.add(this.name, this.uuid);
                        Managers.CHAT.sendDeleteMessageScheduled(AbstractPlayerManagerCommand.this.color + this.name + "\u00a7a" + " was added as " + AbstractPlayerManagerCommand.this.added + ".", this.name, 5000);
                    }

                    @Override
                    public void onFailure() {
                        ChatUtil.sendMessageScheduled("\u00a7cFailed to find " + this.name);
                    }
                });
            } else if (args[1].equalsIgnoreCase("del")) {
                this.manager.remove(name);
                Managers.CHAT.sendDeleteMessage("\u00a7c" + name + " un" + this.verb + ".", name, 5000);
            } else {
                ChatUtil.sendMessage("\u00a7cPlease specify <add/del>.");
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 1) {
            return inputs;
        }
        if (args.length == 2) {
            String filler = this.fillArgs(args[1]);
            if (filler != null) {
                return inputs.setCompletion(TextUtil.substring(filler, args[1].length())).setRest(filler.equalsIgnoreCase("list") ? "" : inputs.getRest());
            }
            String next = LookUpUtil.findNextPlayerName(args[1]);
            return inputs.setCompletion(TextUtil.substring(next == null ? "" : next, args[1].length()));
        }
        if (args.length == 3) {
            String next = LookUpUtil.findNextPlayerName(args[2]);
            return inputs.setCompletion(TextUtil.substring(next == null ? "" : next, args[2].length()));
        }
        return inputs.setCompletion("").setRest("\u00a7cinvalid.");
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        String next;
        if (completer.isSame()) {
            if (completer.getArgs().length == 2) {
                for (int i = 0; i < this.getUsage()[0].length; ++i) {
                    String str = this.getUsage()[0][i];
                    if (!str.equalsIgnoreCase(completer.getArgs()[1])) continue;
                    String result = i == this.getUsage()[0].length - 1 ? this.getUsage()[0][0] : this.getUsage()[0][i + 1];
                    String newInitial = TextUtil.substring(completer.getInitial().trim(), 0, completer.getInitial().length() - completer.getArgs()[completer.getArgs().length - 1].length());
                    completer.setResult(newInitial + result);
                }
            } else {
                completer.setMcComplete(true);
            }
            completer.setLastCompleted(completer.getResult());
            return completer;
        }
        String[] args = completer.getArgs();
        if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("del")) && (next = LookUpUtil.findNextPlayerName(args[2])) != null) {
            String result = TextUtil.substring(completer.getInitial().trim(), 0, completer.getInitial().trim().length() - args[2].length());
            completer.setResult(result + next);
            return completer;
        }
        return super.onTabComplete(completer);
    }

    private String fillArgs(String input) {
        for (String str : this.getUsage()[1]) {
            if (!str.startsWith(input.toLowerCase())) continue;
            return str;
        }
        return null;
    }

    private ITextComponent getComponent() {
        TextComponentString component = new TextComponentString(this.listingName + ": ");
        Iterator<String> players = this.manager.getPlayers().iterator();
        while (players.hasNext()) {
            String name = players.next();
            component.appendSibling(new TextComponentString(this.color + name + "\u00a7f" + (players.hasNext() ? ", " : "")).setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString("UUID: " + this.manager.getPlayersWithUUID().get(name))))).setClickEvent((ClickEvent)new RunnableClickEvent(() -> {
                GuiScreen before = AbstractPlayerManagerCommand.mc.currentScreen;
                mc.displayGuiScreen((GuiScreen)new YesNoNonPausing((result, id) -> {
                    mc.displayGuiScreen(before);
                    if (!result) {
                        return;
                    }
                    this.manager.remove(name);
                    Managers.CHAT.sendDeleteComponent(this.getComponent(), this.verb, 5000);
                    ChatUtil.sendMessage("\u00a7c" + name + " un" + this.verb + ".");
                }, "", this.color + name + "\u00a7f" + " will be un" + this.verb + ". Continue?", 1337));
            }))));
        }
        return component;
    }
}

