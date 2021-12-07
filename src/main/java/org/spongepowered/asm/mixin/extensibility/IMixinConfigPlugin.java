package org.spongepowered.asm.mixin.extensibility;

import java.util.List;
import java.util.Set;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public interface IMixinConfigPlugin {
    public void onLoad(String var1);

    public String getRefMapperConfig();

    public boolean shouldApplyMixin(String var1, String var2);

    public void acceptTargets(Set<String> var1, Set<String> var2);

    public List<String> getMixins();

    public void preApply(String var1, ClassNode var2, String var3, IMixinInfo var4);

    public void postApply(String var1, ClassNode var2, String var3, IMixinInfo var4);
}
