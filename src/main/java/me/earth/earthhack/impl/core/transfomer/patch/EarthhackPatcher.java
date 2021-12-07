/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 */
package me.earth.earthhack.impl.core.transfomer.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.Patch;
import me.earth.earthhack.impl.core.transfomer.PatchManager;
import me.earth.earthhack.impl.core.util.AsmUtil;
import org.objectweb.asm.tree.ClassNode;

public class EarthhackPatcher
implements PatchManager {
    private static final EarthhackPatcher INSTANCE = new EarthhackPatcher();
    private final List<Patch> patches = new ArrayList<Patch>();

    private EarthhackPatcher() {
    }

    public static EarthhackPatcher getInstance() {
        return INSTANCE;
    }

    @Override
    public void addPatch(Patch patch) {
        this.patches.add(patch);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        List<Patch> found = this.patches.stream().filter(p -> p.getName().equals(name) || p.getTransformedName().equals(transformedName)).collect(Collectors.toList());
        if (!found.isEmpty()) {
            Core.LOGGER.info("Found " + found.size() + " patch" + (found.size() == 1 ? "" : "es") + " for: " + name + " : " + transformedName);
            ClassNode cn = AsmUtil.read(bytes, new int[0]);
            found.forEach(p -> p.apply(cn));
            this.patches.removeIf(Patch::isFinished);
            return AsmUtil.writeNoSuperClass(cn, 1, 2);
        }
        return bytes;
    }
}

