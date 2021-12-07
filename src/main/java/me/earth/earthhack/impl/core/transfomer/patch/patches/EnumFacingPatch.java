/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldNode
 */
package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class EnumFacingPatch
extends ArgumentPatch {
    private int applied = 0;

    public EnumFacingPatch() {
        super("fa", "net.minecraft.util.EnumFacing", "vanilla");
    }

    @Override
    protected void applyPatch(ClassNode node) {
        ++this.applied;
        for (FieldNode field : node.fields) {
            if (field.signature != null || !"o".equals(field.name) || (8 & field.access) != 8) continue;
            Core.LOGGER.info("Made EnumFacing.HORIZONTALS public!");
            field.access &= 0xFFFFFFFD;
            field.access &= 0xFFFFFFFB;
            field.access |= 1;
        }
        this.setFinished(this.applied >= 2);
    }
}

