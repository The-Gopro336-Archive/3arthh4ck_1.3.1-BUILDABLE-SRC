package org.spongepowered.asm.util;

import org.spongepowered.asm.util.PrettyPrinter;

class PrettyPrinter$Column {
    private final PrettyPrinter.Table table;
    private PrettyPrinter.Alignment align = PrettyPrinter.Alignment.LEFT;
    private int minWidth = 1;
    private int maxWidth = Integer.MAX_VALUE;
    private int size = 0;
    private String title = "";
    private String format = "%s";

    PrettyPrinter$Column(PrettyPrinter.Table table) {
        this.table = table;
    }

    PrettyPrinter$Column(PrettyPrinter.Table table, String title) {
        this(table);
        this.title = title;
        this.minWidth = title.length();
        this.updateFormat();
    }

    PrettyPrinter$Column(PrettyPrinter.Table table, PrettyPrinter.Alignment align, int size, String title) {
        this(table, title);
        this.align = align;
        this.size = size;
    }

    void setAlignment(PrettyPrinter.Alignment align) {
        this.align = align;
        this.updateFormat();
    }

    void setWidth(int width) {
        if (width > this.size) {
            this.size = width;
            this.updateFormat();
        }
    }

    void setMinWidth(int width) {
        if (width > this.minWidth) {
            this.minWidth = width;
            this.updateFormat();
        }
    }

    void setMaxWidth(int width) {
        this.size = Math.min(this.size, this.maxWidth);
        this.maxWidth = Math.max(1, width);
        this.updateFormat();
    }

    void setTitle(String title) {
        this.title = title;
        this.setWidth(title.length());
    }

    private void updateFormat() {
        int width = Math.min(this.maxWidth, this.size == 0 ? this.minWidth : this.size);
        this.format = "%" + (this.align == PrettyPrinter.Alignment.RIGHT ? "" : "-") + width + "s";
        this.table.updateFormat();
    }

    int getMaxWidth() {
        return this.maxWidth;
    }

    String getTitle() {
        return this.title;
    }

    String getFormat() {
        return this.format;
    }

    public String toString() {
        if (this.title.length() > this.maxWidth) {
            return this.title.substring(0, this.maxWidth);
        }
        return this.title;
    }
}
