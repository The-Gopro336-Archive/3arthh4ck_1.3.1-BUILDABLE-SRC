package org.spongepowered.asm.util.perf;

import org.spongepowered.asm.util.perf.Profiler;

class Profiler$SubSection
extends Profiler.LiveSection {
    private final String baseName;
    private final Profiler.Section root;

    Profiler$SubSection(String name, int cursor, String baseName, Profiler.Section root) {
        super(Profiler.this, name, cursor);
        this.baseName = baseName;
        this.root = root;
    }

    @Override
    Profiler.Section invalidate() {
        this.root.invalidate();
        return super.invalidate();
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void setInfo(String info) {
        this.root.setInfo(info);
        super.setInfo(info);
    }

    @Override
    Profiler.Section getDelegate() {
        return this.root;
    }

    @Override
    Profiler.Section start() {
        this.root.start();
        return super.start();
    }

    @Override
    public Profiler.Section end() {
        this.root.stop();
        return super.end();
    }

    @Override
    public Profiler.Section next(String name) {
        super.stop();
        return this.root.next(name);
    }
}
