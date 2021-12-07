package org.spongepowered.tools.obfuscation;

import org.spongepowered.asm.util.ObfuscationUtil;

final class ObfuscationEnvironment$RemapperProxy
implements ObfuscationUtil.IClassRemapper {
    ObfuscationEnvironment$RemapperProxy() {
    }

    @Override
    public String map(String typeName) {
        if (ObfuscationEnvironment.this.mappingProvider == null) {
            return null;
        }
        return ObfuscationEnvironment.this.mappingProvider.getClassMapping(typeName);
    }

    @Override
    public String unmap(String typeName) {
        if (ObfuscationEnvironment.this.mappingProvider == null) {
            return null;
        }
        return ObfuscationEnvironment.this.mappingProvider.getClassMapping(typeName);
    }
}
