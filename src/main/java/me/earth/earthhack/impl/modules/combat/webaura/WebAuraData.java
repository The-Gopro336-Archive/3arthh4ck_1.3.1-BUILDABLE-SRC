/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.webaura;

import me.earth.earthhack.impl.modules.combat.webaura.WebAura;
import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;

final class WebAuraData
extends ObbyData<WebAura> {
    public WebAuraData(WebAura module) {
        super(module);
        this.register(module.antiSelfWeb, "Prevents you from webbing yourself.");
        this.register(module.target, "-Closest will target the closest player.\n-Untrapped will target the closest player that isn't in web.");
        this.register(module.targetRange, "Range in which players will be targeted.");
    }

    @Override
    public String getDescription() {
        return "Traps your Enemies with Webs.";
    }
}

