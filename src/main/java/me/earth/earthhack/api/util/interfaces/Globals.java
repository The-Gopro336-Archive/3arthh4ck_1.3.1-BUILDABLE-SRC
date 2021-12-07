package me.earth.earthhack.api.util.interfaces;

import java.util.Objects;
import net.minecraft.client.Minecraft;

public interface Globals {
    public static final Minecraft mc = Objects.requireNonNull(Minecraft.getMinecraft());
}
