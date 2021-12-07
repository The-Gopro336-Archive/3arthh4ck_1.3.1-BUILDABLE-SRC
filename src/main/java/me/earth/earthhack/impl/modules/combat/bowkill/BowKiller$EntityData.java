package me.earth.earthhack.impl.modules.combat.bowkill;

public class BowKiller$EntityData {
    private final String id;
    private final long time;

    public BowKiller$EntityData(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return this.id;
    }

    public long getTime() {
        return this.time;
    }

    static String access$000(BowKiller$EntityData x0) {
        return x0.id;
    }
}
