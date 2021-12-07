package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

class JavaScriptCommand$1
implements SafeRunnable {
    final String val$code;

    JavaScriptCommand$1(String string) {
        this.val$code = string;
    }

    @Override
    public void runSafely() throws Throwable {
        Object o = JavaScriptCommand.this.engine.eval(this.val$code);
        if (o != null || JavaScriptCommand.this.jsNull) {
            Globals.mc.addScheduledTask(() -> ChatUtil.sendMessage(o + ""));
        }
    }

    @Override
    public void handle(Throwable t) {
        String message = JavaScriptCommand.this.replaceRn ? t.getMessage().replace("\r\n", "\n") : t.getMessage();
        Globals.mc.addScheduledTask(() -> ChatUtil.sendMessage("<JavaScript> \u00a7cError: " + message));
    }
}
