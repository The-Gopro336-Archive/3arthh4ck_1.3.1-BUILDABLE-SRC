package club.minnced.discord.rpc;

import club.minnced.discord.rpc.DiscordUser;
import com.sun.jna.Callback;

public interface DiscordEventHandlers$OnReady
extends Callback {
    public void accept(DiscordUser var1);
}
