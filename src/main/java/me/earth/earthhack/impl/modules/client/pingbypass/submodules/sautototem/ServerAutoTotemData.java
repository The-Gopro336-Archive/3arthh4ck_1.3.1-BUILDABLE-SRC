package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;

final class ServerAutoTotemData
extends DefaultData<ServerAutoTotem> {
    public ServerAutoTotemData(ServerAutoTotem module) {
        super(module);
        this.register("Health", "If you are outside a hole and your health is lower than this, the autototem will switch a totem in your offhand.");
        this.register("SafeHealth", "Same as Health, but for when you are safe.");
        this.register("XCarry", "If the AutoTotem should search for totems in the XCarry.");
    }

    @Override
    public int getColor() {
        return -65536;
    }

    @Override
    public String getDescription() {
        return "An AutoTotem for the PingBypass.";
    }
}
