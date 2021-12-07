package org.spongepowered.asm.lib.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.analysis.Analyzer;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.BasicVerifier;
import org.spongepowered.asm.lib.util.CheckClassAdapter;

class CheckMethodAdapter$1
extends MethodNode {
    final MethodVisitor val$cmv;

    CheckMethodAdapter$1(int api, int access, String name, String desc, String signature, String[] exceptions, MethodVisitor methodVisitor) {
        this.val$cmv = methodVisitor;
        super(api, access, name, desc, signature, exceptions);
    }

    public void visitEnd() {
        Analyzer<BasicValue> a = new Analyzer<BasicValue>(new BasicVerifier());
        try {
            a.analyze("dummy", this);
        }
        catch (Exception e) {
            if (e instanceof IndexOutOfBoundsException && this.maxLocals == 0 && this.maxStack == 0) {
                throw new RuntimeException("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
            }
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            CheckClassAdapter.printAnalyzerResult(this, a, pw);
            pw.close();
            throw new RuntimeException(e.getMessage() + ' ' + sw.toString());
        }
        this.accept(this.val$cmv);
    }
}
