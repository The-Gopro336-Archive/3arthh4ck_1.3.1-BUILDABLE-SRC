/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.commands.util.EarthhackJsBridge;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;

public class JavaScriptCommand
extends Command
implements Globals,
GlobalExecutor,
CommandScheduler {
    private static final String[] EMPTY = new String[0];
    private final Map<String, String[]> arguments = new HashMap<String, String[]>();
    private final ScriptEngine engine;
    private final boolean replaceRn;
    private final boolean invalid;
    private final boolean jsNull;

    public JavaScriptCommand() {
        super(new String[][]{{"javascript"}, {"code"}});
        CommandDescriptions.register(this, "Allows you to execute JavaScript.");
        Argument arg = DevArguments.getInstance().getArgument("jsrn");
        this.replaceRn = arg == null || (Boolean)arg.getValue() != false;
        arg = DevArguments.getInstance().getArgument("jsnull");
        this.jsNull = arg == null || (Boolean)arg.getValue() != false;
        boolean invalid = false;
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine theEngine = factory.getEngineByName("JavaScript");
        if (theEngine == null) {
            Earthhack.getLogger().warn("JavaScript was null, using nashorn!");
            theEngine = factory.getEngineByName("nashorn");
            if (theEngine == null) {
                invalid = true;
            }
        }
        this.invalid = invalid;
        if (invalid) {
            this.engine = null;
            return;
        }
        this.engine = theEngine;
        EarthhackJsBridge bridge = new EarthhackJsBridge();
        this.engine.put("Earthhack", bridge);
        this.setupArguments();
    }

    @Override
    public boolean fits(String[] args) {
        String low = args[0].toLowerCase();
        return "javascript".startsWith(low) || low.startsWith("javascript");
    }

    @Override
    public void execute(String[] args) {
        if (this.invalid) {
            ChatUtil.sendMessage("\u00a7cYour Java version doesn't support nashorn or JavaScript!");
            return;
        }
        long timeout = 5000L;
        boolean noTimeout = false;
        String s = args[0].toLowerCase().replace("javascript", "");
        if (s.equals("notimeout")) {
            noTimeout = true;
        } else if (!s.isEmpty()) {
            try {
                timeout = Long.parseLong(s);
            }
            catch (NumberFormatException e) {
                ChatUtil.sendMessage("\u00a7cCouldn't parse timeout: \u00a7f" + s + "\u00a7c" + ".");
                return;
            }
            if (timeout < 0L) {
                ChatUtil.sendMessage("\u00a7cTimeout can't be negative!");
                return;
            }
        }
        if (args.length <= 1) {
            ChatUtil.sendMessage("\u00a7cThis command allows you to execute JavaScript Code.\u00a7b Tip:\u00a7f Use the functions offered by \"Math\", or \"Earthhack\".");
            return;
        }
        final String code = CommandUtil.concatenate(args, 1);
        Future<?> future = FIXED_EXECUTOR.submit(new SafeRunnable(){

            @Override
            public void runSafely() throws Throwable {
                Object o = JavaScriptCommand.this.engine.eval(code);
                if (o != null || JavaScriptCommand.this.jsNull) {
                    Globals.mc.addScheduledTask(() -> ChatUtil.sendMessage(o + ""));
                }
            }

            @Override
            public void handle(Throwable t) {
                String message = JavaScriptCommand.this.replaceRn ? t.getMessage().replace("\r\n", "\n") : t.getMessage();
                Globals.mc.addScheduledTask(() -> ChatUtil.sendMessage("<JavaScript> \u00a7cError: " + message));
            }
        });
        if (noTimeout) {
            return;
        }
        long finalTimeout = timeout;
        SCHEDULER.schedule(() -> {
            if (future.cancel(true)) {
                double t = MathUtil.round((double)finalTimeout / 1000.0, 2);
                mc.addScheduledTask(() -> ChatUtil.sendMessage("<JavaScript> \u00a7c" + t + " seconds passed, your js timed out!"));
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        if (completer.isSame() && completer.getArgs().length > 1) {
            String[] args = completer.getArgs();
            String last = args[args.length - 1];
            if (last.isEmpty()) {
                if (args.length == 2) {
                    return completer;
                }
                return super.onTabComplete(completer);
            }
            for (Map.Entry<String, String[]> entry : this.arguments.entrySet()) {
                String[] l;
                if (!last.startsWith(entry.getKey()) || (l = entry.getValue()).length == 0) continue;
                if (entry.getKey().length() == last.length()) {
                    completer.setResult(completer.getInitial() + l[0]);
                    return completer;
                }
                if (l.length == 1) {
                    return completer;
                }
                String r = Commands.getPrefix() + CommandUtil.concatenate(args, 0, args.length - 1);
                if (last.equals(l[l.length - 1])) {
                    String s = entry.getKey() + l[0];
                    return completer.setResult(r + " " + s);
                }
                boolean found = false;
                for (String s : entry.getValue()) {
                    String o = entry.getKey() + s;
                    if (found) {
                        return completer.setResult(r + " " + o);
                    }
                    if (!o.equals(last)) continue;
                    found = true;
                }
            }
        }
        return super.onTabComplete(completer);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length == 1 && args[0].length() > 10 && "javascriptnotimeout".startsWith(args[0].toLowerCase())) {
            return new PossibleInputs(TextUtil.substring("JavaScriptNoTimeout", args[0].length()), " <code>");
        }
        if (args.length >= 2) {
            PossibleInputs inputs = PossibleInputs.empty();
            String last = args[args.length - 1];
            if (!last.isEmpty()) {
                for (Map.Entry<String, String[]> entry : this.arguments.entrySet()) {
                    if (!entry.getKey().startsWith(last) && !last.startsWith(entry.getKey())) continue;
                    if (last.length() < entry.getKey().length()) {
                        return inputs.setCompletion(TextUtil.substring(entry.getKey(), last.length()));
                    }
                    for (String s : entry.getValue()) {
                        String o = entry.getKey() + s;
                        if (!o.startsWith(last)) continue;
                        return inputs.setCompletion(TextUtil.substring(o, last.length()));
                    }
                }
            }
            boolean string = false;
            LinkedList<Character> lastOpened = new LinkedList<Character>();
            for (int i = 1; i < args.length; ++i) {
                String s = args[i];
                block10: for (int j = 0; j < s.length(); ++j) {
                    char c = s.charAt(j);
                    if (string && c != '\'') continue;
                    switch (c) {
                        case '\'': {
                            if (!lastOpened.isEmpty() && ((Character)lastOpened.getLast()).charValue() == '\'') {
                                lastOpened.pollLast();
                                string = false;
                                continue block10;
                            }
                            lastOpened.add(Character.valueOf('\''));
                            string = true;
                            continue block10;
                        }
                        case '{': {
                            lastOpened.add(Character.valueOf('}'));
                            continue block10;
                        }
                        case '}': {
                            Character l = (Character)lastOpened.pollLast();
                            if (l != null && l.charValue() != ')') continue block10;
                            return inputs.setRest("Did you forget a \")\" somewhere?");
                        }
                        case '(': {
                            lastOpened.add(Character.valueOf(')'));
                            continue block10;
                        }
                        case ')': {
                            Character l = (Character)lastOpened.pollLast();
                            if (l != null && l.charValue() != '}') continue block10;
                            return inputs.setRest("Did you forget a \"}\" somewhere?");
                        }
                    }
                }
            }
            Character opened = (Character)lastOpened.pollLast();
            if (opened != null) {
                return inputs.setCompletion(opened.charValue() == '\'' ? "'" : (last.isEmpty() ? "  " + opened : " " + opened));
            }
            return inputs;
        }
        return super.getPossibleInputs(args);
    }

    private void setupArguments() {
        this.arguments.put("function", EMPTY);
        this.arguments.put("return", EMPTY);
        this.arguments.put("Infinity", EMPTY);
        this.arguments.put("NaN", EMPTY);
        this.arguments.put("null", EMPTY);
        this.arguments.put("isNaN(", EMPTY);
        this.arguments.put("isFinite(", EMPTY);
        this.arguments.put("eval(", EMPTY);
        this.arguments.put("Earthhack.", new String[]{"command(", "isEnabled("});
        ArrayList<String> mathArgs = new ArrayList<String>(42);
        mathArgs.add("abs(");
        mathArgs.add("acos(");
        mathArgs.add("acosh(");
        mathArgs.add("asin(");
        mathArgs.add("asinh(");
        mathArgs.add("atan(");
        mathArgs.add("atanh(");
        mathArgs.add("cbrt(");
        mathArgs.add("ceil(");
        mathArgs.add("clz32(");
        mathArgs.add("cos(");
        mathArgs.add("cosh(");
        mathArgs.add("exp(");
        mathArgs.add("expm1(");
        mathArgs.add("floor(");
        mathArgs.add("fround(");
        mathArgs.add("hypot(");
        mathArgs.add("imul(");
        mathArgs.add("log(");
        mathArgs.add("log1p(");
        mathArgs.add("log10(");
        mathArgs.add("log2(");
        mathArgs.add("max(");
        mathArgs.add("min(");
        mathArgs.add("pow(");
        mathArgs.add("random(");
        mathArgs.add("round(");
        mathArgs.add("sign(");
        mathArgs.add("sin(");
        mathArgs.add("sinh(");
        mathArgs.add("sqrt(");
        mathArgs.add("tan(");
        mathArgs.add("tanh(");
        mathArgs.add("trunc(");
        mathArgs.add("E");
        mathArgs.add("LN2");
        mathArgs.add("LN10");
        mathArgs.add("LOG2E");
        mathArgs.add("LOG10E");
        mathArgs.add("PI");
        mathArgs.add("SQRT1_2");
        mathArgs.add("SQRT2");
        this.arguments.put("Math.", mathArgs.toArray(new String[0]));
    }
}

