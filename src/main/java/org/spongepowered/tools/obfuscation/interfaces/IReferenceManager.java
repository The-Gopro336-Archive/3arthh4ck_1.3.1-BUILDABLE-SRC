package org.spongepowered.tools.obfuscation.interfaces;

import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.refmap.ReferenceMapper;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationData;

public interface IReferenceManager {
    public void setAllowConflicts(boolean var1);

    public boolean getAllowConflicts();

    public void write();

    public ReferenceMapper getMapper();

    public void addMethodMapping(String var1, String var2, ObfuscationData<MappingMethod> var3);

    public void addMethodMapping(String var1, String var2, MemberInfo var3, ObfuscationData<MappingMethod> var4);

    public void addFieldMapping(String var1, String var2, MemberInfo var3, ObfuscationData<MappingField> var4);

    public void addClassMapping(String var1, String var2, ObfuscationData<String> var3);
}
