/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;

final class LogoutSpotsData
extends DefaultData<LogoutSpots> {
    public LogoutSpotsData(LogoutSpots module) {
        super(module);
        this.register(module.message, "Informs you about players that log out/back in.");
        this.register(module.render, "Renders the logout spots.");
        this.register(module.friends, "Takes friends into account.");
        this.register(module.scale, "Scale of the Nametag above the LogoutSpot.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Highlights the places where players have logged out.";
    }
}

