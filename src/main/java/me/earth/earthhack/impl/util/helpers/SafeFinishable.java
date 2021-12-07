package me.earth.earthhack.impl.util.helpers;

import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.impl.util.helpers.Finishable;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

public abstract class SafeFinishable
extends Finishable
implements SafeRunnable {
    public SafeFinishable() {
        this(new AtomicBoolean());
    }

    public SafeFinishable(AtomicBoolean finished) {
        super(finished);
    }

    @Override
    public void run() {
        try {
            this.runSafely();
        }
        catch (Throwable t) {
            this.handle(t);
        }
        finally {
            this.setFinished(true);
        }
    }

    @Override
    @Deprecated
    protected void execute() {
    }
}
