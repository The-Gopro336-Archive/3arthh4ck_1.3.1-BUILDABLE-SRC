package org.spongepowered.asm.mixin.injection.invoke.arg;

public abstract class Args {
    protected final Object[] values;

    protected Args(Object[] values) {
        this.values = values;
    }

    public int size() {
        return this.values.length;
    }

    public <T> T get(int index) {
        return (T)this.values[index];
    }

    public abstract <T> void set(int var1, T var2);

    public abstract void setAll(Object ... var1);
}
