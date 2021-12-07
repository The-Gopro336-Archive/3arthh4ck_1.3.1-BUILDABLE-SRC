package org.spongepowered.asm.util;

import com.google.common.base.Strings;
import org.spongepowered.asm.util.PrettyPrinter;

class PrettyPrinter$HorizontalRule
implements PrettyPrinter.ISpecialEntry {
    private final char[] hrChars;

    public PrettyPrinter$HorizontalRule(char ... hrChars) {
        this.hrChars = hrChars;
    }

    public String toString() {
        return Strings.repeat(new String(this.hrChars), PrettyPrinter.this.width + 2);
    }
}
