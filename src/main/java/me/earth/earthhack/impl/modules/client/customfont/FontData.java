package me.earth.earthhack.impl.modules.client.customfont;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;

final class FontData
extends DefaultData<FontMod> {
    protected FontData(FontMod module) {
        super(module);
        this.register("Font", "The Font.");
        this.register("FontStyle", "Plain, Bold, Italic, or All which is Bold and Italic.");
        this.register("FontSize", "Size the Font will be rendered in.");
        this.register("AntiAlias", "Smooths the edges of the font.");
        this.register("Metrics", "Takes sub pixel accuracy into account.");
        this.register("Shadow", "Font will be rendered with shadow regardless. If enabled the shadow will have less offset.");
        this.register("Fonts", "Click this setting to get a list of available fonts.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Handles the CustomFont. If enabled, HUD, Nametags and Gui will be rendered in the custom font.";
    }
}
