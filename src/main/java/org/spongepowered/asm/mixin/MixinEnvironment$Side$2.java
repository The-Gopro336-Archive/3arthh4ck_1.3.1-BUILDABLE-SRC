package org.spongepowered.asm.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.MixinService;

final class MixinEnvironment$Side$2
extends MixinEnvironment.Side {
    @Override
    protected boolean detect() {
        String sideName = MixinService.getService().getSideName();
        return "CLIENT".equals(sideName);
    }
}
