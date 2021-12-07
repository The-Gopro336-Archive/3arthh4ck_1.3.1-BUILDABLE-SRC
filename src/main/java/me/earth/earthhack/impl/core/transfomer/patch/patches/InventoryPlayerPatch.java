package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class InventoryPlayerPatch
extends ArgumentPatch {
    private int applied = 0;

    public InventoryPlayerPatch() {
        super("aea", "net.minecraft.entity.player.InventoryPlayer", "inventory");
    }

    @Override
    protected void applyPatch(ClassNode node) {
        ++this.applied;
        for (FieldNode field : node.fields) {
            if (field.signature != null || !"currentItem".equals(field.name) && !"field_70461_c".equals(field.name) && !"d".equals(field.name)) continue;
            Core.LOGGER.info("Made InventoryPlayer.currentItem volatile!");
            field.access |= 0x40;
        }
        this.setFinished(this.applied >= 2);
    }
}
