package me.earth.earthhack.impl.core.transfomer.patch.patches;

enum BlockPosPatch$Direction {
    UP(1),
    DOWN(-1),
    NORTH(-1),
    SOUTH(1),
    WEST(-1),
    EAST(1);

    public final int offset;

    private BlockPosPatch$Direction(int offset) {
        this.offset = offset;
    }
}
