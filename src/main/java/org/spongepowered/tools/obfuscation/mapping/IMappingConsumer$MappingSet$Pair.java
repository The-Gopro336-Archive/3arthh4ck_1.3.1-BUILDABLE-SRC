package org.spongepowered.tools.obfuscation.mapping;

import com.google.common.base.Objects;
import org.spongepowered.asm.obfuscation.mapping.IMapping;

public class IMappingConsumer$MappingSet$Pair<TMapping extends IMapping<TMapping>> {
    public final TMapping from;
    public final TMapping to;

    public IMappingConsumer$MappingSet$Pair(TMapping from, TMapping to) {
        this.from = from;
        this.to = to;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IMappingConsumer$MappingSet$Pair)) {
            return false;
        }
        IMappingConsumer$MappingSet$Pair other = (IMappingConsumer$MappingSet$Pair)obj;
        return Objects.equal(this.from, other.from) && Objects.equal(this.to, other.to);
    }

    public int hashCode() {
        return Objects.hashCode(this.from, this.to);
    }

    public String toString() {
        return String.format("%s -> %s", this.from, this.to);
    }
}
