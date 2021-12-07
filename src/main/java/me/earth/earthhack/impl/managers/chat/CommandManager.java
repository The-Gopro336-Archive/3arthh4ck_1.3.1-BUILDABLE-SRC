/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiTextField
 *  net.minecraft.network.play.client.CPacketChatMessage
 */
package me.earth.earthhack.impl.managers.chat;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.BindCommand;
import me.earth.earthhack.impl.commands.ConfigCommand;
import me.earth.earthhack.impl.commands.ConnectCommand;
import me.earth.earthhack.impl.commands.DisconnectCommand;
import me.earth.earthhack.impl.commands.EnchantCommand;
import me.earth.earthhack.impl.commands.EnemyCommand;
import me.earth.earthhack.impl.commands.EntityDesyncCommand;
import me.earth.earthhack.impl.commands.FolderCommand;
import me.earth.earthhack.impl.commands.FontCommand;
import me.earth.earthhack.impl.commands.FriendCommand;
import me.earth.earthhack.impl.commands.GameModeCommand;
import me.earth.earthhack.impl.commands.GiveCommand;
import me.earth.earthhack.impl.commands.HClipCommand;
import me.earth.earthhack.impl.commands.HelpCommand;
import me.earth.earthhack.impl.commands.HexCommand;
import me.earth.earthhack.impl.commands.HistoryCommand;
import me.earth.earthhack.impl.commands.JavaScriptCommand;
import me.earth.earthhack.impl.commands.KitCommand;
import me.earth.earthhack.impl.commands.LastCommand;
import me.earth.earthhack.impl.commands.MacroCommand;
import me.earth.earthhack.impl.commands.ModuleCommand;
import me.earth.earthhack.impl.commands.ModuleListCommand;
import me.earth.earthhack.impl.commands.PeekCommand;
import me.earth.earthhack.impl.commands.PluginCommand;
import me.earth.earthhack.impl.commands.PrefixCommand;
import me.earth.earthhack.impl.commands.PresetCommand;
import me.earth.earthhack.impl.commands.PrintCommand;
import me.earth.earthhack.impl.commands.QuitCommand;
import me.earth.earthhack.impl.commands.ResetCommand;
import me.earth.earthhack.impl.commands.SayCommand;
import me.earth.earthhack.impl.commands.ShrugCommand;
import me.earth.earthhack.impl.commands.SoundCommand;
import me.earth.earthhack.impl.commands.Thirty2kCommand;
import me.earth.earthhack.impl.commands.TimesCommand;
import me.earth.earthhack.impl.commands.ToggleCommand;
import me.earth.earthhack.impl.commands.VClipCommand;
import me.earth.earthhack.impl.commands.hidden.FailCommand;
import me.earth.earthhack.impl.commands.hidden.HListSettingCommand;
import me.earth.earthhack.impl.commands.hidden.HModulesCommand;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.commands.packet.PacketCommandImpl;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.CPacketChatMessage;

public class CommandManager
extends SubscriberImpl
implements Globals,
Register<Command> {
    private static final Command MODULE_COMMAND = new ModuleCommand();
    private static final Command FAIL_COMMAND = new FailCommand();
    private final Set<Command> commands = new LinkedHashSet<Command>();
    private final Set<Command> hidden = new LinkedHashSet<Command>();
    private String concatenated;
    private String lastMessage;

    public CommandManager() {
        this.listeners.add(new EventListener<PacketEvent.Send<CPacketChatMessage>>(PacketEvent.Send.class, CPacketChatMessage.class){

            @Override
            public void invoke(PacketEvent.Send<CPacketChatMessage> event) {
                if (((CPacketChatMessage)event.getPacket()).getMessage().startsWith(Commands.getPrefix())) {
                    CommandManager.this.applyCommand(((CPacketChatMessage)event.getPacket()).getMessage());
                    if (!((CPacketChatMessage)event.getPacket()).getMessage().toLowerCase().startsWith(Commands.getPrefix() + "last ") && !((CPacketChatMessage)event.getPacket()).getMessage().equalsIgnoreCase(Commands.getPrefix() + "last")) {
                        CommandManager.this.lastMessage = ((CPacketChatMessage)event.getPacket()).getMessage();
                    }
                    event.setCancelled(true);
                }
            }
        });
    }

    public void init() {
        Earthhack.getLogger().info("Initializing Commands.");
        this.commands.add(new ConfigCommand());
        this.commands.add(new FontCommand());
        this.commands.add(new FriendCommand());
        this.commands.add(new EnemyCommand());
        this.commands.add(new HelpCommand());
        this.commands.add(new HexCommand());
        this.commands.add(new HistoryCommand());
        this.commands.add(new MacroCommand());
        this.commands.add(new LastCommand());
        this.commands.add(new ModuleListCommand());
        this.commands.add(new PeekCommand());
        this.commands.add(new PrefixCommand());
        this.commands.add(new ToggleCommand());
        this.commands.add(new TimesCommand());
        this.commands.add(new PluginCommand());
        this.commands.add(new SayCommand());
        this.commands.add(new GameModeCommand());
        this.commands.add(new JavaScriptCommand());
        this.commands.add(new KitCommand());
        this.commands.add(new Thirty2kCommand());
        this.commands.add(new BindCommand());
        this.commands.add(new ResetCommand());
        this.commands.add(new PrintCommand());
        this.commands.add(new QuitCommand());
        this.commands.add(new ConnectCommand());
        this.commands.add(new DisconnectCommand());
        this.commands.add(new VClipCommand());
        this.commands.add(new HClipCommand());
        this.commands.add(new GiveCommand());
        this.commands.add(new EnchantCommand());
        this.commands.add(new ShrugCommand());
        this.commands.add(new EntityDesyncCommand());
        this.commands.add(new SoundCommand());
        this.commands.add(new FolderCommand());
        this.commands.add(new PacketCommandImpl());
        this.commands.add(new PresetCommand());
        this.hidden.add(new HListSettingCommand());
        this.hidden.add(new HModulesCommand());
        this.hidden.add(new HSettingCommand());
        this.setupAndConcatenate();
    }

    @Override
    public void register(Command command) throws AlreadyRegisteredException {
        if (command.isHidden()) {
            this.hidden.add(command);
        } else {
            this.commands.add(command);
        }
        if (command instanceof Registrable) {
            ((Registrable)((Object)command)).onRegister();
        }
        this.setupAndConcatenate();
    }

    @Override
    public void unregister(Command command) throws CantUnregisterException {
        if (command instanceof Registrable) {
            ((Registrable)((Object)command)).onUnRegister();
        }
        this.hidden.remove(command);
        this.commands.remove(command);
        this.setupAndConcatenate();
    }

    @Override
    public Command getObject(String name) {
        Command command = CommandUtil.getNameableStartingWith(name, this.commands);
        if (!(command != null && command.getName().equalsIgnoreCase(name) || (command = CommandUtil.getNameableStartingWith(name, this.hidden)) == null || command.getName().equalsIgnoreCase(name))) {
            return null;
        }
        return command;
    }

    @Override
    public <C extends Command> C getByClass(Class<C> clazz) {
        Command command = (Command)CollectionUtil.getByClass(clazz, this.commands);
        if (command == null) {
            command = (Command)CollectionUtil.getByClass(clazz, this.hidden);
        }
        return (C)command;
    }

    @Override
    public Collection<Command> getRegistered() {
        return this.commands;
    }

    public String getLastCommand() {
        return this.lastMessage;
    }

    private void setupAndConcatenate() {
        this.commands.remove(MODULE_COMMAND);
        this.commands.add(MODULE_COMMAND);
        this.concatenated = this.concatenateCommands();
    }

    public void renderCommandGui(String message, int x, int y) {
        if (message != null && message.startsWith(Commands.getPrefix())) {
            String[] array = this.createArray(message);
            String possible = this.getCommandForMessage(array).getPossibleInputs(array).getFullText();
            int width = x + CommandManager.mc.fontRenderer.getStringWidth(message.trim());
            CommandManager.mc.fontRenderer.drawString(possible, (float)width, (float)y, -1, true);
        }
    }

    public boolean onTabComplete(GuiTextField inputField) {
        if (inputField.getText().startsWith(Commands.getPrefix())) {
            String[] array = this.createArray(inputField.getText());
            Completer completer = this.getCommandForMessage(array).onTabComplete(new Completer(inputField.getText(), array));
            inputField.setText(completer.getResult());
            return completer.shouldMcComplete();
        }
        return true;
    }

    public void applyCommand(String message) {
        if (message != null && message.length() > 1) {
            String[] array = this.createArray(message);
            Command command = this.getCommandForMessage(array);
            if (command.equals(FAIL_COMMAND)) {
                command = this.getHiddenCommand(array);
            }
            command.execute(array);
        }
    }

    public String getConcatenatedCommands() {
        return this.concatenated;
    }

    public Command getCommandForMessage(String[] array) {
        if (array == null || array.length == 0) {
            return FAIL_COMMAND;
        }
        for (Command command : this.commands) {
            if (!command.fits(array)) continue;
            return command;
        }
        return FAIL_COMMAND;
    }

    public String[] createArray(String message) {
        String noPrefix = message.substring(Commands.getPrefix().length());
        return CommandUtil.toArgs(noPrefix);
    }

    private Command getHiddenCommand(String[] array) {
        for (Command command : this.hidden) {
            if (!command.fits(array)) continue;
            return command;
        }
        return FAIL_COMMAND;
    }

    private String concatenateCommands() {
        StringBuilder builder = new StringBuilder();
        Iterator<Command> itr = this.commands.iterator();
        while (itr.hasNext()) {
            builder.append(itr.next().getName().toLowerCase());
            if (!itr.hasNext()) continue;
            builder.append(", ");
        }
        return builder.toString();
    }
}

