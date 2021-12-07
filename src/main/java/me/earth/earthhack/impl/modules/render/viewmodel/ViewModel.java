package me.earth.earthhack.impl.modules.render.viewmodel;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.viewmodel.ViewModelData;
import net.minecraft.util.EnumHand;

public class ViewModel
extends Module {
    public static final float[] DEFAULT_SCALE = new float[]{1.0f, 1.0f, 1.0f};
    public static final float[] DEFAULT_TRANSLATION = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    protected final Setting<Float> offX = this.register(new NumberSetting<Float>("OffHand-X", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> offY = this.register(new NumberSetting<Float>("OffHand-Y", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> mainX = this.register(new NumberSetting<Float>("MainHand-X", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> mainY = this.register(new NumberSetting<Float>("MainHand-Y", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> xScale = this.register(new NumberSetting<Float>("X-Scale", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> yScale = this.register(new NumberSetting<Float>("Y-Scale", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> zScale = this.register(new NumberSetting<Float>("Z-Scale", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> angleTranslate = this.register(new NumberSetting<Float>("Angle-Translate", Float.valueOf(0.0f), Float.valueOf(-360.0f), Float.valueOf(360.0f)));
    protected final Setting<Float> xTranslate = this.register(new NumberSetting<Float>("X-Translate", Float.valueOf(1.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> yTranslate = this.register(new NumberSetting<Float>("Y-Translate", Float.valueOf(1.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> zTranslate = this.register(new NumberSetting<Float>("Z-Translate", Float.valueOf(1.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));

    public ViewModel() {
        super("ViewModel", Category.Render);
        this.setData(new ViewModelData(this));
    }

    public float getX(EnumHand hand) {
        if (!this.isEnabled()) {
            return 0.0f;
        }
        return hand == EnumHand.MAIN_HAND ? this.mainX.getValue().floatValue() : this.offX.getValue().floatValue();
    }

    public float getY(EnumHand hand) {
        if (!this.isEnabled()) {
            return 0.0f;
        }
        return hand == EnumHand.MAIN_HAND ? this.mainY.getValue().floatValue() : this.offY.getValue().floatValue();
    }

    public float[] getScale() {
        if (!this.isEnabled()) {
            return DEFAULT_SCALE;
        }
        return new float[]{this.xScale.getValue().floatValue(), this.yScale.getValue().floatValue(), this.zScale.getValue().floatValue()};
    }

    public float[] getTranslation() {
        if (!this.isEnabled()) {
            return DEFAULT_TRANSLATION;
        }
        return new float[]{this.angleTranslate.getValue().floatValue(), this.xTranslate.getValue().floatValue(), this.yTranslate.getValue().floatValue(), this.zTranslate.getValue().floatValue()};
    }
}
