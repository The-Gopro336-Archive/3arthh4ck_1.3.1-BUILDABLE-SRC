package org.spongepowered.asm.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.MixinService;

final class MixinEnvironment$Side$3
extends MixinEnvironment.Side {
    @Override
    protected boolean detect() {
        String sideName = MixinService.getService().getSideName();
        return "SERVER".equals(sideName) || "DEDICATEDSERVER".equals(sideName);
    }
}
