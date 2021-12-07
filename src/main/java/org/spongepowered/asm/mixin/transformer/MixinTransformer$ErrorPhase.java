package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

enum MixinTransformer$ErrorPhase {
    PREPARE{

        @Override
        IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler handler, String context, InvalidMixinException ex, IMixinInfo mixin, IMixinErrorHandler.ErrorAction action) {
            try {
                return handler.onPrepareError(mixin.getConfig(), ex, mixin, action);
            }
            catch (AbstractMethodError ame) {
                return action;
            }
        }

        @Override
        protected String getContext(IMixinInfo mixin, String context) {
            return String.format("preparing %s in %s", mixin.getName(), context);
        }
    }
    ,
    APPLY{

        @Override
        IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler handler, String context, InvalidMixinException ex, IMixinInfo mixin, IMixinErrorHandler.ErrorAction action) {
            try {
                return handler.onApplyError(context, ex, mixin, action);
            }
            catch (AbstractMethodError ame) {
                return action;
            }
        }

        @Override
        protected String getContext(IMixinInfo mixin, String context) {
            return String.format("%s -> %s", mixin, context);
        }
    };

    private final String text = this.name().toLowerCase();

    abstract IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler var1, String var2, InvalidMixinException var3, IMixinInfo var4, IMixinErrorHandler.ErrorAction var5);

    protected abstract String getContext(IMixinInfo var1, String var2);

    public String getLogMessage(String context, InvalidMixinException ex, IMixinInfo mixin) {
        return String.format("Mixin %s failed %s: %s %s", this.text, this.getContext(mixin, context), ex.getClass().getName(), ex.getMessage());
    }

    public String getErrorMessage(IMixinInfo mixin, IMixinConfig config, MixinEnvironment.Phase phase) {
        return String.format("Mixin [%s] from phase [%s] in config [%s] FAILED during %s", mixin, phase, config, this.name());
    }
}
