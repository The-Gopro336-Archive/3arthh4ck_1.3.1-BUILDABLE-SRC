package me.earth.earthhack.impl.managers.chat;

import java.util.WeakHashMap;
import net.minecraft.client.gui.ChatLine;

class WrapManager$ChatLineReferenceMap
extends WeakHashMap<ChatLine, Boolean> {
    private int id = -1;

    public WrapManager$ChatLineReferenceMap(ChatLine ... references) {
        if (references != null) {
            for (ChatLine line : references) {
                if (line == null) continue;
                super.put(line, true);
                this.id = line.getChatLineID();
            }
        }
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WrapManager$ChatLineReferenceMap) {
            return ((WrapManager$ChatLineReferenceMap)o).id == this.id;
        }
        return false;
    }
}
