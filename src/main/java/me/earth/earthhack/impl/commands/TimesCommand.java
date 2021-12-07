/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.commands.util.TimesProcess;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class TimesCommand
extends Command
implements CommandScheduler,
Globals {
    private final Map<String, TimesProcess> ids = new ConcurrentHashMap<String, TimesProcess>();
    private final AtomicLong id = new AtomicLong();

    public TimesCommand() {
        super(new String[][]{{"times"}, {"amount", "cancel"}, {"delay", "id"}, {"command"}});
    }

    @Override
    public void execute(String[] args) {
        long delay;
        int amount;
        if (args.length == 1) {
            ChatUtil.sendMessage("Use this command to execute a command x times with a given delay.");
            return;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("cancel")) {
                ChatUtil.sendMessage("\u00a7cNo id specified, available: " + this.ids.keySet() + ".");
            } else {
                ChatUtil.sendMessage("\u00a7cPlease specify a command.");
            }
            return;
        }
        if (args[1].equalsIgnoreCase("cancel")) {
            TimesProcess process = this.ids.get(args[2]);
            if (process == null) {
                ChatUtil.sendMessage("\u00a7cNo process found for id \u00a7f" + args[2] + "\u00a7c" + "!");
                return;
            }
            ChatUtil.sendMessage("\u00a7bCancelling process \u00a7f" + args[2] + "\u00a7b" + "...");
            process.setValid(false);
            process.clear();
            return;
        }
        if (args.length < 4) {
            ChatUtil.sendMessage("\u00a7cPlease specify a command.");
            return;
        }
        try {
            amount = (int)Long.parseLong(args[1]);
            if (amount <= 0) {
                ChatUtil.sendMessage("\u00a7cAmount \u00a7f" + args[1] + "\u00a7c" + " was smaller than or equal to 0!");
                return;
            }
        }
        catch (NumberFormatException e) {
            ChatUtil.sendMessage("\u00a7cCouldn't parse \u00a7f" + args[1] + "\u00a7c" + " to amount.");
            return;
        }
        try {
            delay = Long.parseLong(args[2]);
            if (delay < 0L) {
                ChatUtil.sendMessage("\u00a7cDelay \u00a7f" + args[2] + "\u00a7c" + " was smaller than 0!");
                return;
            }
        }
        catch (NumberFormatException e) {
            ChatUtil.sendMessage("\u00a7cCouldn't parse \u00a7f" + args[2] + "\u00a7c" + " to delay.");
            return;
        }
        String[] arguments = Arrays.copyOfRange(args, 3, args.length);
        Command command = Managers.COMMANDS.getCommandForMessage(arguments);
        if (delay == 0L) {
            mc.addScheduledTask(() -> {
                try {
                    for (int i = 0; i < amount; ++i) {
                        command.execute(arguments);
                    }
                }
                catch (Throwable t) {
                    ChatUtil.sendMessage("\u00a7cAn error occurred while executing command \u00a7f" + arguments[0] + "\u00a7c" + ": " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Runnable last = null;
            String processId = this.id.incrementAndGet() + "";
            TimesProcess process = new TimesProcess(amount);
            for (int i = 0; i < amount; ++i) {
                long time;
                if (last != null) {
                    process.addFuture(SCHEDULER.schedule(last, delay * (long)(i - 1), TimeUnit.MILLISECONDS));
                }
                if ((time = delay * (long)i) < 0L) {
                    ChatUtil.sendMessage("\u00a7cYour delay * amount overflowed!");
                    process.setValid(false);
                    process.clear();
                    return;
                }
                last = () -> mc.addScheduledTask(() -> {
                    if (!process.isValid()) {
                        return;
                    }
                    try {
                        command.execute(arguments);
                    }
                    catch (Throwable t) {
                        ChatUtil.sendMessage("\u00a7cAn error occurred while executing command \u00a7f" + arguments[0] + "\u00a7c" + ": " + t.getMessage());
                        t.printStackTrace();
                    }
                });
            }
            Runnable finalLast = last;
            this.ids.put(processId, process);
            process.addFuture(SCHEDULER.schedule(() -> {
                finalLast.run();
                this.ids.remove(processId);
            }, delay * (long)(amount - 1), TimeUnit.MILLISECONDS));
            ChatUtil.sendMessage("\u00a7aStarted process with id \u00a7b" + processId + "\u00a7a" + ".");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length == 1) {
            return super.getPossibleInputs(args);
        }
        if (args.length == 2) {
            if (TextUtil.startsWith("cancel", args[1])) {
                return super.getPossibleInputs(args);
            }
            return new PossibleInputs("", " <delay> <command>");
        }
        if (TextUtil.startsWith("cancel", args[1])) {
            return PossibleInputs.empty();
        }
        if (args.length == 3) {
            return new PossibleInputs("", " <command>");
        }
        String[] arguments = Arrays.copyOfRange(args, 3, args.length);
        Command command = Managers.COMMANDS.getCommandForMessage(arguments);
        return command.getPossibleInputs(arguments);
    }
}

