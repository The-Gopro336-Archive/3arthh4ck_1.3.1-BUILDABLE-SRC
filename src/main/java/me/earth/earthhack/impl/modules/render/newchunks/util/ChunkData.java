package me.earth.earthhack.impl.modules.render.newchunks.util;

public class ChunkData {
    private final int x;
    private final int z;

    public ChunkData(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int hashCode() {
        int hash = 23;
        hash = hash * 31 + this.x;
        hash = hash * 31 + this.z;
        return hash;
    }

    public boolean equals(Object o) {
        if (o instanceof ChunkData) {
            return Double.compare(((ChunkData)o).x, this.x) == 0 && Double.compare(((ChunkData)o).z, this.z) == 0;
        }
        return super.equals(o);
    }
}
