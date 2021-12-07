package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

final class ListenerUpdate
extends ModuleListener<BlockHighlight, UpdateEvent> {
    public ListenerUpdate(BlockHighlight module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (ListenerUpdate.mc.objectMouseOver != null) {
            switch (ListenerUpdate.mc.objectMouseOver.typeOfHit) {
                case BLOCK: {
                    IBlockState state;
                    BlockPos pos = ListenerUpdate.mc.objectMouseOver.getBlockPos();
                    if (!ListenerUpdate.mc.world.getWorldBorder().contains(pos) || (state = ListenerUpdate.mc.world.getBlockState(pos)).func_185904_a() == Material.AIR) break;
                    ItemStack stack = state.getBlock().getItem(ListenerUpdate.mc.world, pos, state);
                    ((BlockHighlight)this.module).current = stack.getItem().getItemStackDisplayName(stack);
                    return;
                }
                case ENTITY: {
                    Entity entity = ListenerUpdate.mc.objectMouseOver.entityHit;
                    if (entity == null) break;
                    ((BlockHighlight)this.module).current = EntityNames.getName(entity);
                    return;
                }
            }
        }
        ((BlockHighlight)this.module).current = null;
    }
}
