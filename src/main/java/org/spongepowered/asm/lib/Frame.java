/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;

class Frame {
    static final int DIM = -268435456;
    static final int ARRAY_OF = 0x10000000;
    static final int ELEMENT_OF = -268435456;
    static final int KIND = 0xF000000;
    static final int TOP_IF_LONG_OR_DOUBLE = 0x800000;
    static final int VALUE = 0x7FFFFF;
    static final int BASE_KIND = 0xFF00000;
    static final int BASE_VALUE = 1048575;
    static final int BASE = 0x1000000;
    static final int OBJECT = 0x1700000;
    static final int UNINITIALIZED = 0x1800000;
    private static final int LOCAL = 0x2000000;
    private static final int STACK = 0x3000000;
    static final int TOP = 0x1000000;
    static final int BOOLEAN = 0x1000009;
    static final int BYTE = 0x100000A;
    static final int CHAR = 0x100000B;
    static final int SHORT = 0x100000C;
    static final int INTEGER = 0x1000001;
    static final int FLOAT = 0x1000002;
    static final int DOUBLE = 0x1000003;
    static final int LONG = 0x1000004;
    static final int NULL = 0x1000005;
    static final int UNINITIALIZED_THIS = 0x1000006;
    static final int[] SIZE;
    Label owner;
    int[] inputLocals;
    int[] inputStack;
    private int[] outputLocals;
    private int[] outputStack;
    int outputStackTop;
    private int initializationCount;
    private int[] initializations;

    Frame() {
    }

    final void set(ClassWriter cw, int nLocal, Object[] local, int nStack, Object[] stack) {
        int i = Frame.convert(cw, nLocal, local, this.inputLocals);
        while (i < local.length) {
            this.inputLocals[i++] = 0x1000000;
        }
        int nStackTop = 0;
        for (int j = 0; j < nStack; ++j) {
            if (stack[j] != Opcodes.LONG && stack[j] != Opcodes.DOUBLE) continue;
            ++nStackTop;
        }
        this.inputStack = new int[nStack + nStackTop];
        Frame.convert(cw, nStack, stack, this.inputStack);
        this.outputStackTop = 0;
        this.initializationCount = 0;
    }

    private static int convert(ClassWriter cw, int nInput, Object[] input, int[] output) {
        int i = 0;
        for (int j = 0; j < nInput; ++j) {
            if (input[j] instanceof Integer) {
                output[i++] = 0x1000000 | (Integer)input[j];
                if (input[j] != Opcodes.LONG && input[j] != Opcodes.DOUBLE) continue;
                output[i++] = 0x1000000;
                continue;
            }
            output[i++] = input[j] instanceof String ? Frame.type(cw, Type.getObjectType((String)input[j]).getDescriptor()) : 0x1800000 | cw.addUninitializedType("", ((Label)input[j]).position);
        }
        return i;
    }

    final void set(Frame f) {
        this.inputLocals = f.inputLocals;
        this.inputStack = f.inputStack;
        this.outputLocals = f.outputLocals;
        this.outputStack = f.outputStack;
        this.outputStackTop = f.outputStackTop;
        this.initializationCount = f.initializationCount;
        this.initializations = f.initializations;
    }

    private int get(int local) {
        if (this.outputLocals == null || local >= this.outputLocals.length) {
            return 0x2000000 | local;
        }
        int type = this.outputLocals[local];
        if (type == 0) {
            type = this.outputLocals[local] = 0x2000000 | local;
        }
        return type;
    }

    private void set(int local, int type) {
        int n;
        if (this.outputLocals == null) {
            this.outputLocals = new int[10];
        }
        if (local >= (n = this.outputLocals.length)) {
            int[] t = new int[Math.max(local + 1, 2 * n)];
            System.arraycopy(this.outputLocals, 0, t, 0, n);
            this.outputLocals = t;
        }
        this.outputLocals[local] = type;
    }

    private void push(int type) {
        int n;
        if (this.outputStack == null) {
            this.outputStack = new int[10];
        }
        if (this.outputStackTop >= (n = this.outputStack.length)) {
            int[] t = new int[Math.max(this.outputStackTop + 1, 2 * n)];
            System.arraycopy(this.outputStack, 0, t, 0, n);
            this.outputStack = t;
        }
        this.outputStack[this.outputStackTop++] = type;
        int top = this.owner.inputStackTop + this.outputStackTop;
        if (top > this.owner.outputStackMax) {
            this.owner.outputStackMax = top;
        }
    }

    private void push(ClassWriter cw, String desc) {
        int type = Frame.type(cw, desc);
        if (type != 0) {
            this.push(type);
            if (type == 0x1000004 || type == 0x1000003) {
                this.push(0x1000000);
            }
        }
    }

    private static int type(ClassWriter cw, String desc) {
        int data;
        int index = desc.charAt(0) == '(' ? desc.indexOf(41) + 1 : 0;
        switch (desc.charAt(index)) {
            case 'V': {
                return 0;
            }
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 0x1000001;
            }
            case 'F': {
                return 0x1000002;
            }
            case 'J': {
                return 0x1000004;
            }
            case 'D': {
                return 0x1000003;
            }
            case 'L': {
                String t = desc.substring(index + 1, desc.length() - 1);
                return 0x1700000 | cw.addType(t);
            }
        }
        int dims = index + 1;
        while (desc.charAt(dims) == '[') {
            ++dims;
        }
        switch (desc.charAt(dims)) {
            case 'Z': {
                data = 0x1000009;
                break;
            }
            case 'C': {
                data = 0x100000B;
                break;
            }
            case 'B': {
                data = 0x100000A;
                break;
            }
            case 'S': {
                data = 0x100000C;
                break;
            }
            case 'I': {
                data = 0x1000001;
                break;
            }
            case 'F': {
                data = 0x1000002;
                break;
            }
            case 'J': {
                data = 0x1000004;
                break;
            }
            case 'D': {
                data = 0x1000003;
                break;
            }
            default: {
                String t = desc.substring(dims + 1, desc.length() - 1);
                data = 0x1700000 | cw.addType(t);
            }
        }
        return dims - index << 28 | data;
    }

    private int pop() {
        if (this.outputStackTop > 0) {
            return this.outputStack[--this.outputStackTop];
        }
        return 0x3000000 | -(--this.owner.inputStackTop);
    }

    private void pop(int elements) {
        if (this.outputStackTop >= elements) {
            this.outputStackTop -= elements;
        } else {
            this.owner.inputStackTop -= elements - this.outputStackTop;
            this.outputStackTop = 0;
        }
    }

    private void pop(String desc) {
        char c = desc.charAt(0);
        if (c == '(') {
            this.pop((Type.getArgumentsAndReturnSizes(desc) >> 2) - 1);
        } else if (c == 'J' || c == 'D') {
            this.pop(2);
        } else {
            this.pop(1);
        }
    }

    private void init(int var) {
        int n;
        if (this.initializations == null) {
            this.initializations = new int[2];
        }
        if (this.initializationCount >= (n = this.initializations.length)) {
            int[] t = new int[Math.max(this.initializationCount + 1, 2 * n)];
            System.arraycopy(this.initializations, 0, t, 0, n);
            this.initializations = t;
        }
        this.initializations[this.initializationCount++] = var;
    }

    private int init(ClassWriter cw, int t) {
        int s;
        if (t == 0x1000006) {
            s = 0x1700000 | cw.addType(cw.thisName);
        } else if ((t & 0xFFF00000) == 0x1800000) {
            String type = cw.typeTable[t & 1048575].strVal1;
            s = 0x1700000 | cw.addType(type);
        } else {
            return t;
        }
        for (int j = 0; j < this.initializationCount; ++j) {
            int u = this.initializations[j];
            int dim = u & 0xF0000000;
            int kind = u & 0xF000000;
            if (kind == 0x2000000) {
                u = dim + this.inputLocals[u & 0x7FFFFF];
            } else if (kind == 0x3000000) {
                u = dim + this.inputStack[this.inputStack.length - (u & 0x7FFFFF)];
            }
            if (t != u) continue;
            return s;
        }
        return t;
    }

    final void initInputFrame(ClassWriter cw, int access, Type[] args, int maxLocals) {
        this.inputLocals = new int[maxLocals];
        this.inputStack = new int[0];
        int i = 0;
        if ((access & 8) == 0) {
            this.inputLocals[i++] = (access & 0x80000) == 0 ? 0x1700000 | cw.addType(cw.thisName) : 0x1000006;
        }
        for (int j = 0; j < args.length; ++j) {
            int t = Frame.type(cw, args[j].getDescriptor());
            this.inputLocals[i++] = t;
            if (t != 0x1000004 && t != 0x1000003) continue;
            this.inputLocals[i++] = 0x1000000;
        }
        while (i < maxLocals) {
            this.inputLocals[i++] = 0x1000000;
        }
    }

    void execute(int opcode, int arg, ClassWriter cw, Item item) {
        block0 : switch (opcode) {
            case 0: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 145: 
            case 146: 
            case 147: 
            case 167: 
            case 177: {
                break;
            }
            case 1: {
                this.push(0x1000005);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 16: 
            case 17: 
            case 21: {
                this.push(0x1000001);
                break;
            }
            case 9: 
            case 10: 
            case 22: {
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 11: 
            case 12: 
            case 13: 
            case 23: {
                this.push(0x1000002);
                break;
            }
            case 14: 
            case 15: 
            case 24: {
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 18: {
                switch (item.type) {
                    case 3: {
                        this.push(0x1000001);
                        break block0;
                    }
                    case 5: {
                        this.push(0x1000004);
                        this.push(0x1000000);
                        break block0;
                    }
                    case 4: {
                        this.push(0x1000002);
                        break block0;
                    }
                    case 6: {
                        this.push(0x1000003);
                        this.push(0x1000000);
                        break block0;
                    }
                    case 7: {
                        this.push(0x1700000 | cw.addType("java/lang/Class"));
                        break block0;
                    }
                    case 8: {
                        this.push(0x1700000 | cw.addType("java/lang/String"));
                        break block0;
                    }
                    case 16: {
                        this.push(0x1700000 | cw.addType("java/lang/invoke/MethodType"));
                        break block0;
                    }
                }
                this.push(0x1700000 | cw.addType("java/lang/invoke/MethodHandle"));
                break;
            }
            case 25: {
                this.push(this.get(arg));
                break;
            }
            case 46: 
            case 51: 
            case 52: 
            case 53: {
                this.pop(2);
                this.push(0x1000001);
                break;
            }
            case 47: 
            case 143: {
                this.pop(2);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 48: {
                this.pop(2);
                this.push(0x1000002);
                break;
            }
            case 49: 
            case 138: {
                this.pop(2);
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 50: {
                this.pop(1);
                int t1 = this.pop();
                this.push(-268435456 + t1);
                break;
            }
            case 54: 
            case 56: 
            case 58: {
                int t1 = this.pop();
                this.set(arg, t1);
                if (arg <= 0) break;
                int t2 = this.get(arg - 1);
                if (t2 == 0x1000004 || t2 == 0x1000003) {
                    this.set(arg - 1, 0x1000000);
                    break;
                }
                if ((t2 & 0xF000000) == 0x1000000) break;
                this.set(arg - 1, t2 | 0x800000);
                break;
            }
            case 55: 
            case 57: {
                this.pop(1);
                int t1 = this.pop();
                this.set(arg, t1);
                this.set(arg + 1, 0x1000000);
                if (arg <= 0) break;
                int t2 = this.get(arg - 1);
                if (t2 == 0x1000004 || t2 == 0x1000003) {
                    this.set(arg - 1, 0x1000000);
                    break;
                }
                if ((t2 & 0xF000000) == 0x1000000) break;
                this.set(arg - 1, t2 | 0x800000);
                break;
            }
            case 79: 
            case 81: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                this.pop(3);
                break;
            }
            case 80: 
            case 82: {
                this.pop(4);
                break;
            }
            case 87: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 174: 
            case 176: 
            case 191: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                this.pop(1);
                break;
            }
            case 88: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 173: 
            case 175: {
                this.pop(2);
                break;
            }
            case 89: {
                int t1 = this.pop();
                this.push(t1);
                this.push(t1);
                break;
            }
            case 90: {
                int t1 = this.pop();
                int t2 = this.pop();
                this.push(t1);
                this.push(t2);
                this.push(t1);
                break;
            }
            case 91: {
                int t1 = this.pop();
                int t2 = this.pop();
                int t3 = this.pop();
                this.push(t1);
                this.push(t3);
                this.push(t2);
                this.push(t1);
                break;
            }
            case 92: {
                int t1 = this.pop();
                int t2 = this.pop();
                this.push(t2);
                this.push(t1);
                this.push(t2);
                this.push(t1);
                break;
            }
            case 93: {
                int t1 = this.pop();
                int t2 = this.pop();
                int t3 = this.pop();
                this.push(t2);
                this.push(t1);
                this.push(t3);
                this.push(t2);
                this.push(t1);
                break;
            }
            case 94: {
                int t1 = this.pop();
                int t2 = this.pop();
                int t3 = this.pop();
                int t4 = this.pop();
                this.push(t2);
                this.push(t1);
                this.push(t4);
                this.push(t3);
                this.push(t2);
                this.push(t1);
                break;
            }
            case 95: {
                int t1 = this.pop();
                int t2 = this.pop();
                this.push(t1);
                this.push(t2);
                break;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 136: 
            case 142: 
            case 149: 
            case 150: {
                this.pop(2);
                this.push(0x1000001);
                break;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: {
                this.pop(4);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 137: 
            case 144: {
                this.pop(2);
                this.push(0x1000002);
                break;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                this.pop(4);
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 121: 
            case 123: 
            case 125: {
                this.pop(3);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 132: {
                this.set(arg, 0x1000001);
                break;
            }
            case 133: 
            case 140: {
                this.pop(1);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 134: {
                this.pop(1);
                this.push(0x1000002);
                break;
            }
            case 135: 
            case 141: {
                this.pop(1);
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 139: 
            case 190: 
            case 193: {
                this.pop(1);
                this.push(0x1000001);
                break;
            }
            case 148: 
            case 151: 
            case 152: {
                this.pop(4);
                this.push(0x1000001);
                break;
            }
            case 168: 
            case 169: {
                throw new RuntimeException("JSR/RET are not supported with computeFrames option");
            }
            case 178: {
                this.push(cw, item.strVal3);
                break;
            }
            case 179: {
                this.pop(item.strVal3);
                break;
            }
            case 180: {
                this.pop(1);
                this.push(cw, item.strVal3);
                break;
            }
            case 181: {
                this.pop(item.strVal3);
                this.pop();
                break;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                this.pop(item.strVal3);
                if (opcode != 184) {
                    int t1 = this.pop();
                    if (opcode == 183 && item.strVal2.charAt(0) == '<') {
                        this.init(t1);
                    }
                }
                this.push(cw, item.strVal3);
                break;
            }
            case 186: {
                this.pop(item.strVal2);
                this.push(cw, item.strVal2);
                break;
            }
            case 187: {
                this.push(0x1800000 | cw.addUninitializedType(item.strVal1, arg));
                break;
            }
            case 188: {
                this.pop();
                switch (arg) {
                    case 4: {
                        this.push(0x11000009);
                        break block0;
                    }
                    case 5: {
                        this.push(0x1100000B);
                        break block0;
                    }
                    case 8: {
                        this.push(0x1100000A);
                        break block0;
                    }
                    case 9: {
                        this.push(0x1100000C);
                        break block0;
                    }
                    case 10: {
                        this.push(0x11000001);
                        break block0;
                    }
                    case 6: {
                        this.push(0x11000002);
                        break block0;
                    }
                    case 7: {
                        this.push(0x11000003);
                        break block0;
                    }
                }
                this.push(0x11000004);
                break;
            }
            case 189: {
                String s = item.strVal1;
                this.pop();
                if (s.charAt(0) == '[') {
                    this.push(cw, '[' + s);
                    break;
                }
                this.push(0x11700000 | cw.addType(s));
                break;
            }
            case 192: {
                String s = item.strVal1;
                this.pop();
                if (s.charAt(0) == '[') {
                    this.push(cw, s);
                    break;
                }
                this.push(0x1700000 | cw.addType(s));
                break;
            }
            default: {
                this.pop(arg);
                this.push(cw, item.strVal1);
            }
        }
    }

    final boolean merge(ClassWriter cw, Frame frame, int edge) {
        int kind;
        int dim;
        int t;
        int s;
        int i;
        boolean changed = false;
        int nLocal = this.inputLocals.length;
        int nStack = this.inputStack.length;
        if (frame.inputLocals == null) {
            frame.inputLocals = new int[nLocal];
            changed = true;
        }
        for (i = 0; i < nLocal; ++i) {
            if (this.outputLocals != null && i < this.outputLocals.length) {
                s = this.outputLocals[i];
                if (s == 0) {
                    t = this.inputLocals[i];
                } else {
                    dim = s & 0xF0000000;
                    kind = s & 0xF000000;
                    if (kind == 0x1000000) {
                        t = s;
                    } else {
                        t = kind == 0x2000000 ? dim + this.inputLocals[s & 0x7FFFFF] : dim + this.inputStack[nStack - (s & 0x7FFFFF)];
                        if ((s & 0x800000) != 0 && (t == 0x1000004 || t == 0x1000003)) {
                            t = 0x1000000;
                        }
                    }
                }
            } else {
                t = this.inputLocals[i];
            }
            if (this.initializations != null) {
                t = this.init(cw, t);
            }
            changed |= Frame.merge(cw, t, frame.inputLocals, i);
        }
        if (edge > 0) {
            for (i = 0; i < nLocal; ++i) {
                t = this.inputLocals[i];
                changed |= Frame.merge(cw, t, frame.inputLocals, i);
            }
            if (frame.inputStack == null) {
                frame.inputStack = new int[1];
                changed = true;
            }
            return changed |= Frame.merge(cw, edge, frame.inputStack, 0);
        }
        int nInputStack = this.inputStack.length + this.owner.inputStackTop;
        if (frame.inputStack == null) {
            frame.inputStack = new int[nInputStack + this.outputStackTop];
            changed = true;
        }
        for (i = 0; i < nInputStack; ++i) {
            t = this.inputStack[i];
            if (this.initializations != null) {
                t = this.init(cw, t);
            }
            changed |= Frame.merge(cw, t, frame.inputStack, i);
        }
        for (i = 0; i < this.outputStackTop; ++i) {
            s = this.outputStack[i];
            dim = s & 0xF0000000;
            kind = s & 0xF000000;
            if (kind == 0x1000000) {
                t = s;
            } else {
                t = kind == 0x2000000 ? dim + this.inputLocals[s & 0x7FFFFF] : dim + this.inputStack[nStack - (s & 0x7FFFFF)];
                if ((s & 0x800000) != 0 && (t == 0x1000004 || t == 0x1000003)) {
                    t = 0x1000000;
                }
            }
            if (this.initializations != null) {
                t = this.init(cw, t);
            }
            changed |= Frame.merge(cw, t, frame.inputStack, nInputStack + i);
        }
        return changed;
    }

    private static boolean merge(ClassWriter cw, int t, int[] types, int index) {
        int v;
        int u = types[index];
        if (u == t) {
            return false;
        }
        if ((t & 0xFFFFFFF) == 0x1000005) {
            if (u == 0x1000005) {
                return false;
            }
            t = 0x1000005;
        }
        if (u == 0) {
            types[index] = t;
            return true;
        }
        if ((u & 0xFF00000) == 0x1700000 || (u & 0xF0000000) != 0) {
            if (t == 0x1000005) {
                return false;
            }
            if ((t & 0xFFF00000) == (u & 0xFFF00000)) {
                if ((u & 0xFF00000) == 0x1700000) {
                    v = t & 0xF0000000 | 0x1700000 | cw.getMergedType(t & 0xFFFFF, u & 0xFFFFF);
                } else {
                    int vdim = -268435456 + (u & 0xF0000000);
                    v = vdim | 0x1700000 | cw.addType("java/lang/Object");
                }
            } else if ((t & 0xFF00000) == 0x1700000 || (t & 0xF0000000) != 0) {
                int tdim = ((t & 0xF0000000) == 0 || (t & 0xFF00000) == 0x1700000 ? 0 : -268435456) + (t & 0xF0000000);
                int udim = ((u & 0xF0000000) == 0 || (u & 0xFF00000) == 0x1700000 ? 0 : -268435456) + (u & 0xF0000000);
                v = Math.min(tdim, udim) | 0x1700000 | cw.addType("java/lang/Object");
            } else {
                v = 0x1000000;
            }
        } else {
            v = u == 0x1000005 ? ((t & 0xFF00000) == 0x1700000 || (t & 0xF0000000) != 0 ? t : 0x1000000) : 0x1000000;
        }
        if (u != v) {
            types[index] = v;
            return true;
        }
        return false;
    }

    static {
        int[] b = new int[202];
        String s = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
        for (int i = 0; i < b.length; ++i) {
            b[i] = s.charAt(i) - 69;
        }
        SIZE = b;
    }
}

