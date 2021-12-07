package me.earth.earthhack.impl.commands.packet.util;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;

public class DummyServer
extends MinecraftServer {
    public DummyServer(File anvilFileIn, Proxy proxyIn, DataFixer dataFixerIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn) {
        super(new File("*"), proxyIn, dataFixerIn, authServiceIn, sessionServiceIn, profileRepoIn, profileCacheIn);
    }

    public boolean init() throws IOException {
        return false;
    }

    public boolean canStructuresSpawn() {
        return false;
    }

    public GameType getGameType() {
        return GameType.SURVIVAL;
    }

    public EnumDifficulty getDifficulty() {
        return EnumDifficulty.PEACEFUL;
    }

    public boolean isHardcore() {
        return false;
    }

    public int getOpPermissionLevel() {
        return 0;
    }

    public boolean shouldBroadcastRconToOps() {
        return false;
    }

    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    public boolean isDedicatedServer() {
        return false;
    }

    public boolean shouldUseNativeTransport() {
        return false;
    }

    public boolean isCommandBlockEnabled() {
        return false;
    }

    public String shareToLAN(GameType type, boolean allowCheats) {
        return "Dummy-Value";
    }
}
