package me.earth.earthhack.impl.modules.combat.legswitch;

import java.util.Map;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

final class LegConstellation {
    public final Map<BlockPos, IBlockState> states;
    public final EntityPlayer player;
    public final BlockPos targetPos;
    public final BlockPos playerPos;
    public final BlockPos firstPos;
    public final BlockPos secondPos;
    public boolean firstNeedsObby;
    public boolean secondNeedsObby;
    public boolean invalid;

    public LegConstellation(EntityPlayer player, BlockPos targetPos, BlockPos playerPos, BlockPos firstPos, BlockPos secondPos, Map<BlockPos, IBlockState> states, boolean firstNeedsObby, boolean secondNeedsObby) {
        this.player = player;
        this.targetPos = targetPos;
        this.playerPos = playerPos;
        this.firstPos = firstPos;
        this.secondPos = secondPos;
        this.states = states;
        this.firstNeedsObby = firstNeedsObby;
        this.secondNeedsObby = secondNeedsObby;
    }

    public boolean isValid(LegSwitch legSwitch, EntityPlayer self, IBlockAccess access) {
        if (this.invalid || EntityUtil.isDead(this.player)) {
            return false;
        }
        if (!PositionUtil.getPosition(this.player).equals(this.playerPos) || !access.getBlockState(this.playerPos).func_185904_a().isReplaceable()) {
            return false;
        }
        if (!legSwitch.checkPos(this.firstPos) || !legSwitch.checkPos(this.secondPos)) {
            return false;
        }
        for (Map.Entry<BlockPos, IBlockState> entry : this.states.entrySet()) {
            if (access.getBlockState(entry.getKey()).equals(entry.getValue())) continue;
            return false;
        }
        float damage = DamageUtil.calculate(this.firstPos, (EntityLivingBase)self);
        if ((double)damage > (double)EntityUtil.getHealth(self) + 0.5 || damage > legSwitch.maxSelfDamage.getValue().floatValue()) {
            return false;
        }
        damage = DamageUtil.calculate(this.secondPos, (EntityLivingBase)self);
        return !((double)damage > (double)EntityUtil.getHealth(self) + 0.5) && !(damage > legSwitch.maxSelfDamage.getValue().floatValue());
    }

    public void add(BlockPos pos, IBlockState state) {
        this.states.put(pos, state);
    }
}
