package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.modules.combat.autocrystal.AbstractCalculation;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalDataMotion;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.IBreakHelper;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class CalculationMotion
extends AbstractCalculation<CrystalDataMotion> {
    public CalculationMotion(AutoCrystal module, List<Entity> entities, List<EntityPlayer> players) {
        super(module, entities, players, new BlockPos[0]);
    }

    @Override
    protected IBreakHelper<CrystalDataMotion> getBreakHelper() {
        return this.module.breakHelperMotion;
    }

    @Override
    protected boolean evaluate(BreakData<CrystalDataMotion> breakData) {
        BreakValidity validity;
        boolean slowReset = false;
        if (this.breakData.getAntiTotem() != null && (validity = HelperUtil.isValid(this.module, this.breakData.getAntiTotem())) != BreakValidity.INVALID) {
            this.attack(this.breakData.getAntiTotem(), validity);
            this.module.breakTimer.reset(this.module.breakDelay.getValue().intValue());
            this.module.antiTotemHelper.setTarget(null);
            this.module.antiTotemHelper.setTargetPos(null);
        } else {
            int packets = !this.module.rotate.getValue().noRotate(ACRotate.Break) ? 1 : this.module.packets.getValue();
            CrystalData firstRotation = null;
            ArrayList<CrystalDataMotion> valids = new ArrayList<CrystalDataMotion>(packets);
            for (CrystalDataMotion data : this.breakData.getData()) {
                if (data.getTiming() == CrystalDataMotion.Timing.NONE) continue;
                validity = this.isValid(this.module, data);
                if (validity == BreakValidity.VALID && valids.size() < packets) {
                    valids.add(data);
                    continue;
                }
                if (validity != BreakValidity.ROTATIONS || data.getTiming() != CrystalDataMotion.Timing.BOTH && data.getTiming() != CrystalDataMotion.Timing.POST || firstRotation != null) continue;
                firstRotation = data;
            }
            int slowDelay = this.module.slowBreakDelay.getValue();
            float slow = this.module.slowBreakDamage.getValue().floatValue();
            if (valids.isEmpty()) {
                if (firstRotation != null && (this.module.shouldDanger() || !(slowReset = firstRotation.getDamage() <= slow) || this.module.breakTimer.passed(slowDelay))) {
                    this.attack(firstRotation.getCrystal(), BreakValidity.ROTATIONS);
                }
            } else {
                slowReset = !this.module.shouldDanger();
                for (CrystalDataMotion v : valids) {
                    boolean high;
                    boolean bl = high = v.getDamage() > this.module.slowBreakDamage.getValue().floatValue();
                    if (!high && !this.module.breakTimer.passed(this.module.slowBreakDelay.getValue().intValue())) continue;
                    boolean bl2 = slowReset = slowReset && !high;
                    if (v.getTiming() == CrystalDataMotion.Timing.POST || v.getTiming() == CrystalDataMotion.Timing.BOTH && v.getPostSelf() < v.getSelfDmg()) {
                        this.attackPost(v.getCrystal());
                        continue;
                    }
                    this.attack(v.getCrystal(), BreakValidity.VALID);
                }
            }
        }
        if (this.attacking) {
            this.module.breakTimer.reset(slowReset ? (long)this.module.slowBreakDelay.getValue().intValue() : (long)this.module.breakDelay.getValue().intValue());
        }
        return this.rotating && !this.module.rotate.getValue().noRotate(ACRotate.Place);
    }

    protected void attackPost(Entity entity) {
        this.attacking = true;
        this.scheduling = true;
        this.rotating = !this.module.rotate.getValue().noRotate(ACRotate.Break);
        MutableWrapper<Boolean> attacked = new MutableWrapper<Boolean>(false);
        Runnable post = this.module.rotationHelper.post(entity, attacked);
        this.module.post.add(post);
    }

    private BreakValidity isValid(AutoCrystal module, CrystalDataMotion dataMotion) {
        Entity crystal = dataMotion.getCrystal();
        if (module.existed.getValue() != 0) {
            double d = System.currentTimeMillis() - ((IEntity)((Object)crystal)).getTimeStamp();
            double d2 = module.pingExisted.getValue() != false ? (double)ServerUtil.getPingNoPingSpoof() / 2.0 : 0.0;
            if (d + d2 < (double)module.existed.getValue().intValue()) {
                return BreakValidity.INVALID;
            }
        }
        if (module.rotate.getValue().noRotate(ACRotate.Break) || RotationUtil.isLegit(crystal, crystal) && module.positionHistoryHelper.arePreviousRotationsLegit(crystal, module.rotationTicks.getValue(), true)) {
            return BreakValidity.VALID;
        }
        return BreakValidity.ROTATIONS;
    }
}
