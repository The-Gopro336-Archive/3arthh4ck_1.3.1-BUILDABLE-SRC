package org.spongepowered.asm.service;

import java.io.IOException;
import org.spongepowered.asm.lib.tree.ClassNode;

public interface IClassBytecodeProvider {
    public byte[] getClassBytes(String var1, String var2) throws IOException;

    public byte[] getClassBytes(String var1, boolean var2) throws ClassNotFoundException, IOException;

    public ClassNode getClassNode(String var1) throws ClassNotFoundException, IOException;
}
