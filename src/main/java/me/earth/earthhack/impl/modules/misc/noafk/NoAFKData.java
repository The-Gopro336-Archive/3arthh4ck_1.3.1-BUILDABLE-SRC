/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFK;

final class NoAFKData
extends DefaultData<NoAFK> {
    public NoAFKData(NoAFK module) {
        super(module);
        this.register("Rotate", "Makes you rotate.");
        this.register("Swing", "Makes you swing your arm.");
        this.register("Sneak", "Makes you sneak and unsneak.");
        this.register("AutoReply", "Makes you reply to /msg's.");
        this.register("Message", "The message to reply with.");
        this.register("Indicator", "Private Message, depends on server.");
        this.register("Reply", "Private response, depends on server.");
        this.register("Color", "Indicator, depends on server, most servers make private messages LightPurple.");
    }

    @Override
    public int getColor() {
        return -14791015;
    }

    @Override
    public String getDescription() {
        return "Prevents you from getting kicked for being afk, can also be used as AutoReply.";
    }
}

