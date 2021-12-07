package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.modules.client.server.util.ServerMode;

class ServerModule$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$client$server$util$ServerMode;

    static {
        $SwitchMap$me$earth$earthhack$impl$modules$client$server$util$ServerMode = new int[ServerMode.values().length];
        try {
            ServerModule$1.$SwitchMap$me$earth$earthhack$impl$modules$client$server$util$ServerMode[ServerMode.Host.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerModule$1.$SwitchMap$me$earth$earthhack$impl$modules$client$server$util$ServerMode[ServerMode.Client.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
