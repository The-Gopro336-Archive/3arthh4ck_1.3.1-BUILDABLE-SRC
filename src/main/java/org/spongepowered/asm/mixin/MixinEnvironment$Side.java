package org.spongepowered.asm.mixin;

import org.spongepowered.asm.service.MixinService;

public enum MixinEnvironment$Side {
    UNKNOWN{

        @Override
        protected boolean detect() {
            return false;
        }
    }
    ,
    CLIENT{

        @Override
        protected boolean detect() {
            String sideName = MixinService.getService().getSideName();
            return "CLIENT".equals(sideName);
        }
    }
    ,
    SERVER{

        @Override
        protected boolean detect() {
            String sideName = MixinService.getService().getSideName();
            return "SERVER".equals(sideName) || "DEDICATEDSERVER".equals(sideName);
        }
    };


    protected abstract boolean detect();
}
