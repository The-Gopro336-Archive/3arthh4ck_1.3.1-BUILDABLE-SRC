package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AbstractCalculation;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.Calculation;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ThreadHelper
implements Globals {
    private final StopWatch threadTimer = new StopWatch();
    private final Setting<Boolean> multiThread;
    private final Setting<Integer> threadDelay;
    private final Setting<RotationThread> rotationThread;
    private final Setting<ACRotate> rotate;
    private final AutoCrystal module;
    private volatile AbstractCalculation<?> currentCalc;

    public ThreadHelper(AutoCrystal module, Setting<Boolean> multiThread, Setting<Integer> threadDelay, Setting<RotationThread> rotationThread, Setting<ACRotate> rotate) {
        this.module = module;
        this.multiThread = multiThread;
        this.threadDelay = threadDelay;
        this.rotationThread = rotationThread;
        this.rotate = rotate;
    }

    public synchronized void start(AbstractCalculation<?> calculation, boolean multiThread) {
        if (!this.module.isPingBypass() && this.threadTimer.passed(this.threadDelay.getValue().intValue()) && (this.currentCalc == null || this.currentCalc.isFinished())) {
            this.currentCalc = calculation;
            this.execute(this.currentCalc, multiThread);
        }
    }

    public synchronized void startThread(BlockPos ... blackList) {
        if (ThreadHelper.mc.world == null || ThreadHelper.mc.player == null || this.module.isPingBypass() || !this.threadTimer.passed(this.threadDelay.getValue().intValue()) || this.currentCalc != null && !this.currentCalc.isFinished()) {
            return;
        }
        if (mc.isCallingFromMinecraftThread()) {
            this.startThread(new ArrayList<Entity>(ThreadHelper.mc.world.loadedEntityList), new ArrayList<EntityPlayer>(ThreadHelper.mc.world.playerEntities), blackList);
        } else {
            this.startThread(Managers.ENTITIES.getEntities(), Managers.ENTITIES.getPlayers(), blackList);
        }
    }

    public synchronized void startThread(boolean breakOnly, boolean noBreak, BlockPos ... blackList) {
        if (ThreadHelper.mc.world == null || ThreadHelper.mc.player == null || this.module.isPingBypass() || !this.threadTimer.passed(this.threadDelay.getValue().intValue()) || this.currentCalc != null && !this.currentCalc.isFinished()) {
            return;
        }
        if (mc.isCallingFromMinecraftThread()) {
            this.startThread(new ArrayList<Entity>(ThreadHelper.mc.world.loadedEntityList), new ArrayList<EntityPlayer>(ThreadHelper.mc.world.playerEntities), breakOnly, noBreak, blackList);
        } else {
            this.startThread(Managers.ENTITIES.getEntities(), Managers.ENTITIES.getPlayers(), breakOnly, noBreak, blackList);
        }
    }

    private void startThread(List<Entity> entities, List<EntityPlayer> players, boolean breakOnly, boolean noBreak, BlockPos ... blackList) {
        this.currentCalc = new Calculation(this.module, entities, players, breakOnly, noBreak, blackList);
        this.execute(this.currentCalc, this.multiThread.getValue());
    }

    private void startThread(List<Entity> entities, List<EntityPlayer> players, BlockPos ... blackList) {
        this.currentCalc = new Calculation(this.module, entities, players, blackList);
        this.execute(this.currentCalc, this.multiThread.getValue());
    }

    private void execute(AbstractCalculation<?> calculation, boolean multiThread) {
        if (multiThread) {
            Managers.THREAD.submitRunnable(calculation);
            this.threadTimer.reset();
        } else {
            this.threadTimer.reset();
            calculation.run();
        }
    }

    public void schedulePacket(PacketEvent.Receive<?> event) {
        if (this.multiThread.getValue().booleanValue() && (this.rotate.getValue() == ACRotate.None || this.rotationThread.getValue() != RotationThread.Predict)) {
            event.addPostEvent(() -> this.startThread(new BlockPos[0]));
        }
    }

    public AbstractCalculation<?> getCurrentCalc() {
        return this.currentCalc;
    }

    public void reset() {
        this.currentCalc = null;
    }
}
