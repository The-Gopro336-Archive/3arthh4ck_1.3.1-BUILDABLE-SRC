package org.spongepowered.asm.mixin.transformer.ext;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

public interface IExtension {
    public boolean checkActive(MixinEnvironment var1);

    public void preApply(ITargetClassContext var1);

    public void postApply(ITargetClassContext var1);

    public void export(MixinEnvironment var1, String var2, boolean var3, byte[] var4);
}
