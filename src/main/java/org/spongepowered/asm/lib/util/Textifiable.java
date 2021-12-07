package org.spongepowered.asm.lib.util;

import java.util.Map;
import org.spongepowered.asm.lib.Label;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Textifiable {
    public void textify(StringBuffer var1, Map<Label, String> var2);
}
