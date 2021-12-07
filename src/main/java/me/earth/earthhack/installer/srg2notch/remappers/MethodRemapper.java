/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.LocalVariableNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TryCatchBlockNode
 */
package me.earth.earthhack.installer.srg2notch.remappers;

import java.util.ArrayList;
import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import me.earth.earthhack.installer.srg2notch.remappers.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class MethodRemapper
implements Remapper {
    @Override
    public void remap(ClassNode cn, Mapping mapping) {
        for (MethodNode mn : cn.methods) {
            mn.desc = MappingUtil.mapDescription(mn.desc, mapping);
            mn.name = MappingUtil.map(null, mn.name, mn.desc, mapping);
            if (mn.signature != null) {
                mn.signature = MappingUtil.mapSignature(mn.signature, mapping);
            }
            if (mn.tryCatchBlocks != null) {
                for (Object t : mn.tryCatchBlocks) {
                    ((TryCatchBlockNode)t).type = mapping.getClasses().getOrDefault(((TryCatchBlockNode)t).type, ((TryCatchBlockNode)t).type);
                }
            }
            if (mn.exceptions != null && !mn.exceptions.isEmpty()) {
                ArrayList<String> exceptions = new ArrayList<String>(mn.exceptions.size());
                for (String e : mn.exceptions) {
                    exceptions.add(mapping.getClasses().getOrDefault(e, e));
                }
                mn.exceptions = exceptions;
            }
            if (mn.localVariables == null) continue;
            for (LocalVariableNode l : mn.localVariables) {
                l.desc = MappingUtil.mapDescription(l.desc, mapping);
                if (l.signature == null) continue;
                l.signature = MappingUtil.mapSignature(l.signature, mapping);
            }
        }
    }
}

