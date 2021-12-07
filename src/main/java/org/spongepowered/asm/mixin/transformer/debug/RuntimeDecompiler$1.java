package org.spongepowered.asm.mixin.transformer.debug;

import java.io.File;
import java.io.IOException;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

class RuntimeDecompiler$1
implements IBytecodeProvider {
    private byte[] byteCode;

    RuntimeDecompiler$1() {
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        if (this.byteCode == null) {
            this.byteCode = InterpreterUtil.getBytes(new File(externalPath));
        }
        return this.byteCode;
    }
}
