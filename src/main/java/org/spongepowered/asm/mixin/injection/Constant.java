package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Constant {
    public boolean nullValue() default false;

    public int intValue() default 0;

    public float floatValue() default 0.0f;

    public long longValue() default 0L;

    public double doubleValue() default 0.0;

    public String stringValue() default "";

    public Class<?> classValue() default Object.class;

    public int ordinal() default -1;

    public String slice() default "";

    public Condition[] expandZeroConditions() default {};

    public boolean log() default false;

    public static enum Condition {
        LESS_THAN_ZERO(155, 156),
        LESS_THAN_OR_EQUAL_TO_ZERO(158, 157),
        GREATER_THAN_OR_EQUAL_TO_ZERO(LESS_THAN_ZERO),
        GREATER_THAN_ZERO(LESS_THAN_OR_EQUAL_TO_ZERO);

        private final int[] opcodes;
        private final Condition equivalence;

        private Condition(int ... opcodes) {
            this(null, opcodes);
        }

        private Condition(Condition equivalence) {
            this(equivalence, equivalence.opcodes);
        }

        private Condition(Condition equivalence, int ... opcodes) {
            this.equivalence = equivalence != null ? equivalence : this;
            this.opcodes = opcodes;
        }

        public Condition getEquivalentCondition() {
            return this.equivalence;
        }

        public int[] getOpcodes() {
            return this.opcodes;
        }
    }
}
