package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.util.text.ChatUtil;

class AbstractPlayerManagerCommand$1
extends LookUp {
    AbstractPlayerManagerCommand$1(LookUp.Type type, String name) {
        super(type, name);
    }

    @Override
    public void onSuccess() {
        AbstractPlayerManagerCommand.this.manager.add(this.name, this.uuid);
        Managers.CHAT.sendDeleteMessageScheduled(AbstractPlayerManagerCommand.this.color + this.name + "\u00a7a" + " was added as " + AbstractPlayerManagerCommand.this.added + ".", this.name, 5000);
    }

    @Override
    public void onFailure() {
        ChatUtil.sendMessageScheduled("\u00a7cFailed to find " + this.name);
    }
}
