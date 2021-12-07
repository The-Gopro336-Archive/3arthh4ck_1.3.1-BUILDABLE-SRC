/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.viewclip;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

public class CameraClip
extends Module {
    public CameraClip() {
        super("CameraClip", Category.Render);
        this.register(new BooleanSetting("Extend", false));
        this.register(new NumberSetting<Double>("Distance", 10.0, 0.0, 50.0));
        this.setData(new SimpleData(this, "Makes the camera clip through blocks in F5."));
    }
}

