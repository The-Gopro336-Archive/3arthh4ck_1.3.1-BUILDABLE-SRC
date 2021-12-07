/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.media;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.client.media.Media;

final class MediaData
extends DefaultData<Media> {
    protected MediaData(Media module) {
        super(module);
        this.register("Replacement", "The name you want to appear with.");
        this.register("Reload", "Use this in case you changed your name using an altmanager.");
        this.register(module.replaceCustom, "Renders custom names.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Allows you to protect your name when recording or streaming.";
    }
}

