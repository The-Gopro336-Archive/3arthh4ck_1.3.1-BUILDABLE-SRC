/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.webaura;

import java.util.ArrayList;
import java.util.Comparator;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.webaura.WebAura;
import me.earth.earthhack.impl.util.helpers.blocks.noattack.NoAttackObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

final class ListenerWebAura
extends NoAttackObbyListener<WebAura> {
    public ListenerWebAura(WebAura module) {
        super(module, -10);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        switch (((WebAura)this.module).target.getValue()) {
            case Closest: {
                ((WebAura)this.module).currentTarget = EntityUtil.getClosestEnemy();
                if (((WebAura)this.module).currentTarget == null || ((WebAura)this.module).currentTarget.getDistanceSq((Entity)ListenerWebAura.mc.player) > MathUtil.square(((WebAura)this.module).targetRange.getValue())) {
                    return result.setValid(false);
                }
                return this.trap((Entity)((WebAura)this.module).currentTarget, result);
            }
            case Untrapped: {
                ((WebAura)this.module).currentTarget = null;
                ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
                for (EntityPlayer player : ListenerWebAura.mc.world.playerEntities) {
                    BlockPos pos;
                    if (player == null || EntityUtil.isDead((Entity)player) || Managers.FRIENDS.contains(player) || player.equals((Object)ListenerWebAura.mc.player) || ListenerWebAura.mc.world.getBlockState(pos = new BlockPos((Entity)player)).getBlock() == Blocks.WEB || ListenerWebAura.mc.world.getBlockState(pos.up()).getBlock() == Blocks.WEB || !(ListenerWebAura.mc.player.getDistanceSq((Entity)player) < MathUtil.square(((WebAura)this.module).targetRange.getValue()))) continue;
                    players.add(player);
                }
                players.sort(Comparator.comparingDouble(p -> p.getDistanceSq((Entity)ListenerWebAura.mc.player)));
                for (EntityPlayer player : players) {
                    this.trap((Entity)player, result);
                }
                return result;
            }
        }
        return result.setValid(false);
    }

    @Override
    protected int getSlot() {
        return InventoryUtil.findHotbarBlock(Blocks.WEB, new Block[0]);
    }

    @Override
    protected String getDisableString() {
        return "Disabled, no Webs.";
    }

    private TargetResult trap(Entity entity, TargetResult result) {
        BlockPos pos = new BlockPos(entity);
        BlockPos up = pos.up();
        IBlockState state = ListenerWebAura.mc.world.getBlockState(pos);
        IBlockState upState = ListenerWebAura.mc.world.getBlockState(up);
        if (state.getBlock() == Blocks.WEB || upState.getBlock() == Blocks.WEB) {
            return result;
        }
        if (state.getMaterial().isReplaceable()) {
            result.getTargets().add(pos);
        } else if (upState.getMaterial().isReplaceable()) {
            result.getTargets().add(up);
        }
        return result;
    }
}

