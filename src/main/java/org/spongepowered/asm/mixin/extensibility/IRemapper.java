package org.spongepowered.asm.mixin.extensibility;

public interface IRemapper {
    public String mapMethodName(String var1, String var2, String var3);

    public String mapFieldName(String var1, String var2, String var3);

    public String map(String var1);

    public String unmap(String var1);

    public String mapDesc(String var1);

    public String unmapDesc(String var1);
}
