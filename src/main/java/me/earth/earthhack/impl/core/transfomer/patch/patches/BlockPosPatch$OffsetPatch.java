package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.transfomer.patch.patches.BlockPosPatch;

final class BlockPosPatch$OffsetPatch {
    public final BlockPosPatch.Direction direction;
    public final String notch;
    public final String notchDesc;
    public final String searge;
    public final String mcp;
    public final String srgMcpDesc;

    public BlockPosPatch$OffsetPatch(BlockPosPatch.Direction direction, String notch, String notchDesc, String searge, String mcp, boolean takesInt) {
        this.direction = direction;
        this.notch = notch;
        this.notchDesc = notchDesc;
        this.searge = searge;
        this.mcp = mcp;
        this.srgMcpDesc = takesInt ? "(I)Lnet/minecraft/util/math/BlockPos;" : "()Lnet/minecraft/util/math/BlockPos;";
    }

    public String toString() {
        return this.notch + this.notchDesc + " -> " + this.mcp + this.srgMcpDesc + " (" + this.searge + ")";
    }
}
