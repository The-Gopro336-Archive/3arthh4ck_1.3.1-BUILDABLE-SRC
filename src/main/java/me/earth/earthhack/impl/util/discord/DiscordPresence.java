package me.earth.earthhack.impl.util.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.rpc.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
    private static final ModuleCache<RPC> RPC = Caches.getModule(RPC.class);
    public static DiscordRichPresence presence = new DiscordRichPresence();
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private static Thread thread;
    private static int index;

    public static void start() {
        if (RPC.isEnabled()) {
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize("875058498868760648", handlers, true, "");
            DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
            DiscordPresence.presence.details = ((RPC)DiscordPresence.RPC.get()).customDetails.getValue().booleanValue() ? ((RPC)DiscordPresence.RPC.get()).details.getValue() : (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.getMinecraft().getCurrentServerData() != null ? (((RPC)DiscordPresence.RPC.get()).showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + "." : " multiplayer.") : " singleplayer."));
            DiscordPresence.presence.state = ((RPC)DiscordPresence.RPC.get()).state.getValue();
            DiscordPresence.presence.largeImageKey = "phobos";
            DiscordPresence.presence.largeImageText = "3arthh4ck 1.3.1";
            rpc.Discord_UpdatePresence(presence);
            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();
                    DiscordPresence.presence.details = ((RPC)DiscordPresence.RPC.get()).customDetails.getValue().booleanValue() ? ((RPC)DiscordPresence.RPC.get()).details.getValue() : (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.getMinecraft().getCurrentServerData() != null ? (((RPC)DiscordPresence.RPC.get()).showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + "." : " multiplayer.") : " singleplayer."));
                    DiscordPresence.presence.state = ((RPC)DiscordPresence.RPC.get()).state.getValue();
                    if (((RPC)DiscordPresence.RPC.get()).froggers.getValue().booleanValue()) {
                        if (index == 30) {
                            index = 1;
                        }
                        DiscordPresence.presence.largeImageKey = "frog_" + index;
                        ++index;
                    }
                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }, "RPC-Callback-Handler");
            thread.start();
        }
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    static {
        index = 1;
    }
}
