package org.spongepowered.asm.mixin.transformer;

import com.google.gson.annotations.SerializedName;

class MixinConfig$OverwriteOptions {
    @SerializedName(value="conformVisibility")
    boolean conformAccessModifiers;
    @SerializedName(value="requireAnnotations")
    boolean requireOverwriteAnnotations;

    MixinConfig$OverwriteOptions() {
    }
}
