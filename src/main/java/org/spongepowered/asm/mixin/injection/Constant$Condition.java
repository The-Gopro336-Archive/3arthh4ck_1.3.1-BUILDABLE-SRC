package org.spongepowered.asm.mixin.injection;

public enum Constant$Condition {
    LESS_THAN_ZERO(155, 156),
    LESS_THAN_OR_EQUAL_TO_ZERO(158, 157),
    GREATER_THAN_OR_EQUAL_TO_ZERO(LESS_THAN_ZERO),
    GREATER_THAN_ZERO(LESS_THAN_OR_EQUAL_TO_ZERO);

    private final int[] opcodes;
    private final Constant$Condition equivalence;

    private Constant$Condition(int ... opcodes) {
        this(null, opcodes);
    }

    private Constant$Condition(Constant$Condition equivalence) {
        this(equivalence, equivalence.opcodes);
    }

    private Constant$Condition(Constant$Condition equivalence, int ... opcodes) {
        this.equivalence = equivalence != null ? equivalence : this;
        this.opcodes = opcodes;
    }

    public Constant$Condition getEquivalentCondition() {
        return this.equivalence;
    }

    public int[] getOpcodes() {
        return this.opcodes;
    }
}
