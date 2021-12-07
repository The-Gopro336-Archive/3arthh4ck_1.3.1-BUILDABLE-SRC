package me.earth.earthhack.impl.modules.client.server.protocol;

public final class Protocol {
    public static final int NAME = 0;
    public static final int COMMAND = 1;
    public static final int MESSAGE = 2;
    public static final int PACKET = 3;
    public static final int GLOBAL = 4;
    public static final int PRIVATE = 5;
    public static final int EXCEPTION = 6;
    public static final int CUSTOM = 7;
    public static final int LIST = 8;
    public static final int POSITION = 9;
    public static final int VELOCITY = 10;
    public static final int EATING = 11;

    private Protocol() {
        throw new AssertionError();
    }

    public static int[] ids() {
        int[] result = new int[9];
        for (int i = 0; i < result.length; ++i) {
            result[i] = i;
        }
        return result;
    }
}
