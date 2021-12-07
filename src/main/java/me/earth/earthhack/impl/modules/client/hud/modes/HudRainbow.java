package me.earth.earthhack.impl.modules.client.hud.modes;

public enum HudRainbow {
    None(""),
    Horizontal("\u00a7+"),
    Vertical("\u00a7-"),
    Static("");

    private final String color;

    private HudRainbow(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}
