package me.earth.earthhack.impl.managers.thread.lookup;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public abstract class LookUp {
    protected String name;
    protected UUID uuid;
    protected Map<Date, String> names;
    protected Type type;

    public LookUp(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public LookUp(Type type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public abstract void onSuccess();

    public abstract void onFailure();

    public static enum Type {
        NAME,
        UUID,
        HISTORY;

    }
}
