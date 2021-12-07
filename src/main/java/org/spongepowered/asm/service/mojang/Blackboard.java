package org.spongepowered.asm.service.mojang;

import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.service.IGlobalPropertyService;

public class Blackboard
implements IGlobalPropertyService {
    @Override
    public final <T> T getProperty(String key) {
        return (T)Launch.blackboard.get(key);
    }

    @Override
    public final void setProperty(String key, Object value) {
        Launch.blackboard.put(key, value);
    }

    @Override
    public final <T> T getProperty(String key, T defaultValue) {
        Object value = Launch.blackboard.get(key);
        return (T)(value != null ? value : defaultValue);
    }

    @Override
    public final String getPropertyString(String key, String defaultValue) {
        Object value = Launch.blackboard.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
