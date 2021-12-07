package me.earth.earthhack.impl.managers.minecraft.movement;

import net.minecraft.network.play.client.CPacketEntityAction;

class ActionManager$2 {
    static final int[] $SwitchMap$net$minecraft$network$play$client$CPacketEntityAction$Action;

    static {
        $SwitchMap$net$minecraft$network$play$client$CPacketEntityAction$Action = new int[CPacketEntityAction.Action.values().length];
        try {
            ActionManager$2.$SwitchMap$net$minecraft$network$play$client$CPacketEntityAction$Action[CPacketEntityAction.Action.START_SPRINTING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ActionManager$2.$SwitchMap$net$minecraft$network$play$client$CPacketEntityAction$Action[CPacketEntityAction.Action.STOP_SPRINTING.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ActionManager$2.$SwitchMap$net$minecraft$network$play$client$CPacketEntityAction$Action[CPacketEntityAction.Action.START_SNEAKING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ActionManager$2.$SwitchMap$net$minecraft$network$play$client$CPacketEntityAction$Action[CPacketEntityAction.Action.STOP_SNEAKING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
