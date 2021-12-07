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

public class EntityPatch
extends ArgumentPatch {
    private int applied = 0;

    public EntityPatch() {
        super("ve", "net.minecraft.entity.Entity", "dead");
    }

    @Override
    protected void applyPatch(ClassNode node) {
        ++this.applied;
        for (FieldNode field : node.fields) {
            if (field.signature != null || !"isDead".equals(field.name) && !"field_70128_L".equals(field.name) && !"F".equals(field.name)) continue;
            Core.LOGGER.info("Made Entity.isDead volatile!");
            field.access |= 0x40;
        }
        this.setFinished(this.applied >= 2);
    }
}

