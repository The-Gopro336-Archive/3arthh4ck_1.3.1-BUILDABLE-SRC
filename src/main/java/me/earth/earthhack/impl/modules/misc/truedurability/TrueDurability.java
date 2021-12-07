/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.truedurability;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.client.SimpleData;

public class TrueDurability
extends Module {
    public TrueDurability() {
        super("TrueDurability", Category.Player);
        this.setData(new SimpleData(this, "Displays the true durability of unbreakables."));
    }
}

