package me.earth.earthhack.impl.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.util.text.ChatUtil;

class HistoryCommand$1
extends LookUp {
    final String[] val$args;

    HistoryCommand$1(LookUp.Type type, String name, String[] stringArray) {
        this.val$args = stringArray;
        super(type, name);
    }

    @Override
    public void onSuccess() {
        Globals.mc.addScheduledTask(() -> {
            boolean first = true;
            ChatUtil.sendMessage("");
            for (Map.Entry entry : this.names.entrySet()) {
                String dateString;
                String string = dateString = ((Date)entry.getKey()).getTime() == 0L ? "" : new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format((Date)entry.getKey());
                if (first) {
                    Managers.CHAT.sendDeleteMessage("\u00a7l" + (String)entry.getValue() + "\u00a77" + " - " + "\u00a76" + dateString, this.val$args[1], 3000);
                    first = false;
                    continue;
                }
                ChatUtil.sendMessage((String)entry.getValue() + "\u00a77" + " - " + "\u00a76" + dateString);
            }
            ChatUtil.sendMessage("");
        });
    }

    @Override
    public void onFailure() {
        Managers.CHAT.sendDeleteMessage("\u00a7cFailed to lookup \u00a7f" + this.val$args[1], this.val$args[1], 3000);
    }
}
