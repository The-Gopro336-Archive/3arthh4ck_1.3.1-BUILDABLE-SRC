package me.earth.earthhack.impl.modules.combat.holefiller;

import java.util.ArrayList;
import java.util.Comparator;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.managers.thread.holes.HoleRunnable;
import me.earth.earthhack.impl.managers.thread.holes.IHoleManager;
import me.earth.earthhack.impl.modules.combat.holefiller.HoleFiller;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerObby
extends ObbyListener<HoleFiller> {
    private boolean wasRunning;

    public ListenerObby(HoleFiller module) {
        super(module, 10);
    }

    @Override
    public void onModuleToggle() {
        super.onModuleToggle();
        this.wasRunning = false;
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        ((HoleFiller)this.module).target = null;
        if ((this.wasRunning || !((HoleFiller)this.module).requireTarget.getValue().booleanValue()) && ((HoleFiller)this.module).disable.getValue() != 0 && ((HoleFiller)this.module).disableTimer.passed(((HoleFiller)this.module).disable.getValue().intValue())) {
            ((HoleFiller)this.module).disable();
            return;
        }
        if (((HoleFiller)this.module).requireTarget.getValue().booleanValue()) {
            ((HoleFiller)this.module).target = EntityUtil.getClosestEnemy();
            if (((HoleFiller)this.module).target == null || ((HoleFiller)this.module).target.getDistanceSq(ListenerObby.mc.player) > MathUtil.square(((HoleFiller)this.module).targetRange.getValue()) || ((HoleFiller)this.module).waitForHoleLeave.getValue().booleanValue() && (HoleUtil.is1x1(PositionUtil.getPosition(((HoleFiller)this.module).target))[0] || HoleUtil.is2x1(PositionUtil.getPosition(((HoleFiller)this.module).target), false) || HoleUtil.is2x1(PositionUtil.getPosition(((HoleFiller)this.module).target), false))) {
                ((HoleFiller)this.module).waiting = true;
                return;
            }
        }
        ((HoleFiller)this.module).waiting = false;
        if (!this.wasRunning) {
            ((HoleFiller)this.module).disableTimer.reset();
        }
        this.wasRunning = true;
        super.invoke(event);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        BlockPos playerPos;
        if (((HoleFiller)this.module).calcTimer.passed(((HoleFiller)this.module).calcDelay.getValue().intValue())) {
            HoleRunnable runnable = new HoleRunnable((IHoleManager)this.module, (HoleObserver)this.module);
            runnable.run();
            ((HoleFiller)this.module).calcTimer.reset();
        }
        ArrayList<BlockPos> targets = new ArrayList<BlockPos>(((HoleFiller)this.module).safes.size() + ((HoleFiller)this.module).unsafes.size() + ((HoleFiller)this.module).longs.size() + ((HoleFiller)this.module).bigs.size());
        targets.addAll(((HoleFiller)this.module).safes);
        targets.addAll(((HoleFiller)this.module).unsafes);
        if (((HoleFiller)this.module).longHoles.getValue().booleanValue()) {
            targets.addAll(((HoleFiller)this.module).longs);
        }
        if (((HoleFiller)this.module).bigHoles.getValue().booleanValue()) {
            targets.addAll(((HoleFiller)this.module).bigs);
        }
        if (!(HoleUtil.isHole(playerPos = PositionUtil.getPosition(), false)[0] || HoleUtil.is2x1(playerPos) || HoleUtil.is2x2(playerPos) || ((HoleFiller)this.module).safety.getValue().booleanValue() && Managers.SAFETY.isSafe())) {
            EntityPlayer p2 = RotationUtil.getRotationPlayer();
            Vec3d v = p2.getPositionVector().addVector(p2.motionX, p2.motionY, p2.motionZ);
            targets.removeIf(pos -> pos.distanceSq(v.x, v.y, v.z) < MathUtil.square(((HoleFiller)this.module).minSelf.getValue()));
            targets.sort(Comparator.comparingDouble(pos -> -BlockUtil.getDistanceSq(pos)));
        }
        ((HoleFiller)this.module).target = EntityUtil.getClosestEnemy();
        if (((HoleFiller)this.module).target != null) {
            targets.removeIf(p -> BlockUtil.getDistanceSq(((HoleFiller)this.module).target, p) > MathUtil.square(((HoleFiller)this.module).targetDistance.getValue()));
            targets.sort(Comparator.comparingDouble(p -> BlockUtil.getDistanceSq(((HoleFiller)this.module).target, p)));
        }
        result.setTargets(targets);
        return result;
    }
}
