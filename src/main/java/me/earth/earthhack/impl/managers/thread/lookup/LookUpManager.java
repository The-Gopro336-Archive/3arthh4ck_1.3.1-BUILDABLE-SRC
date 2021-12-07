package me.earth.earthhack.impl.managers.thread.lookup;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;

public class LookUpManager
implements Globals {
    private static final long CONNECTION_COOLDOWN;
    private volatile ScheduledExecutorService service;
    private final AtomicLong last = new AtomicLong();

    public Future<?> doLookUp(LookUp lookUp) {
        switch (lookUp.type) {
            case NAME: {
                if (lookUp.uuid == null) {
                    lookUp.onFailure();
                    break;
                }
                String name = LookUpUtil.getNameSimple(lookUp.uuid);
                if (name != null) {
                    lookUp.name = name;
                    lookUp.onSuccess();
                    break;
                }
                return this.scheduleLookUp(lookUp);
            }
            case UUID: {
                if (lookUp.name == null) {
                    lookUp.onFailure();
                    break;
                }
                UUID uuid = LookUpUtil.getUUIDSimple(lookUp.name);
                if (uuid != null) {
                    lookUp.uuid = uuid;
                    lookUp.onSuccess();
                    break;
                }
                return this.scheduleLookUp(lookUp);
            }
            case HISTORY: {
                if (lookUp.name == null) {
                    lookUp.onFailure();
                    break;
                }
                UUID id = LookUpUtil.getUUIDSimple(lookUp.name);
                if (id != null) {
                    lookUp.uuid = id;
                }
                return this.scheduleLookUp(lookUp);
            }
        }
        return null;
    }

    private void doBigLookUp(LookUp lookUp) {
        switch (lookUp.type) {
            case NAME: {
                String name = LookUpUtil.getName(lookUp.uuid);
                if (name != null) {
                    lookUp.name = name;
                    lookUp.onSuccess();
                    break;
                }
                lookUp.onFailure();
                break;
            }
            case UUID: {
                UUID uuid = LookUpUtil.getUUID(lookUp.name);
                if (uuid != null) {
                    lookUp.uuid = uuid;
                    lookUp.onSuccess();
                    break;
                }
                lookUp.onFailure();
                break;
            }
            case HISTORY: {
                UUID id = lookUp.uuid;
                if (id == null) {
                    id = LookUpUtil.getUUID(lookUp.name);
                }
                if (id != null) {
                    lookUp.names = LookUpUtil.getNameHistory(id);
                    lookUp.onSuccess();
                    break;
                }
                lookUp.onFailure();
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Future<?> scheduleLookUp(LookUp lookUp) {
        if (this.service == null) {
            LookUpManager lookUpManager = this;
            synchronized (lookUpManager) {
                if (this.service == null) {
                    this.service = ThreadUtil.newDaemonScheduledExecutor("LookUp");
                }
            }
        }
        long t = Math.max(0L, CONNECTION_COOLDOWN - System.currentTimeMillis() + this.last.getAndSet(System.currentTimeMillis()));
        return this.service.schedule(() -> this.doBigLookUp(lookUp), t, TimeUnit.MILLISECONDS);
    }

    static {
        Argument a = DevArguments.getInstance().getArgument("connection");
        Earthhack.getLogger().info("Connection Timeout: " + a.getValue());
        CONNECTION_COOLDOWN = (Long)a.getValue();
    }
}
