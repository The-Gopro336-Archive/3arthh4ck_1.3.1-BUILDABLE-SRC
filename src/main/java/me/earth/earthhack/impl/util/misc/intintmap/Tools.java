package me.earth.earthhack.impl.util.misc.intintmap;

public class Tools {
    private static final int INT_PHI = -1640531527;

    public static long nextPowerOfTwo(long x) {
        if (x == 0L) {
            return 1L;
        }
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return (x | x >> 32) + 1L;
    }

    public static int arraySize(int expected, float f) {
        long s = Math.max(2L, Tools.nextPowerOfTwo((long)Math.ceil((float)expected / f)));
        if (s > 0x40000000L) {
            throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
        }
        return (int)s;
    }

    public static int phiMix(int x) {
        int h = x * -1640531527;
        return h ^ h >> 16;
    }
}
