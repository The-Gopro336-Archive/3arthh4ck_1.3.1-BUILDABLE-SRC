/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.raytrace;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.player.liquids.LiquidInteract;

public class RayTrace
extends Module {
    private static final ModuleCache<Phase> PHASE = Caches.getModule(Phase.class);
    private static final ModuleCache<LiquidInteract> LIQUID_INTERACT = Caches.getModule(LiquidInteract.class);
    protected final Setting<Boolean> phase = this.register(new BooleanSetting("Phase", true));
    protected final Setting<Boolean> liquids = this.register(new BooleanSetting("Liquids", true));
    protected final Setting<Boolean> liquidCrystalPlace = this.register(new BooleanSetting("Liquid-CrystalPlace", true));
    protected final Setting<Boolean> onlyPhase = this.register(new BooleanSetting("OnlyPhase", true));

    public RayTrace() {
        super("PhaseTrace", Category.Player);
    }

    public boolean isActive() {
        return this.isEnabled() && (this.phase.getValue() != false && PHASE.isEnabled() || LIQUID_INTERACT.isEnabled() && this.liquids.getValue() != false);
    }

    public boolean phaseCheck() {
        return this.onlyPhase.getValue() != false && PHASE.isEnabled() && ((Phase)PHASE.get()).isPhasing();
    }

    public boolean liquidCrystalPlace() {
        return LIQUID_INTERACT.isEnabled() && this.liquidCrystalPlace.getValue() != false && this.isEnabled();
    }
}

