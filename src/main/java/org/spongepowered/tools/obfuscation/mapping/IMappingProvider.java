package org.spongepowered.tools.obfuscation.mapping;

import java.io.File;
import java.io.IOException;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;

public interface IMappingProvider {
    public void clear();

    public boolean isEmpty();

    public void read(File var1) throws IOException;

    public MappingMethod getMethodMapping(MappingMethod var1);

    public MappingField getFieldMapping(MappingField var1);

    public String getClassMapping(String var1);

    public String getPackageMapping(String var1);
}
