package org.spongepowered.asm.mixin.injection.modify;

import java.util.HashMap;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.Locals;
import org.spongepowered.asm.util.PrettyPrinter;
import org.spongepowered.asm.util.SignaturePrinter;

public class LocalVariableDiscriminator$Context
implements PrettyPrinter.IPrettyPrintable {
    final Target target;
    final Type returnType;
    final AbstractInsnNode node;
    final int baseArgIndex;
    final Local[] locals;
    private final boolean isStatic;

    public LocalVariableDiscriminator$Context(Type returnType, boolean argsOnly, Target target, AbstractInsnNode node) {
        this.isStatic = Bytecode.methodIsStatic(target.method);
        this.returnType = returnType;
        this.target = target;
        this.node = node;
        this.baseArgIndex = this.isStatic ? 0 : 1;
        this.locals = this.initLocals(target, argsOnly, node);
        this.initOrdinals();
    }

    private Local[] initLocals(Target target, boolean argsOnly, AbstractInsnNode node) {
        LocalVariableNode[] locals;
        if (!argsOnly && (locals = Locals.getLocalsAt(target.classNode, target.method, node)) != null) {
            Local[] lvt = new Local[locals.length];
            for (int l = 0; l < locals.length; ++l) {
                if (locals[l] == null) continue;
                lvt[l] = new Local(locals[l].name, Type.getType(locals[l].desc));
            }
            return lvt;
        }
        Local[] lvt = new Local[this.baseArgIndex + target.arguments.length];
        if (!this.isStatic) {
            lvt[0] = new Local("this", Type.getType(target.classNode.name));
        }
        for (int local = this.baseArgIndex; local < lvt.length; ++local) {
            Type arg = target.arguments[local - this.baseArgIndex];
            lvt[local] = new Local("arg" + local, arg);
        }
        return lvt;
    }

    private void initOrdinals() {
        HashMap<Type, Integer> ordinalMap = new HashMap<Type, Integer>();
        for (int l = 0; l < this.locals.length; ++l) {
            Integer ordinal = 0;
            if (this.locals[l] == null) continue;
            ordinal = (Integer)ordinalMap.get(this.locals[l].type);
            ordinal = ordinal == null ? 0 : ordinal + 1;
            ordinalMap.put(this.locals[l].type, ordinal);
            this.locals[l].ord = ordinal;
        }
    }

    @Override
    public void print(PrettyPrinter printer) {
        printer.add("%5s  %7s  %30s  %-50s  %s", "INDEX", "ORDINAL", "TYPE", "NAME", "CANDIDATE");
        for (int l = this.baseArgIndex; l < this.locals.length; ++l) {
            Local local = this.locals[l];
            if (local != null) {
                Type localType = local.type;
                String localName = local.name;
                int ordinal = local.ord;
                String candidate = this.returnType.equals(localType) ? "YES" : "-";
                printer.add("[%3d]    [%3d]  %30s  %-50s  %s", l, ordinal, SignaturePrinter.getTypeName(localType, false), localName, candidate);
                continue;
            }
            if (l <= 0) continue;
            Local prevLocal = this.locals[l - 1];
            boolean isTop = prevLocal != null && prevLocal.type != null && prevLocal.type.getSize() > 1;
            printer.add("[%3d]           %30s", l, isTop ? "<top>" : "-");
        }
    }

    public class Local {
        int ord = 0;
        String name;
        Type type;

        public Local(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public String toString() {
            return String.format("Local[ordinal=%d, name=%s, type=%s]", this.ord, this.name, this.type);
        }
    }
}
