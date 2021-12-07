/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntityEndGateway
 *  net.minecraft.tileentity.TileEntityEndPortal
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.impl.Earthhack;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TileEntityEndGateway.class})
public abstract class MixinTileEntityEndGateway
extends TileEntityEndPortal {
    private static final Minecraft MC = Minecraft.getMinecraft();

    @Redirect(method={"shouldRenderFace"}, at=@At(value="INVOKE", target="Lnet/minecraft/tileentity/TileEntityEndGateway;getBlockType()Lnet/minecraft/block/Block;"))
    private Block shouldRenderFaceHook(TileEntityEndGateway tileEntityEndGateway) {
        Block block = this.getBlockType();
        if (block == null) {
            if (this.world == null && MixinTileEntityEndGateway.MC.world != null) {
                this.setWorld((World)MixinTileEntityEndGateway.MC.world);
                block = MixinTileEntityEndGateway.MC.world.getBlockState(this.getPos()).getBlock();
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

