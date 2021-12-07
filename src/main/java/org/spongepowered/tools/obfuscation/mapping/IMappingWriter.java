package org.spongepowered.tools.obfuscation.mapping;

import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;

public interface IMappingWriter {
    public void write(String var1, ObfuscationType var2, IMappingConsumer.MappingSet<MappingField> var3, IMappingConsumer.MappingSet<MappingMethod> var4);
}
