package me.earth.earthhack.installer.srg2notch;

import me.earth.earthhack.impl.core.util.AsmUtil;
import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.remappers.AnnotationRemapper;
import me.earth.earthhack.installer.srg2notch.remappers.ClassRemapper;
import me.earth.earthhack.installer.srg2notch.remappers.FieldRemapper;
import me.earth.earthhack.installer.srg2notch.remappers.InstructionRemapper;
import me.earth.earthhack.installer.srg2notch.remappers.MethodRemapper;
import me.earth.earthhack.installer.srg2notch.remappers.Remapper;
import org.objectweb.asm.tree.ClassNode;

public class ASMRemapper {
    private final Remapper[] reMappers = new Remapper[5];

    public ASMRemapper() {
        this.reMappers[0] = new ClassRemapper();
        this.reMappers[1] = new FieldRemapper();
        this.reMappers[2] = new MethodRemapper();
        this.reMappers[3] = new InstructionRemapper();
        this.reMappers[4] = new AnnotationRemapper();
    }

    public byte[] transform(byte[] clazz, Mapping mapping) {
        ClassNode cn;
        try {
            cn = AsmUtil.read(clazz, new int[0]);
        }
        catch (IllegalArgumentException e) {
            return clazz;
        }
        for (Remapper remapper : this.reMappers) {
            remapper.remap(cn, mapping);
        }
        return AsmUtil.write(cn, new int[0]);
    }
}
