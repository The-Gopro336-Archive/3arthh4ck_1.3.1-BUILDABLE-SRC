/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.mixin.injection.invoke.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.analysis.Analyzer;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.BasicInterpreter;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.Frame;
import org.spongepowered.asm.lib.tree.analysis.Interpreter;
import org.spongepowered.asm.mixin.injection.struct.Target;

public class InsnFinder {
    private static final Logger logger = LogManager.getLogger((String)"mixin");

    public AbstractInsnNode findPopInsn(Target target, AbstractInsnNode node) {
        try {
            new PopAnalyzer(node).analyze(target.classNode.name, target.method);
        }
        catch (AnalyzerException ex) {
            if (ex.getCause() instanceof AnalysisResultException) {
                return ((AnalysisResultException)ex.getCause()).getResult();
            }
            logger.catching((Throwable)ex);
        }
        return null;
    }

    static class PopAnalyzer
    extends Analyzer<BasicValue> {
        protected final AbstractInsnNode node;

        public PopAnalyzer(AbstractInsnNode node) {
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
            private AnalyzerState state;
            private int depth;

            public PopFrame(int locals, int stack) {
                super(locals, stack);
                this.state = AnalyzerState.SEARCH;
                this.depth = 0;
            }

            @Override
            public void execute(AbstractInsnNode insn, Interpreter<BasicValue> interpreter) throws AnalyzerException {
                this.current = insn;
                super.execute(insn, interpreter);
            }

            @Override
            public void push(BasicValue value) throws IndexOutOfBoundsException {
                if (this.current == PopAnalyzer.this.node && this.state == AnalyzerState.SEARCH) {
                    this.state = AnalyzerState.ANALYSE;
                    ++this.depth;
                } else if (this.state == AnalyzerState.ANALYSE) {
                    ++this.depth;
                }
                super.push(value);
            }

            @Override
            public BasicValue pop() throws IndexOutOfBoundsException {
                if (this.state == AnalyzerState.ANALYSE && --this.depth == 0) {
                    this.state = AnalyzerState.COMPLETE;
                    throw new AnalysisResultException(this.current);
                }
                return (BasicValue)super.pop();
            }
        }
    }

    static enum AnalyzerState {
        SEARCH,
        ANALYSE,
        COMPLETE;

    }

    static class AnalysisResultException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private AbstractInsnNode result;

        public AnalysisResultException(AbstractInsnNode popNode) {
            this.result = popNode;
        }

        public AbstractInsnNode getResult() {
            return this.result;
        }
    }
}

