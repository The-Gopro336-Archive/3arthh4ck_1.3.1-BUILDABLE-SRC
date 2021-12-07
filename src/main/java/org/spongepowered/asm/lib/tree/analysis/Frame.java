/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib.tree.analysis;

import java.util.ArrayList;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.IincInsnNode;
import org.spongepowered.asm.lib.tree.InvokeDynamicInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MultiANewArrayInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.Interpreter;
import org.spongepowered.asm.lib.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Frame<V extends Value> {
    private V returnValue;
    private V[] values;
    private int locals;
    private int top;

    public Frame(int nLocals, int nStack) {
        this.values = new Value[nLocals + nStack];
        this.locals = nLocals;
    }

    public Frame(Frame<? extends V> src) {
        this(src.locals, src.values.length - src.locals);
        this.init(src);
    }

    public Frame<V> init(Frame<? extends V> src) {
        this.returnValue = src.returnValue;
        System.arraycopy(src.values, 0, this.values, 0, this.values.length);
        this.top = src.top;
        return this;
    }

    public void setReturn(V v) {
        this.returnValue = v;
    }

    public int getLocals() {
        return this.locals;
    }

    public int getMaxStackSize() {
        return this.values.length - this.locals;
    }

    public V getLocal(int i) throws IndexOutOfBoundsException {
        if (i >= this.locals) {
            throw new IndexOutOfBoundsException("Trying to access an inexistant local variable");
        }
        return this.values[i];
    }

    public void setLocal(int i, V value) throws IndexOutOfBoundsException {
        if (i >= this.locals) {
            throw new IndexOutOfBoundsException("Trying to access an inexistant local variable " + i);
        }
        this.values[i] = value;
    }

    public int getStackSize() {
        return this.top;
    }

    public V getStack(int i) throws IndexOutOfBoundsException {
        return this.values[i + this.locals];
    }

    public void clearStack() {
        this.top = 0;
    }

    public V pop() throws IndexOutOfBoundsException {
        if (this.top == 0) {
            throw new IndexOutOfBoundsException("Cannot pop operand off an empty stack.");
        }
        return this.values[--this.top + this.locals];
    }

    public void push(V value) throws IndexOutOfBoundsException {
        if (this.top + this.locals >= this.values.length) {
            throw new IndexOutOfBoundsException("Insufficient maximum stack size.");
        }
        this.values[this.top++ + this.locals] = value;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void execute(AbstractInsnNode insn, Interpreter<V> interpreter) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case 0: {
                return;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: {
                this.push(interpreter.copyOperation(insn, this.getLocal(((VarInsnNode)insn).var)));
                return;
            }
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: {
                V value2 = this.pop();
                V value1 = this.pop();
                this.push(interpreter.binaryOperation(insn, value1, value2));
                return;
            }
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: {
                V local;
                Object value1 = interpreter.copyOperation(insn, this.pop());
                int var = ((VarInsnNode)insn).var;
                this.setLocal(var, value1);
                if (value1.getSize() == 2) {
                    this.setLocal(var + 1, interpreter.newValue(null));
                }
                if (var <= 0 || (local = this.getLocal(var - 1)) == null || local.getSize() != 2) return;
                this.setLocal(var - 1, interpreter.newValue(null));
                return;
            }
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                V value3 = this.pop();
                V value2 = this.pop();
                V value1 = this.pop();
                interpreter.ternaryOperation(insn, value1, value2, value3);
                return;
            }
            case 87: {
                if (this.pop().getSize() != 2) return;
                throw new AnalyzerException(insn, "Illegal use of POP");
            }
            case 88: {
                if (this.pop().getSize() != 1 || this.pop().getSize() == 1) return;
                throw new AnalyzerException(insn, "Illegal use of POP2");
            }
            case 89: {
                V value1 = this.pop();
                if (value1.getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of DUP");
                }
                this.push(value1);
                this.push(interpreter.copyOperation(insn, value1));
                return;
            }
            case 90: {
                V value1 = this.pop();
                V value2 = this.pop();
                if (value1.getSize() != 1 || value2.getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of DUP_X1");
                }
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value2);
                this.push(value1);
                return;
            }
            case 91: {
                V value1 = this.pop();
                if (value1.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP_X2");
                V value2 = this.pop();
                if (value2.getSize() == 1) {
                    V value3 = this.pop();
                    if (value3.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP_X2");
                    this.push(interpreter.copyOperation(insn, value1));
                    this.push(value3);
                    this.push(value2);
                    this.push(value1);
                    return;
                }
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value2);
                this.push(value1);
                return;
            }
            case 92: {
                V value1 = this.pop();
                if (value1.getSize() == 1) {
                    V value2 = this.pop();
                    if (value2.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2");
                    this.push(value2);
                    this.push(value1);
                    this.push(interpreter.copyOperation(insn, value2));
                    this.push(interpreter.copyOperation(insn, value1));
                    return;
                }
                this.push(value1);
                this.push(interpreter.copyOperation(insn, value1));
                return;
            }
            case 93: {
                V value1 = this.pop();
                if (value1.getSize() == 1) {
                    V value3;
                    V value2 = this.pop();
                    if (value2.getSize() != 1 || (value3 = this.pop()).getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X1");
                    this.push(interpreter.copyOperation(insn, value2));
                    this.push(interpreter.copyOperation(insn, value1));
                    this.push(value3);
                    this.push(value2);
                    this.push(value1);
                    return;
                }
                V value2 = this.pop();
                if (value2.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X1");
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value2);
                this.push(value1);
                return;
            }
            case 94: {
                V value1 = this.pop();
                if (value1.getSize() == 1) {
                    V value2 = this.pop();
                    if (value2.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X2");
                    V value3 = this.pop();
                    if (value3.getSize() == 1) {
                        V value4 = this.pop();
                        if (value4.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X2");
                        this.push(interpreter.copyOperation(insn, value2));
                        this.push(interpreter.copyOperation(insn, value1));
                        this.push(value4);
                        this.push(value3);
                        this.push(value2);
                        this.push(value1);
                        return;
                    }
                    this.push(interpreter.copyOperation(insn, value2));
                    this.push(interpreter.copyOperation(insn, value1));
                    this.push(value3);
                    this.push(value2);
                    this.push(value1);
                    return;
                }
                V value2 = this.pop();
                if (value2.getSize() == 1) {
                    V value3 = this.pop();
                    if (value3.getSize() != 1) throw new AnalyzerException(insn, "Illegal use of DUP2_X2");
                    this.push(interpreter.copyOperation(insn, value1));
                    this.push(value3);
                    this.push(value2);
                    this.push(value1);
                    return;
                }
                this.push(interpreter.copyOperation(insn, value1));
                this.push(value2);
                this.push(value1);
                return;
            }
            case 95: {
                V value2 = this.pop();
                V value1 = this.pop();
                if (value1.getSize() != 1 || value2.getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of SWAP");
                }
                this.push(interpreter.copyOperation(insn, value2));
                this.push(interpreter.copyOperation(insn, value1));
                return;
            }
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 113: 
            case 114: 
            case 115: {
                V value2 = this.pop();
                V value1 = this.pop();
                this.push(interpreter.binaryOperation(insn, value1, value2));
                return;
            }
            case 116: 
            case 117: 
            case 118: 
            case 119: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 127: 
            case 128: 
            case 129: 
            case 130: 
            case 131: {
                V value2 = this.pop();
                V value1 = this.pop();
                this.push(interpreter.binaryOperation(insn, value1, value2));
                return;
            }
            case 132: {
                int var = ((IincInsnNode)insn).var;
                this.setLocal(var, interpreter.unaryOperation(insn, this.getLocal(var)));
                return;
            }
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: {
                V value2 = this.pop();
                V value1 = this.pop();
                this.push(interpreter.binaryOperation(insn, value1, value2));
                return;
            }
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: {
                V value2 = this.pop();
                V value1 = this.pop();
                interpreter.binaryOperation(insn, value1, value2);
                return;
            }
            case 167: {
                return;
            }
            case 168: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 169: {
                return;
            }
            case 170: 
            case 171: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: {
                V value1 = this.pop();
                interpreter.unaryOperation(insn, value1);
                interpreter.returnOperation(insn, value1, this.returnValue);
                return;
            }
            case 177: {
                if (this.returnValue == null) return;
                throw new AnalyzerException(insn, "Incompatible return type");
            }
            case 178: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 179: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 180: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 181: {
                V value2 = this.pop();
                V value1 = this.pop();
                interpreter.binaryOperation(insn, value1, value2);
                return;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                ArrayList<V> values = new ArrayList<V>();
                String desc = ((MethodInsnNode)insn).desc;
                for (int i = Type.getArgumentTypes(desc).length; i > 0; --i) {
                    values.add(0, this.pop());
                }
                if (insn.getOpcode() != 184) {
                    values.add(0, this.pop());
                }
                if (Type.getReturnType(desc) == Type.VOID_TYPE) {
                    interpreter.naryOperation(insn, values);
                    return;
                }
                this.push(interpreter.naryOperation(insn, values));
                return;
            }
            case 186: {
                ArrayList<V> values = new ArrayList<V>();
                String desc = ((InvokeDynamicInsnNode)insn).desc;
                for (int i = Type.getArgumentTypes(desc).length; i > 0; --i) {
                    values.add(0, this.pop());
                }
                if (Type.getReturnType(desc) == Type.VOID_TYPE) {
                    interpreter.naryOperation(insn, values);
                    return;
                }
                this.push(interpreter.naryOperation(insn, values));
                return;
            }
            case 187: {
                this.push(interpreter.newOperation(insn));
                return;
            }
            case 188: 
            case 189: 
            case 190: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 191: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 192: 
            case 193: {
                this.push(interpreter.unaryOperation(insn, this.pop()));
                return;
            }
            case 194: 
            case 195: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            case 197: {
                ArrayList<V> values = new ArrayList<V>();
                for (int i = ((MultiANewArrayInsnNode)insn).dims; i > 0; --i) {
                    values.add(0, this.pop());
                }
                this.push(interpreter.naryOperation(insn, values));
                return;
            }
            case 198: 
            case 199: {
                interpreter.unaryOperation(insn, this.pop());
                return;
            }
            default: {
                throw new RuntimeException("Illegal opcode " + insn.getOpcode());
            }
        }
    }

    public boolean merge(Frame<? extends V> frame, Interpreter<V> interpreter) throws AnalyzerException {
        if (this.top != frame.top) {
            throw new AnalyzerException(null, "Incompatible stack heights");
        }
        boolean changes = false;
        for (int i = 0; i < this.locals + this.top; ++i) {
            V v = interpreter.merge(this.values[i], frame.values[i]);
            if (v.equals(this.values[i])) continue;
            this.values[i] = v;
            changes = true;
        }
        return changes;
    }

    public boolean merge(Frame<? extends V> frame, boolean[] access) {
        boolean changes = false;
        for (int i = 0; i < this.locals; ++i) {
            if (access[i] || this.values[i].equals(frame.values[i])) continue;
            this.values[i] = frame.values[i];
            changes = true;
        }
        return changes;
    }

    public String toString() {
        int i;
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < this.getLocals(); ++i) {
            sb.append(this.getLocal(i));
        }
        sb.append(' ');
        for (i = 0; i < this.getStackSize(); ++i) {
            sb.append(this.getStack(i).toString());
        }
        return sb.toString();
    }
}

