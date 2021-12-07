package org.spongepowered.asm.mixin.transformer;

import com.google.gson.annotations.SerializedName;
import java.util.List;

class MixinConfig$InjectorOptions {
    @SerializedName(value="defaultRequire")
    int defaultRequireValue = 0;
    @SerializedName(value="defaultGroup")
    String defaultGroup = "default";
    @SerializedName(value="injectionPoints")
    List<String> injectionPoints;
    @SerializedName(value="maxShiftBy")
    int maxShiftBy = 0;

    MixinConfig$InjectorOptions() {
    }
}
