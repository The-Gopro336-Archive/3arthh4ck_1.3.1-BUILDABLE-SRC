package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.holeesp.HoleESP;

final class HoleESPData
extends DefaultData<HoleESP> {
    public HoleESPData(HoleESP module) {
        super(module);
        this.register(module.range, "Range that holes should be rendered in. High ranges (30+) mean more workload for the CPU and might slow down modules like HoleFiller.");
        this.register(module.holes, "Amount of normal holes to render.");
        this.register(module.safeHole, "Amount of safe holes (bedrock only) to render.");
        this.register(module.wide, "Amount of 2x1 holes to render.");
        this.register(module.big, "Amount of 2x2 holes to render.");
        this.register(module.fov, "Only renders Holes inside your fov.");
        this.register(module.own, "Render the hole you are currently standing in.");
        this.register(module.height, "Height of the ESP for safe holes. A value of 0 means a flat square will be rendered at the bottom of the hole.");
        this.register(module.unsafeHeight, "Height of the ESP for unsafe holes. A value of 0 means a flat square will be rendered at the bottom of the hole.");
        this.register(module.wideHeight, "Height of the ESP for 2x1 holes. A value of 0 means a flat square will be rendered at the bottom of the hole.");
        this.register(module.bigHeight, "Height of the ESP for 2x2 holes. A value of 0 means a flat square will be rendered at the bottom of the hole.");
        this.register(module.unsafeColor, "The color unsafe holes should be rendered in.");
        this.register(module.safeColor, "The color safe holes should be rendered in.");
        this.register(module.wideColor, "The color 2x1 holes should be rendered in.");
        this.register(module.bigColor, "The color 2x2 holes should be rendered in.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Highlights Holes around you.";
    }
}
