package org.spongepowered.asm.util;

import org.spongepowered.asm.util.PrettyPrinter;

class PrettyPrinter$KeyValue
implements PrettyPrinter.IVariableWidthEntry {
    private final String key;
    private final Object value;

    public PrettyPrinter$KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return String.format(PrettyPrinter.this.kvFormat, this.key, this.value);
    }

    @Override
    public int getWidth() {
        return this.toString().length();
    }
}
