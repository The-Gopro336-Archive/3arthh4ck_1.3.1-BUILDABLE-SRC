package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;

final class AnnotationWriter
extends AnnotationVisitor {
    private final ClassWriter cw;
    private int size;
    private final boolean named;
    private final ByteVector bv;
    private final ByteVector parent;
    private final int offset;
    AnnotationWriter next;
    AnnotationWriter prev;

    AnnotationWriter(ClassWriter cw, boolean named, ByteVector bv, ByteVector parent, int offset) {
        super(327680);
        this.cw = cw;
        this.named = named;
        this.bv = bv;
        this.parent = parent;
        this.offset = offset;
    }

    public void visit(String name, Object value) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        if (value instanceof String) {
            this.bv.put12(115, this.cw.newUTF8((String)value));
        } else if (value instanceof Byte) {
            this.bv.put12(66, this.cw.newInteger((int)((Byte)value).byteValue()).index);
        } else if (value instanceof Boolean) {
            int v = (Boolean)value != false ? 1 : 0;
            this.bv.put12(90, this.cw.newInteger((int)v).index);
        } else if (value instanceof Character) {
            this.bv.put12(67, this.cw.newInteger((int)((Character)value).charValue()).index);
        } else if (value instanceof Short) {
            this.bv.put12(83, this.cw.newInteger((int)((Short)value).shortValue()).index);
        } else if (value instanceof Type) {
            this.bv.put12(99, this.cw.newUTF8(((Type)value).getDescriptor()));
        } else if (value instanceof byte[]) {
            byte[] v = (byte[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(66, this.cw.newInteger((int)v[i]).index);
            }
        } else if (value instanceof boolean[]) {
            boolean[] v = (boolean[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(90, this.cw.newInteger((int)(v[i] ? 1 : 0)).index);
            }
        } else if (value instanceof short[]) {
            short[] v = (short[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(83, this.cw.newInteger((int)v[i]).index);
            }
        } else if (value instanceof char[]) {
            char[] v = (char[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(67, this.cw.newInteger((int)v[i]).index);
            }
        } else if (value instanceof int[]) {
            int[] v = (int[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(73, this.cw.newInteger((int)v[i]).index);
            }
        } else if (value instanceof long[]) {
            long[] v = (long[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(74, this.cw.newLong((long)v[i]).index);
            }
        } else if (value instanceof float[]) {
            float[] v = (float[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(70, this.cw.newFloat((float)v[i]).index);
            }
        } else if (value instanceof double[]) {
            double[] v = (double[])value;
            this.bv.put12(91, v.length);
            for (int i = 0; i < v.length; ++i) {
                this.bv.put12(68, this.cw.newDouble((double)v[i]).index);
            }
        } else {
            Item i = this.cw.newConstItem(value);
            this.bv.put12(".s.IFJDCS".charAt(i.type), i.index);
        }
    }

    public void visitEnum(String name, String desc, String value) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        this.bv.put12(101, this.cw.newUTF8(desc)).putShort(this.cw.newUTF8(value));
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        this.bv.put12(64, this.cw.newUTF8(desc)).putShort(0);
        return new AnnotationWriter(this.cw, true, this.bv, this.bv, this.bv.length - 2);
    }

    public AnnotationVisitor visitArray(String name) {
        ++this.size;
        if (this.named) {
            this.bv.putShort(this.cw.newUTF8(name));
        }
        this.bv.put12(91, 0);
        return new AnnotationWriter(this.cw, false, this.bv, this.bv, this.bv.length - 2);
    }

    public void visitEnd() {
        if (this.parent != null) {
            byte[] data = this.parent.data;
            data[this.offset] = (byte)(this.size >>> 8);
            data[this.offset + 1] = (byte)this.size;
        }
    }

    int getSize() {
        int size = 0;
        AnnotationWriter aw = this;
        while (aw != null) {
            size += aw.bv.length;
            aw = aw.next;
        }
        return size;
    }

    void put(ByteVector out) {
        int n = 0;
        int size = 2;
        AnnotationWriter aw = this;
        AnnotationWriter last = null;
        while (aw != null) {
            ++n;
            size += aw.bv.length;
            aw.visitEnd();
            aw.prev = last;
            last = aw;
            aw = aw.next;
        }
        out.putInt(size);
        out.putShort(n);
        aw = last;
        while (aw != null) {
            out.putByteArray(aw.bv.data, 0, aw.bv.length);
            aw = aw.prev;
        }
    }

    static void put(AnnotationWriter[] panns, int off, ByteVector out) {
        int i;
        int size = 1 + 2 * (panns.length - off);
        for (i = off; i < panns.length; ++i) {
            size += panns[i] == null ? 0 : panns[i].getSize();
        }
        out.putInt(size).putByte(panns.length - off);
        for (i = off; i < panns.length; ++i) {
            AnnotationWriter aw = panns[i];
            AnnotationWriter last = null;
            int n = 0;
            while (aw != null) {
                ++n;
                aw.visitEnd();
                aw.prev = last;
                last = aw;
                aw = aw.next;
            }
            out.putShort(n);
            aw = last;
            while (aw != null) {
                out.putByteArray(aw.bv.data, 0, aw.bv.length);
                aw = aw.prev;
            }
        }
    }

    static void putTarget(int typeRef, TypePath typePath, ByteVector out) {
        switch (typeRef >>> 24) {
            case 0: 
            case 1: 
            case 22: {
                out.putShort(typeRef >>> 16);
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                out.putByte(typeRef >>> 24);
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                out.putInt(typeRef);
                break;
            }
            default: {
                out.put12(typeRef >>> 24, (typeRef & 0xFFFF00) >> 8);
            }
        }
        if (typePath == null) {
            out.putByte(0);
        } else {
            int length = typePath.b[typePath.offset] * 2 + 1;
            out.putByteArray(typePath.b, typePath.offset, length);
        }
    }
}
