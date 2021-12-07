/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.norender.NoRender;

final class NoRenderData
extends DefaultData<NoRender> {
    public NoRenderData(NoRender module) {
        super(module);
        this.register(module.fire, "Doesn't render the fire overlay when you are on fire.");
        this.register(module.portal, "Doesn't render Portals while you are inside them.");
        this.register(module.pumpkin, "Doesn't render the pumpkin overlay.");
        this.register(module.totemPops, "Doesn't render the totempop animation when you pop one.");
        this.register(module.nausea, "Doesn't render Nausea effects.");
        this.register(module.hurtCam, "Makes the camera not shake when you take damage.");
        this.register(module.noWeather, "Doesn't render Rain.");
        this.register(module.barriers, "Allows you to see Barriers when on.");
        this.register(module.skyLight, "Prevents SkyLight lag exploits.");
        this.register(module.noFog, "Doesn't render fog.");
        this.register(module.blocks, "Allows you to look through blocks while you are inside them.");
        this.register(module.advancements, "Doesn't render Advancements.");
        this.register(module.critParticles, "Doesn't render attack particles.");
        this.register(module.dynamicFov, "Removes the dynamic fov, when you sprint or fly for example.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Don't render annoying overlays.";
    }
}

