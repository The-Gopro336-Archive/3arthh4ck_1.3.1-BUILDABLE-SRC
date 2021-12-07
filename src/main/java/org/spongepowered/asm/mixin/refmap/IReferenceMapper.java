package org.spongepowered.asm.mixin.refmap;

public interface IReferenceMapper {
    public boolean isDefault();

    public String getResourceName();

    public String getStatus();

    public String getContext();

    public void setContext(String var1);

    public String remap(String var1, String var2);

    public String remapWithContext(String var1, String var2, String var3);
}
