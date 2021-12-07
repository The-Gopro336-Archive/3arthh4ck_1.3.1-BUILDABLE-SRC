package org.spongepowered.asm.mixin.injection.invoke.util;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.analysis.Analyzer;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.BasicInterpreter;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.Frame;
import org.spongepowered.asm.lib.tree.analysis.Interpreter;
import org.spongepowered.asm.mixin.injection.invoke.util.InsnFinder;

class InsnFinder$PopAnalyzer
extends Analyzer<BasicValue> {
    protected final AbstractInsnNode node;

    public InsnFinder$PopAnalyzer(AbstractInsnNode node) {
        super(new BasicInterpreter());
        this.node = node;
    }

    @Override
    protected Frame<BasicValue> newFrame(int locals, int stack) {
        return new PopFrame(locals, stack);
    }

    class PopFrame
    extends Frame<BasicValue> {
        private AbstractInsnNode current;
        private InsnFinder.AnalyzerState state;
        private int depth;

        public PopFrame(int locals, int stack) {
            super(locals, stack);
            this.state = InsnFinder.AnalyzerState.SEARCH;
            this.depth = 0;
        }

        @Override
        public void execute(AbstractInsnNode insn, Interpreter<BasicValue> interpreter) throws AnalyzerException {
            this.current = insn;
            super.execute(insn, interpreter);
        }

        @Override
        public void push(BasicValue value) throws IndexOutOfBoundsException {
            if (this.current == PopAnalyzer.this.node && this.state == InsnFinder.AnalyzerState.SEARCH) {
                this.state = InsnFinder.AnalyzerState.ANALYSE;
                ++this.depth;
            } else if (this.state == InsnFinder.AnalyzerState.ANALYSE) {
                ++this.depth;
            }
            super.push(value);
        }

        @Override
        public BasicValue pop() throws IndexOutOfBoundsException {
            if (this.state == InsnFinder.AnalyzerState.ANALYSE && --this.depth == 0) {
                this.state = InsnFinder.AnalyzerState.COMPLETE;
                throw new InsnFinder.AnalysisResultException(this.current);
            }
            return (BasicValue)super.pop();
        }
    }
}
