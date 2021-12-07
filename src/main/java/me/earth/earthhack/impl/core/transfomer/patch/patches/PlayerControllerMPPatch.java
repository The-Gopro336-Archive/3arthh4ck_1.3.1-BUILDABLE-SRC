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

public class PlayerControllerMPPatch
extends ArgumentPatch {
    private int applied = 0;

    public PlayerControllerMPPatch() {
        super("bsa", "net.minecraft.client.multiplayer.PlayerControllerMP", "inventorymp");
    }

    @Override
    protected void applyPatch(ClassNode node) {
        ++this.applied;
        for (FieldNode field : node.fields) {
            if (field.signature != null || !"currentPlayerItem".equals(field.name) && !"field_78777_l".equals(field.name) && !"j".equals(field.name)) continue;
            Core.LOGGER.info("Made PlayerControllerMP.currentPlayerItem volatile!");
            field.access |= 0x40;
        }
        this.setFinished(this.applied >= 2);
    }
}

