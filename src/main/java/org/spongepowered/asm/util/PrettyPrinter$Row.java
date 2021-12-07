package org.spongepowered.asm.util;

import org.spongepowered.asm.util.PrettyPrinter;

class PrettyPrinter$Row
implements PrettyPrinter.IVariableWidthEntry {
    final PrettyPrinter.Table table;
    final String[] args;

    public PrettyPrinter$Row(PrettyPrinter.Table table, Object ... args) {
        this.table = table.grow(args.length);
        this.args = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            this.args[i] = args[i].toString();
            this.table.columns.get(i).setMinWidth(this.args[i].length());
        }
    }

    public String toString() {
        Object[] args = new Object[this.table.columns.size()];
        for (int col = 0; col < args.length; ++col) {
            PrettyPrinter.Column column = this.table.columns.get(col);
            args[col] = col >= this.args.length ? "" : (this.args[col].length() > column.getMaxWidth() ? this.args[col].substring(0, column.getMaxWidth()) : this.args[col]);
        }
        return String.format(this.table.format, args);
    }

    @Override
    public int getWidth() {
        return this.toString().length();
    }
}
