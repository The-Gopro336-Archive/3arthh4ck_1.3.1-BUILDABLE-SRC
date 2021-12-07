package org.spongepowered.asm.util;

public abstract class ObfuscationUtil {
    private ObfuscationUtil() {
    }

    public static String mapDescriptor(String desc, IClassRemapper remapper) {
        return ObfuscationUtil.remapDescriptor(desc, remapper, false);
    }

    public static String unmapDescriptor(String desc, IClassRemapper remapper) {
        return ObfuscationUtil.remapDescriptor(desc, remapper, true);
    }

    private static String remapDescriptor(String desc, IClassRemapper remapper, boolean unmap) {
        StringBuilder sb = new StringBuilder();
        StringBuilder token = null;
        for (int pos = 0; pos < desc.length(); ++pos) {
            char c = desc.charAt(pos);
            if (token != null) {
                if (c == ';') {
                    sb.append('L').append(ObfuscationUtil.remap(token.toString(), remapper, unmap)).append(';');
                    token = null;
                    continue;
                }
                token.append(c);
                continue;
            }
            if (c == 'L') {
                token = new StringBuilder();
                continue;
            }
            sb.append(c);
        }
        if (token != null) {
            throw new IllegalArgumentException("Invalid descriptor '" + desc + "', missing ';'");
        }
        return sb.toString();
    }

    private static Object remap(String typeName, IClassRemapper remapper, boolean unmap) {
        String result = unmap ? remapper.unmap(typeName) : remapper.map(typeName);
        return result != null ? result : typeName;
    }

    public static interface IClassRemapper {
        public String map(String var1);

        public String unmap(String var1);
    }
}
