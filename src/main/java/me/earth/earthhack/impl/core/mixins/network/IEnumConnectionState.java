package me.earth.earthhack.impl.core.mixins.network;

import java.util.Map;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EnumConnectionState.class})
public interface IEnumConnectionState {
    @Accessor(value="STATES_BY_CLASS")
    public Map<Class<? extends Packet<?>>, EnumConnectionState> getStatesByClass();
}
