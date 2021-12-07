package org.spongepowered.tools.obfuscation;

import org.spongepowered.asm.obfuscation.mapping.IMapping;

public class Mappings$MappingConflictException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final IMapping<?> oldMapping;
    private final IMapping<?> newMapping;

    public Mappings$MappingConflictException(IMapping<?> oldMapping, IMapping<?> newMapping) {
        this.oldMapping = oldMapping;
        this.newMapping = newMapping;
    }

    public IMapping<?> getOld() {
        return this.oldMapping;
    }

    public IMapping<?> getNew() {
        return this.newMapping;
    }
}
