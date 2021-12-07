package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;

public class ServerUtil
implements Globals {
    private static final ModuleCache<PingSpoof> PING_SPOOF = Caches.getModule(PingSpoof.class);
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);

    public static void disconnectFromMC(String message) {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null) {
            connection.getNetworkManager().closeChannel(new TextComponentString(message));
        }
    }

    public static int getPingNoPingSpoof() {
        int ping = ServerUtil.getPing();
        if (PING_SPOOF.isEnabled()) {
            ping -= ((PingSpoof)PING_SPOOF.get()).getDelay();
        }
        return ping;
    }

    public static int getPing() {
        if (PINGBYPASS.isEnabled()) {
            return ((PingBypass)PINGBYPASS.get()).getServerPing();
        }
        try {
            NetworkPlayerInfo info;
            NetHandlerPlayClient connection = mc.getConnection();
            if (connection != null && (info = connection.getPlayerInfo(mc.getConnection().getGameProfile().getId())) != null) {
                return info.getResponseTime();
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return 0;
    }
}
