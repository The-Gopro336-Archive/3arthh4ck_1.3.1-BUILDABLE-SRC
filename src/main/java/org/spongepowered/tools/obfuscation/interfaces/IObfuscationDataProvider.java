package org.spongepowered.tools.obfuscation.interfaces;

import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public interface IObfuscationDataProvider {
    public <T> ObfuscationData<T> getObfEntryRecursive(MemberInfo var1);

    public <T> ObfuscationData<T> getObfEntry(MemberInfo var1);

    public <T> ObfuscationData<T> getObfEntry(IMapping<T> var1);

    public ObfuscationData<MappingMethod> getObfMethodRecursive(MemberInfo var1);

    public ObfuscationData<MappingMethod> getObfMethod(MemberInfo var1);

    public ObfuscationData<MappingMethod> getRemappedMethod(MemberInfo var1);

    public ObfuscationData<MappingMethod> getObfMethod(MappingMethod var1);

    public ObfuscationData<MappingMethod> getRemappedMethod(MappingMethod var1);

    public ObfuscationData<MappingField> getObfFieldRecursive(MemberInfo var1);

    public ObfuscationData<MappingField> getObfField(MemberInfo var1);

    public ObfuscationData<MappingField> getObfField(MappingField var1);

    public ObfuscationData<String> getObfClass(TypeHandle var1);

    public ObfuscationData<String> getObfClass(String var1);
}
