package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.impl.Earthhack;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TileEntityEndGateway.class})
public abstract class MixinTileEntityEndGateway
extends TileEntityEndPortal {
    private static final Minecraft MC = Minecraft.getMinecraft();

    @Redirect(method={"shouldRenderFace"}, at=@At(value="INVOKE", target="Lnet/minecraft/tileentity/TileEntityEndGateway;getBlockType()Lnet/minecraft/block/Block;"))
    private Block shouldRenderFaceHook(TileEntityEndGateway tileEntityEndGateway) {
        Block block = this.func_145838_q();
        if (block == null) {
            if (this.field_145850_b == null && MixinTileEntityEndGateway.MC.world != null) {
                this.func_145834_a(MixinTileEntityEndGateway.MC.world);
                block = MixinTileEntityEndGateway.MC.world.getBlockState(this.func_174877_v()).getBlock();
                if (block == null) {
                    Earthhack.getLogger().warn("EndGateway still null!");
                    return Blocks.END_GATEWAY;
                }
                return block;
            }
            return Blocks.END_GATEWAY;
        }
        return block;
    }
}
