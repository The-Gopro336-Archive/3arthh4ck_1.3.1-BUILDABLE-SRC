package me.earth.earthhack.impl.commands.packet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.factory.PacketFactory;
import me.earth.earthhack.impl.commands.packet.generic.GenericArgument;
import net.minecraft.network.Packet;

public interface PacketCommand {
    public Class<? extends Packet<?>> getPacket(String var1);

    public Map<Class<? extends Packet<?>>, List<GenericArgument<?>>> getGenerics();

    public Map<Class<? extends Packet<?>>, PacketFactory> getCustom();

    public Set<Class<? extends Packet<?>>> getPackets();

    public Map<Class<?>, PacketArgument<?>> getArguments();

    public String getName(Class<? extends Packet<?>> var1);
}
