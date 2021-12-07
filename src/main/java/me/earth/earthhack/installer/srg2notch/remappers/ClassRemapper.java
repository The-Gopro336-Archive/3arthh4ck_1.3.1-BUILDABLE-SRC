/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 */
package me.earth.earthhack.installer.srg2notch.remappers;

import java.util.ArrayList;
import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import me.earth.earthhack.installer.srg2notch.remappers.Remapper;
import org.objectweb.asm.tree.ClassNode;

public class ClassRemapper
implements Remapper {
    @Override
    public void remap(ClassNode cn, Mapping mapping) {
        if (cn.superName != null) {
            cn.superName = mapping.getClasses().getOrDefault(cn.superName, cn.superName);
        }
        if (cn.interfaces != null && !cn.interfaces.isEmpty()) {
            ArrayList<String> interfaces = new ArrayList<String>(cn.interfaces.size());
            for (String i : cn.interfaces) {
                interfaces.add(mapping.getClasses().getOrDefault(i, i));
            }
            cn.interfaces = interfaces;
        }
        if (cn.signature != null) {
            cn.signature = MappingUtil.mapSignature(cn.signature, mapping);
        }
    }
}

