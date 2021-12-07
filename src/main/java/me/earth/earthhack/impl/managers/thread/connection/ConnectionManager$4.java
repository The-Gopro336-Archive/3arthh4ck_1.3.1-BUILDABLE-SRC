package me.earth.earthhack.impl.managers.thread.connection;

import net.minecraft.network.play.server.SPacketPlayerListItem;

class ConnectionManager$4 {
    static final int[] $SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action;

    static {
        $SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action = new int[SPacketPlayerListItem.Action.values().length];
        try {
            ConnectionManager$4.$SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action[SPacketPlayerListItem.Action.ADD_PLAYER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ConnectionManager$4.$SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action[SPacketPlayerListItem.Action.REMOVE_PLAYER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
