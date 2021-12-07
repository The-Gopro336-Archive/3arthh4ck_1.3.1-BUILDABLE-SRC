package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketEntityStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SPacketEntityStatus.class})
public interface ISPacketEntityStatus {
    @Accessor(value="entityId")
    public int getEntityId();

    @Accessor(value="logicOpcode")
    public byte getLogicOpcode();
}
