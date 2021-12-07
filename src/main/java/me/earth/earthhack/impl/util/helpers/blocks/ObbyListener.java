package me.earth.earthhack.impl.util.helpers.blocks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class ObbyListener<T extends ObbyListenerModule<?>>
extends ModuleListener<T, MotionUpdateEvent> {
    public final Map<BlockPos, Long> placed = new HashMap<BlockPos, Long>();
    public List<BlockPos> targets = new LinkedList<BlockPos>();

    public ObbyListener(T module, int priority) {
        super(module, MotionUpdateEvent.class, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            this.pre(event);
        } else {
            this.post(event);
        }
    }

    protected abstract TargetResult getTargets(TargetResult var1);

    public void onModuleToggle() {
        this.placed.clear();
        this.targets = new LinkedList<BlockPos>();
    }

    protected void pre(MotionUpdateEvent event) {
        ((ObbyListenerModule)this.module).rotations = null;
        ((ObbyListenerModule)this.module).blocksPlaced = 0;
        if (this.update() && !this.attackCrystalFirst()) {
            this.placeTargets();
        }
        if (this.rotateCheck()) {
            if (((ObbyListenerModule)this.module).rotations != null) {
                this.setRotations(((ObbyListenerModule)this.module).rotations, event);
            }
        } else {
            this.execute();
        }
    }

    protected boolean rotateCheck() {
        return ((ObbyListenerModule)this.module).rotate.getValue() != Rotate.None;
    }

    protected void placeTargets() {
        for (BlockPos pos : this.targets) {
            if (!this.placed.containsKey(pos) && ObbyModule.HELPER.getBlockState(pos).func_185904_a().isReplaceable() && ((ObbyListenerModule)this.module).placeBlock(pos)) break;
        }
    }

    protected boolean attackCrystalFirst() {
        boolean hasPlaced = false;
        Optional<BlockPos> crystalPos = this.targets.stream().filter(pos -> !ObbyListener.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB((BlockPos)pos)).isEmpty() && ObbyListener.mc.world.getBlockState((BlockPos)pos).func_185904_a().isReplaceable()).findFirst();
        if (crystalPos.isPresent()) {
            hasPlaced = ((ObbyListenerModule)this.module).placeBlock(crystalPos.get());
        }
        return hasPlaced;
    }

    protected boolean update() {
        if (this.updatePlaced()) {
            return false;
        }
        ((ObbyListenerModule)this.module).slot = this.getSlot();
        if (((ObbyListenerModule)this.module).slot == -1) {
            this.disableModule();
            return false;
        }
        if (this.hasTimerNotPassed()) {
            return false;
        }
        TargetResult result = this.getTargets(new TargetResult());
        this.targets = result.getTargets();
        return result.isValid();
    }

    protected boolean hasTimerNotPassed() {
        return !((ObbyListenerModule)this.module).timer.passed(((ObbyListenerModule)this.module).getDelay());
    }

    public void addCallback(BlockPos pos) {
        Managers.BLOCKS.addCallback(pos, s -> mc.addScheduledTask(() -> this.placed.remove(pos)));
        this.placed.put(pos, System.currentTimeMillis());
    }

    protected void disableModule() {
        ModuleUtil.disableRed((Module)this.module, this.getDisableString());
    }

    protected boolean updatePlaced() {
        this.placed.entrySet().removeIf(entry -> System.currentTimeMillis() - (Long)entry.getValue() >= (long)((ObbyListenerModule)this.module).confirm.getValue().intValue());
        return false;
    }

    protected int getSlot() {
        return InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
    }

    protected String getDisableString() {
        return "Disabled, no Obsidian.";
    }

    protected void post(MotionUpdateEvent event) {
        this.execute();
    }

    protected void setRotations(float[] rotations, MotionUpdateEvent event) {
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }

    protected void execute() {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, ((ObbyListenerModule)this.module)::execute);
    }
}
