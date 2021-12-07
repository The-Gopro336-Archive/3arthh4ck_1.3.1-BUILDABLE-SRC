package org.spongepowered.tools.obfuscation.mapping.mcp;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.obfuscation.mapping.mcp.MappingFieldSrg;

class MappingProviderSrg$1
implements LineProcessor<String> {
    final BiMap val$packageMap;
    final BiMap val$classMap;
    final BiMap val$fieldMap;
    final BiMap val$methodMap;
    final File val$input;

    MappingProviderSrg$1(BiMap biMap, BiMap biMap2, BiMap biMap3, BiMap biMap4, File file) {
        this.val$packageMap = biMap;
        this.val$classMap = biMap2;
        this.val$fieldMap = biMap3;
        this.val$methodMap = biMap4;
        this.val$input = file;
    }

    @Override
    public String getResult() {
        return null;
    }

    @Override
    public boolean processLine(String line) throws IOException {
        if (Strings.isNullOrEmpty(line) || line.startsWith("#")) {
            return true;
        }
        String type = line.substring(0, 2);
        String[] args = line.substring(4).split(" ");
        if (type.equals("PK")) {
            this.val$packageMap.forcePut(args[0], args[1]);
        } else if (type.equals("CL")) {
            this.val$classMap.forcePut(args[0], args[1]);
        } else if (type.equals("FD")) {
            this.val$fieldMap.forcePut(new MappingFieldSrg(args[0]).copy(), new MappingFieldSrg(args[1]).copy());
        } else if (type.equals("MD")) {
            this.val$methodMap.forcePut(new MappingMethod(args[0], args[1]), new MappingMethod(args[2], args[3]));
        } else {
            throw new MixinException("Invalid SRG file: " + this.val$input);
        }
        return true;
    }
}
