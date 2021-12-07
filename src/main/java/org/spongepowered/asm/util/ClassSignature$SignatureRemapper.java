package org.spongepowered.asm.util;

import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.lib.signature.SignatureWriter;
import org.spongepowered.asm.util.ClassSignature;

class ClassSignature$SignatureRemapper
extends SignatureWriter {
    private final Set<String> localTypeVars = new HashSet<String>();

    ClassSignature$SignatureRemapper() {
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        this.localTypeVars.add(name);
        super.visitFormalTypeParameter(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        ClassSignature.TypeVar typeVar;
        if (!this.localTypeVars.contains(name) && (typeVar = ClassSignature.this.getTypeVar(name)) != null) {
            super.visitTypeVariable(typeVar.toString());
            return;
        }
        super.visitTypeVariable(name);
    }
}
