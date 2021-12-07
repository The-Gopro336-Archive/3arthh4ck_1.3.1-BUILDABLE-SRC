/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.client.management.Management;

final class ManagementData
extends DefaultData<Management> {
    public ManagementData(Management management) {
        super(management);
        this.descriptions.put(((Management)this.module).clear, "Clears saved TotemPops for all players.");
        this.descriptions.put(((Management)this.module).logout, "If on: totempops get cleared when you log off.");
        this.descriptions.put(((Management)this.module).friend, "If on: Friends you automatically.");
        this.descriptions.put(((Management)this.module).deathTime, "DeathTime for Modules that use SetDead and similar stuff.");
        this.descriptions.put(((Management)this.module).time, "Sets the worlds time. A value of 0 means that time is running normally.");
        this.register(((Management)this.module).pooledScreenShots, "Removes the Lag from Screenshots.");
        this.register(management.soundRemove, "Allows you to disable SoundRemove in the entire client. If its off it overrides all SoundRemove settings in the client.");
    }

    @Override
    public int getColor() {
        return -16777216;
    }

    @Override
    public String getDescription() {
        return "Manages internal settings for 3arthh4ck, it doesn't matter if this module is enabled or not.";
    }
}

