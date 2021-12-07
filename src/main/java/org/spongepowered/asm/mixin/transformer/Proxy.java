package org.spongepowered.asm.mixin.transformer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

public final class Proxy
implements IClassTransformer,
ILegacyClassTransformer {
    private static List<Proxy> proxies = new ArrayList<Proxy>();
    private static MixinTransformer transformer = new MixinTransformer();
    private boolean isActive = true;

    public Proxy() {
        for (Proxy hook : proxies) {
            hook.isActive = false;
        }
        proxies.add(this);
        LogManager.getLogger("mixin").debug("Adding new mixin transformer proxy #{}", new Object[]{proxies.size()});
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (this.isActive) {
            return transformer.transformClassBytes(name, transformedName, basicClass);
        }
        return basicClass;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        return true;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        if (this.isActive) {
            return transformer.transformClassBytes(name, transformedName, basicClass);
        }
        return basicClass;
    }
}
