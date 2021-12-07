package org.spongepowered.asm.service;

public interface IGlobalPropertyService {
    public <T> T getProperty(String var1);

    public void setProperty(String var1, Object var2);

    public <T> T getProperty(String var1, T var2);

    public String getPropertyString(String var1, String var2);
}
