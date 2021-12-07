package me.earth.earthhack.impl.commands.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CommandUtil
implements Globals {
    public static String concatenate(String[] args, int startIndex) {
        return CommandUtil.concatenate(args, startIndex, args.length);
    }

    public static String concatenate(String[] args, int startIndex, int end) {
        if (startIndex < 0 || startIndex >= args.length) {
            throw new ArrayIndexOutOfBoundsException(startIndex);
        }
        if (end > args.length) {
            throw new ArrayIndexOutOfBoundsException(end);
        }
        StringBuilder builder = new StringBuilder(args[startIndex]);
        for (int i = startIndex + 1; i < end; ++i) {
            builder.append(" ").append(args[i]);
        }
        return builder.toString();
    }

    public static String completeBoolean(String bool) {
        if (bool == null) {
            return "";
        }
        if ("true".startsWith(bool.toLowerCase())) {
            return TextUtil.substring("true", bool.length());
        }
        if ("false".startsWith(bool.toLowerCase())) {
            return TextUtil.substring("false", bool.length());
        }
        return null;
    }

    public static <T extends Nameable> T getNameableStartingWith(String prefix, Register<T> from) {
        return CommandUtil.getNameableStartingWith(prefix, from.getRegistered());
    }

    public static <T extends Nameable> T getNameableStartingWith(String prefix, Collection<T> from) {
        for (Nameable t : from) {
            if (!TextUtil.startsWith(t.getName(), prefix)) continue;
            return (T)t;
        }
        return null;
    }

    public static <T> ITextComponent concatenate(ITextComponent base, Iterable<T> iterable, Function<T, ITextComponent> f) {
        Iterator<T> it = iterable.iterator();
        while (it.hasNext()) {
            T t = it.next();
            base.appendSibling(f.apply(t));
            if (!it.hasNext()) continue;
            base.appendSibling(new TextComponentString(", "));
        }
        return base;
    }

    public static <T> StringBuilder concatenate(StringBuilder builder, Iterable<T> iterable, BiConsumer<StringBuilder, T> c) {
        Iterator<T> it = iterable.iterator();
        while (it.hasNext()) {
            T t = it.next();
            c.accept(builder, (StringBuilder)t);
            if (!it.hasNext()) continue;
            builder.append(", ");
        }
        return builder;
    }

    public static int levenshtein(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];
        for (int i = 0; i <= x.length(); ++i) {
            for (int j = 0; j <= y.length(); ++j) {
                dp[i][j] = i == 0 ? j : (j == 0 ? i : CommandUtil.min(dp[i - 1][j - 1] + CommandUtil.costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), dp[i - 1][j] + 1, dp[i][j - 1] + 1));
            }
        }
        return dp[x.length()][y.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int ... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    public static String[] toArgs(String input) {
        boolean escaped = false;
        ArrayList<String> strings = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (ch == '\"') {
                if (escaped) {
                    strings.add(builder.toString());
                    builder = new StringBuilder();
                    escaped = false;
                    continue;
                }
                escaped = true;
                continue;
            }
            if (ch == ' ' && !escaped) {
                if (builder.length() == 0) continue;
                strings.add(builder.toString());
                builder = new StringBuilder();
                continue;
            }
            builder.append(ch);
        }
        if (builder.length() != 0) {
            strings.add(builder.toString());
        }
        return strings.toArray(new String[0]);
    }
}
