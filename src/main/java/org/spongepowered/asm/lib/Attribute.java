package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Label;

public class Attribute {
    public final String type;
    byte[] value;
    Attribute next;

    protected Attribute(String type) {
        this.type = type;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected Label[] getLabels() {
        return null;
    }

    protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
        Attribute attr = new Attribute(this.type);
        attr.value = new byte[len];
        System.arraycopy(cr.b, off, attr.value, 0, len);
        return attr;
    }

    protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        v.data = this.value;
        v.length = this.value.length;
        return v;
    }

    final int getCount() {
        int count = 0;
        Attribute attr = this;
        while (attr != null) {
            ++count;
            attr = attr.next;
        }
        return count;
    }

    final int getSize(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        Attribute attr = this;
        int size = 0;
        while (attr != null) {
            cw.newUTF8(attr.type);
            size += attr.write((ClassWriter)cw, (byte[])code, (int)len, (int)maxStack, (int)maxLocals).length + 6;
            attr = attr.next;
        }
        return size;
    }

    final void put(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals, ByteVector out) {
        Attribute attr = this;
        while (attr != null) {
            ByteVector b = attr.write(cw, code, len, maxStack, maxLocals);
            out.putShort(cw.newUTF8(attr.type)).putInt(b.length);
            out.putByteArray(b.data, 0, b.length);
            attr = attr.next;
        }
    }
}
