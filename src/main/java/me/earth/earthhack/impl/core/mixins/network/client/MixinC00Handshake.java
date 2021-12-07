package me.earth.earthhack.impl.core.mixins.network.client;

import me.earth.earthhack.impl.core.ducks.network.IC00Handshake;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={C00Handshake.class})
public abstract class MixinC00Handshake
implements IC00Handshake {
    @Shadow
    private String field_149598_b;
    private boolean cancel;

    @Override
    @Accessor(value="ip")
    public abstract void setIP(String var1);

    @Override
    @Accessor(value="port")
    public abstract void setPort(int var1);

    @Override
    public void cancelFML(boolean cancel) {
        this.cancel = cancel;
    }

    @Redirect(method={"writePacketData"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/PacketBuffer;writeString(Ljava/lang/String;)Lnet/minecraft/network/PacketBuffer;"))
    public PacketBuffer writePacketDataHook(PacketBuffer buffer, String string) {
        if (this.cancel) {
            buffer.writeString(this.field_149598_b);
        } else {
            buffer.writeString(string);
        }
        return buffer;
    }
}
