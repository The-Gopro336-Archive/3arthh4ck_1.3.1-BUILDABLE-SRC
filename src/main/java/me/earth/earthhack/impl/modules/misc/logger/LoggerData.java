/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.logger.Logger;

final class LoggerData
extends DefaultData<Logger> {
    public LoggerData(Logger module) {
        super(module);
        this.register(module.filter, "Filters Packets if on. Use the command <logger <add/del> <packet>> to add packets to filter. Then decide if those packets should be white/blacklisted with the List-Type setting.");
        this.register(module.incoming, "Logs packets coming in from the server.");
        this.register(module.outgoing, "Logs packets that are being send to the server.");
        this.register(module.info, "Logs all fields of the packet.");
        this.register(module.deobfuscate, "Deobfuscates the fields of packets.");
        this.register(module.stackTrace, "Prints the StackTrace so you can see where a Packet comes from.");
        this.register(module.mode, "-Normal, the normal PacketLogger.\n-Buffer, Can bypass certain \"obfuscation\" techniques.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Logs incoming/outgoing packets.";
    }
}

