package me.earth.earthhack.impl.modules.combat.autotrap;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;

final class ListenerAutoTrap
extends ObbyListener<AutoTrap> {
    protected static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);

    public ListenerAutoTrap(AutoTrap module) {
        super(module, -999);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        return ((AutoTrap)this.module).getTargets(result);
    }

    @Override
    protected boolean updatePlaced() {
        if (FREECAM.isEnabled() && !((AutoTrap)this.module).freeCam.getValue().booleanValue()) {
            ((AutoTrap)this.module).disable();
            return true;
        }
        return super.updatePlaced();
    }
}
