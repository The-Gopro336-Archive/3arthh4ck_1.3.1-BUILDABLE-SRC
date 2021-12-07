package org.spongepowered.asm.util;

class PrettyPrinter$CentredText {
    private final Object centred;

    public PrettyPrinter$CentredText(Object centred) {
        this.centred = centred;
    }

    public String toString() {
        String text = this.centred.toString();
        return String.format("%" + ((PrettyPrinter.this.width - text.length()) / 2 + text.length()) + "s", text);
    }
}
