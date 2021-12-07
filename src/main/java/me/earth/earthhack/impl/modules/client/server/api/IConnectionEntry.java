/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.api.util.interfaces.Nameable;

public interface IConnectionEntry
extends Nameable {
    default public int getId() {
        return 0;
    }
}

