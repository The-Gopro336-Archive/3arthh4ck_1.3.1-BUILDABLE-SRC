package me.earth.earthhack.impl.util.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Finishable
implements Runnable {
    private final AtomicBoolean finished;

    public Finishable() {
        this(new AtomicBoolean());
    }

    public Finishable(AtomicBoolean finished) {
        this.finished = finished;
    }

    @Override
    public void run() {
        try {
            this.execute();
        }
        finally {
            this.setFinished(true);
        }
    }

    protected abstract void execute();

    public void setFinished(boolean finished) {
        this.finished.set(finished);
    }

    public boolean isFinished() {
        return this.finished.get();
    }
}
