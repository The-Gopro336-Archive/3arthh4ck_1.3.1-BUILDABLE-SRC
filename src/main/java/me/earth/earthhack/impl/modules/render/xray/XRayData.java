package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.xray.XRay;

final class XRayData
extends DefaultData<XRay> {
    public XRayData(XRay module) {
        super(module);
        this.register(module.mode, "Simple is just Opacity with an Opacity value of 0.");
        this.register(module.soft, "Makes the world not flicker when the module is toggled or blocks are added.");
        this.register(module.opacity, "The Opacity value for Mode-Opacity.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Allows you to see through the world.";
    }
}
