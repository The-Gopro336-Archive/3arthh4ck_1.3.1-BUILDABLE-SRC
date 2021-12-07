package me.earth.earthhack.impl.modules.render.breadcrumbs.util;

import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.Vec3d;

public class Trace$TracePos {
    private final Vec3d pos;
    private final StopWatch stopWatch = new StopWatch();
    private long time;

    public Trace$TracePos(Vec3d pos) {
        this.pos = pos;
        this.stopWatch.reset();
    }

    public Trace$TracePos(Vec3d pos, long time) {
        this.pos = pos;
        this.stopWatch.reset();
        this.time = time;
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public boolean shouldRemoveTrace() {
        return this.stopWatch.passed(2000L);
    }

    public long getTime() {
        return this.time;
    }
}
