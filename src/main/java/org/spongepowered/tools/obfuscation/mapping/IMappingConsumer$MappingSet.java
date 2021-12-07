package org.spongepowered.tools.obfuscation.mapping;

import com.google.common.base.Objects;
import java.util.LinkedHashSet;
import org.spongepowered.asm.obfuscation.mapping.IMapping;

public class IMappingConsumer$MappingSet<TMapping extends IMapping<TMapping>>
extends LinkedHashSet<Pair<TMapping>> {
    private static final long serialVersionUID = 1L;

    public static class Pair<TMapping extends IMapping<TMapping>> {
        public final TMapping from;
        public final TMapping to;

        public Pair(TMapping from, TMapping to) {
            this.from = from;
            this.to = to;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Pair)) {
                return false;
            }
            Pair other = (Pair)obj;
            return Objects.equal(this.from, other.from) && Objects.equal(this.to, other.to);
        }

        public int hashCode() {
            return Objects.hashCode(this.from, this.to);
        }

        public String toString() {
            return String.format("%s -> %s", this.from, this.to);
        }
    }
}
