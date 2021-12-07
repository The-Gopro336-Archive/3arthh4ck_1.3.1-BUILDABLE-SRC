/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.event.HoverEvent
 */
package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.impl.core.ducks.util.IHoverEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ChatComponentUtil {
    public static HoverEvent setOffset(HoverEvent event) {
        return ((IHoverEvent)event).setOffset(false);
    }
}

