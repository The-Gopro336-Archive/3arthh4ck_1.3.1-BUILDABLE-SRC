package org.spongepowered.asm.mixin.injection.invoke;

import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.invoke.InvokeInjector;
import org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess;
import org.spongepowered.asm.mixin.injection.points.BeforeNew;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;

public class RedirectInjector
extends InvokeInjector {
    private static final String KEY_NOMINATORS = "nominators";
    private static final String KEY_FUZZ = "fuzz";
    private static final String KEY_OPCODE = "opcode";
    protected Meta meta;
    private Map<BeforeNew, ConstructorRedirectData> ctorRedirectors = new HashMap<BeforeNew, ConstructorRedirectData>();

    public RedirectInjector(InjectionInfo info) {
        this(info, "@Redirect");
    }

    protected RedirectInjector(InjectionInfo info, String annotationType) {
        super(info, annotationType);
        int priority = info.getContext().getPriority();
        boolean isFinal = Annotations.getVisible(this.methodNode, Final.class) != null;
        this.meta = new Meta(priority, isFinal, this.info.toString(), this.methodNode.desc);
    }

    @Override
    protected void checkTarget(Target target) {
    }

    @Override
    protected void addTargetNode(Target target, List<InjectionNodes.InjectionNode> myNodes, AbstractInsnNode insn, Set<InjectionPoint> nominators) {
        Object other;
        InjectionNodes.InjectionNode node = target.getInjectionNode(insn);
        ConstructorRedirectData ctorData = null;
        int fuzz = 8;
        int opcode = 0;
        if (node != null && (other = (Meta)node.getDecoration("redirector")) != null && ((Meta)other).getOwner() != this) {
            if (((Meta)other).priority >= this.meta.priority) {
                Injector.logger.warn("{} conflict. Skipping {} with priority {}, already redirected by {} with priority {}", new Object[]{this.annotationType, this.info, this.meta.priority, ((Meta)other).name, ((Meta)other).priority});
                return;
            }
            if (((Meta)other).isFinal) {
                throw new InvalidInjectionException(this.info, String.format("%s conflict: %s failed because target was already remapped by %s", this.annotationType, this, ((Meta)other).name));
            }
        }
        for (InjectionPoint ip : nominators) {
            if (ip instanceof BeforeNew) {
                ctorData = this.getCtorRedirect((BeforeNew)ip);
                ctorData.wildcard = !((BeforeNew)ip).hasDescriptor();
                continue;
            }
            if (!(ip instanceof BeforeFieldAccess)) continue;
            BeforeFieldAccess bfa = (BeforeFieldAccess)ip;
            fuzz = bfa.getFuzzFactor();
            opcode = bfa.getArrayOpcode();
        }
        InjectionNodes.InjectionNode targetNode = target.addInjectionNode(insn);
        targetNode.decorate("redirector", this.meta);
        targetNode.decorate(KEY_NOMINATORS, nominators);
        if (insn instanceof TypeInsnNode && insn.getOpcode() == 187) {
            targetNode.decorate("ctor", ctorData);
        } else {
            targetNode.decorate(KEY_FUZZ, fuzz);
            targetNode.decorate(KEY_OPCODE, opcode);
        }
        myNodes.add(targetNode);
    }

    private ConstructorRedirectData getCtorRedirect(BeforeNew ip) {
        ConstructorRedirectData ctorRedirect = this.ctorRedirectors.get(ip);
        if (ctorRedirect == null) {
            ctorRedirect = new ConstructorRedirectData();
            this.ctorRedirectors.put(ip, ctorRedirect);
        }
        return ctorRedirect;
    }

    @Override
    protected void inject(Target target, InjectionNodes.InjectionNode node) {
        if (!this.preInject(node)) {
            return;
        }
        if (node.isReplaced()) {
            throw new UnsupportedOperationException("Redirector target failure for " + this.info);
        }
        if (node.getCurrentTarget() instanceof MethodInsnNode) {
            this.checkTargetForNode(target, node);
            this.injectAtInvoke(target, node);
            return;
        }
        if (node.getCurrentTarget() instanceof FieldInsnNode) {
            this.checkTargetForNode(target, node);
            this.injectAtFieldAccess(target, node);
            return;
        }
        if (node.getCurrentTarget() instanceof TypeInsnNode && node.getCurrentTarget().getOpcode() == 187) {
            if (!this.isStatic && target.isStatic) {
                throw new InvalidInjectionException(this.info, String.format("non-static callback method %s has a static target which is not supported", this));
            }
            this.injectAtConstructor(target, node);
            return;
        }
        throw new InvalidInjectionException(this.info, String.format("%s annotation on is targetting an invalid insn in %s in %s", this.annotationType, target, this));
    }

    protected boolean preInject(InjectionNodes.InjectionNode node) {
        Meta other = (Meta)node.getDecoration("redirector");
        if (other.getOwner() != this) {
            Injector.logger.warn("{} conflict. Skipping {} with priority {}, already redirected by {} with priority {}", new Object[]{this.annotationType, this.info, this.meta.priority, other.name, other.priority});
            return false;
        }
        return true;
    }

    @Override
    protected void postInject(Target target, InjectionNodes.InjectionNode node) {
        super.postInject(target, node);
        if (node.getOriginalTarget() instanceof TypeInsnNode && node.getOriginalTarget().getOpcode() == 187) {
            ConstructorRedirectData meta = (ConstructorRedirectData)node.getDecoration("ctor");
            if (meta.wildcard && meta.injected == 0) {
                throw new InvalidInjectionException(this.info, String.format("%s ctor invocation was not found in %s", this.annotationType, target));
            }
        }
    }

    @Override
    protected void injectAtInvoke(Target target, InjectionNodes.InjectionNode node) {
        RedirectedInvoke invoke = new RedirectedInvoke(target, (MethodInsnNode)node.getCurrentTarget());
        this.validateParams(invoke);
        InsnList insns = new InsnList();
        int extraLocals = Bytecode.getArgsSize(invoke.locals) + 1;
        int extraStack = 1;
        int[] argMap = this.storeArgs(target, invoke.locals, insns, 0);
        if (invoke.captureTargetArgs) {
            int argSize = Bytecode.getArgsSize(target.arguments);
            extraLocals += argSize;
            extraStack += argSize;
            argMap = Ints.concat(argMap, target.getArgIndices());
        }
        AbstractInsnNode insn = this.invokeHandlerWithArgs(this.methodArgs, insns, argMap);
        target.replaceNode(invoke.node, insn, insns);
        target.addToLocals(extraLocals);
        target.addToStack(extraStack);
    }

    protected void validateParams(RedirectedInvoke invoke) {
        int argc = this.methodArgs.length;
        String description = String.format("%s handler method %s", this.annotationType, this);
        if (!invoke.returnType.equals(this.returnType)) {
            throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Expected return type %s found %s", description, this.returnType, invoke.returnType));
        }
        for (int index = 0; index < argc; ++index) {
            Type toType = null;
            if (index >= this.methodArgs.length) {
                throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Not enough arguments found for capture of target method args, expected %d but found %d", description, argc, this.methodArgs.length));
            }
            Type fromType = this.methodArgs[index];
            if (index < invoke.locals.length) {
                toType = invoke.locals[index];
            } else {
                invoke.captureTargetArgs = true;
                argc = Math.max(argc, invoke.locals.length + invoke.target.arguments.length);
                int arg = index - invoke.locals.length;
                if (arg >= invoke.target.arguments.length) {
                    throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Found unexpected additional target argument with type %s at index %d", description, fromType, index));
                }
                toType = invoke.target.arguments[arg];
            }
            AnnotationNode coerce = Annotations.getInvisibleParameter(this.methodNode, Coerce.class, index);
            if (fromType.equals(toType)) {
                if (coerce == null || !this.info.getContext().getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) continue;
                Injector.logger.warn("Redundant @Coerce on {} argument {}, {} is identical to {}", new Object[]{description, index, toType, fromType});
                continue;
            }
            boolean canCoerce = Injector.canCoerce(fromType, toType);
            if (coerce == null) {
                throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Found unexpected argument type %s at index %d, expected %s", description, fromType, index, toType));
            }
            if (canCoerce) continue;
            throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Cannot @Coerce argument type %s at index %d to %s", description, toType, index, fromType));
        }
    }

    private void injectAtFieldAccess(Target target, InjectionNodes.InjectionNode node) {
        int handlerDimensions;
        FieldInsnNode fieldNode = (FieldInsnNode)node.getCurrentTarget();
        int opCode = fieldNode.getOpcode();
        Type ownerType = Type.getType("L" + fieldNode.owner + ";");
        Type fieldType = Type.getType(fieldNode.desc);
        int targetDimensions = fieldType.getSort() == 9 ? fieldType.getDimensions() : 0;
        int n = handlerDimensions = this.returnType.getSort() == 9 ? this.returnType.getDimensions() : 0;
        if (handlerDimensions > targetDimensions) {
            throw new InvalidInjectionException(this.info, "Dimensionality of handler method is greater than target array on " + this);
        }
        if (handlerDimensions == 0 && targetDimensions > 0) {
            int fuzz = (Integer)node.getDecoration(KEY_FUZZ);
            int opcode = (Integer)node.getDecoration(KEY_OPCODE);
            this.injectAtArrayField(target, fieldNode, opCode, ownerType, fieldType, fuzz, opcode);
        } else {
            this.injectAtScalarField(target, fieldNode, opCode, ownerType, fieldType);
        }
    }

    private void injectAtArrayField(Target target, FieldInsnNode fieldNode, int opCode, Type ownerType, Type fieldType, int fuzz, int opcode) {
        Type elementType = fieldType.getElementType();
        if (opCode != 178 && opCode != 180) {
            throw new InvalidInjectionException(this.info, String.format("Unspported opcode %s for array access %s", Bytecode.getOpcodeName(opCode), this.info));
        }
        if (this.returnType.getSort() != 0) {
            if (opcode != 190) {
                opcode = elementType.getOpcode(46);
            }
            AbstractInsnNode varNode = BeforeFieldAccess.findArrayNode(target.insns, fieldNode, opcode, fuzz);
            this.injectAtGetArray(target, fieldNode, varNode, ownerType, fieldType);
        } else {
            AbstractInsnNode varNode = BeforeFieldAccess.findArrayNode(target.insns, fieldNode, elementType.getOpcode(79), fuzz);
            this.injectAtSetArray(target, fieldNode, varNode, ownerType, fieldType);
        }
    }

    private void injectAtGetArray(Target target, FieldInsnNode fieldNode, AbstractInsnNode varNode, Type ownerType, Type fieldType) {
        String handlerDesc = RedirectInjector.getGetArrayHandlerDescriptor(varNode, this.returnType, fieldType);
        boolean withArgs = this.checkDescriptor(handlerDesc, target, "array getter");
        this.injectArrayRedirect(target, fieldNode, varNode, withArgs, "array getter");
    }

    private void injectAtSetArray(Target target, FieldInsnNode fieldNode, AbstractInsnNode varNode, Type ownerType, Type fieldType) {
        String handlerDesc = Bytecode.generateDescriptor(null, RedirectInjector.getArrayArgs(fieldType, 1, fieldType.getElementType()));
        boolean withArgs = this.checkDescriptor(handlerDesc, target, "array setter");
        this.injectArrayRedirect(target, fieldNode, varNode, withArgs, "array setter");
    }

    public void injectArrayRedirect(Target target, FieldInsnNode fieldNode, AbstractInsnNode varNode, boolean withArgs, String type) {
        if (varNode == null) {
            String advice = "";
            throw new InvalidInjectionException(this.info, String.format("Array element %s on %s could not locate a matching %s instruction in %s. %s", this.annotationType, this, type, target, advice));
        }
        if (!this.isStatic) {
            target.insns.insertBefore((AbstractInsnNode)fieldNode, new VarInsnNode(25, 0));
            target.addToStack(1);
        }
        InsnList invokeInsns = new InsnList();
        if (withArgs) {
            this.pushArgs(target.arguments, invokeInsns, target.getArgIndices(), 0, target.arguments.length);
            target.addToStack(Bytecode.getArgsSize(target.arguments));
        }
        target.replaceNode(varNode, this.invokeHandler(invokeInsns), invokeInsns);
    }

    public void injectAtScalarField(Target target, FieldInsnNode fieldNode, int opCode, Type ownerType, Type fieldType) {
        AbstractInsnNode invoke = null;
        InsnList insns = new InsnList();
        if (opCode == 178 || opCode == 180) {
            invoke = this.injectAtGetField(insns, target, fieldNode, opCode == 178, ownerType, fieldType);
        } else if (opCode == 179 || opCode == 181) {
            invoke = this.injectAtPutField(insns, target, fieldNode, opCode == 179, ownerType, fieldType);
        } else {
            throw new InvalidInjectionException(this.info, String.format("Unspported opcode %s for %s", Bytecode.getOpcodeName(opCode), this.info));
        }
        target.replaceNode(fieldNode, invoke, insns);
    }

    private AbstractInsnNode injectAtGetField(InsnList insns, Target target, FieldInsnNode node, boolean staticField, Type owner, Type fieldType) {
        String handlerDesc = staticField ? Bytecode.generateDescriptor(fieldType, new Object[0]) : Bytecode.generateDescriptor(fieldType, owner);
        boolean withArgs = this.checkDescriptor(handlerDesc, target, "getter");
        if (!this.isStatic) {
            insns.add(new VarInsnNode(25, 0));
            if (!staticField) {
                insns.add(new InsnNode(95));
            }
        }
        if (withArgs) {
            this.pushArgs(target.arguments, insns, target.getArgIndices(), 0, target.arguments.length);
            target.addToStack(Bytecode.getArgsSize(target.arguments));
        }
        target.addToStack(this.isStatic ? 0 : 1);
        return this.invokeHandler(insns);
    }

    private AbstractInsnNode injectAtPutField(InsnList insns, Target target, FieldInsnNode node, boolean staticField, Type owner, Type fieldType) {
        String handlerDesc = staticField ? Bytecode.generateDescriptor(null, fieldType) : Bytecode.generateDescriptor(null, owner, fieldType);
        boolean withArgs = this.checkDescriptor(handlerDesc, target, "setter");
        if (!this.isStatic) {
            if (staticField) {
                insns.add(new VarInsnNode(25, 0));
                insns.add(new InsnNode(95));
            } else {
                int marshallVar = target.allocateLocals(fieldType.getSize());
                insns.add(new VarInsnNode(fieldType.getOpcode(54), marshallVar));
                insns.add(new VarInsnNode(25, 0));
                insns.add(new InsnNode(95));
                insns.add(new VarInsnNode(fieldType.getOpcode(21), marshallVar));
            }
        }
        if (withArgs) {
            this.pushArgs(target.arguments, insns, target.getArgIndices(), 0, target.arguments.length);
            target.addToStack(Bytecode.getArgsSize(target.arguments));
        }
        target.addToStack(!this.isStatic && !staticField ? 1 : 0);
        return this.invokeHandler(insns);
    }

    protected boolean checkDescriptor(String desc, Target target, String type) {
        if (this.methodNode.desc.equals(desc)) {
            return false;
        }
        int pos = desc.indexOf(41);
        String alternateDesc = String.format("%s%s%s", desc.substring(0, pos), Joiner.on("").join(target.arguments), desc.substring(pos));
        if (this.methodNode.desc.equals(alternateDesc)) {
            return true;
        }
        throw new InvalidInjectionException(this.info, String.format("%s method %s %s has an invalid signature. Expected %s but found %s", this.annotationType, type, this, desc, this.methodNode.desc));
    }

    protected void injectAtConstructor(Target target, InjectionNodes.InjectionNode node) {
        ConstructorRedirectData meta = (ConstructorRedirectData)node.getDecoration("ctor");
        if (meta == null) {
            throw new InvalidInjectionException(this.info, String.format("%s ctor redirector has no metadata, the injector failed a preprocessing phase", this.annotationType));
        }
        TypeInsnNode newNode = (TypeInsnNode)node.getCurrentTarget();
        AbstractInsnNode dupNode = target.get(target.indexOf(newNode) + 1);
        MethodInsnNode initNode = target.findInitNodeFor(newNode);
        if (initNode == null) {
            if (!meta.wildcard) {
                throw new InvalidInjectionException(this.info, String.format("%s ctor invocation was not found in %s", this.annotationType, target));
            }
            return;
        }
        boolean isAssigned = dupNode.getOpcode() == 89;
        String desc = initNode.desc.replace(")V", ")L" + newNode.desc + ";");
        boolean withArgs = false;
        try {
            withArgs = this.checkDescriptor(desc, target, "constructor");
        }
        catch (InvalidInjectionException ex) {
            if (!meta.wildcard) {
                throw ex;
            }
            return;
        }
        if (isAssigned) {
            target.removeNode(dupNode);
        }
        if (this.isStatic) {
            target.removeNode(newNode);
        } else {
            target.replaceNode((AbstractInsnNode)newNode, new VarInsnNode(25, 0));
        }
        InsnList insns = new InsnList();
        if (withArgs) {
            this.pushArgs(target.arguments, insns, target.getArgIndices(), 0, target.arguments.length);
            target.addToStack(Bytecode.getArgsSize(target.arguments));
        }
        this.invokeHandler(insns);
        if (isAssigned) {
            LabelNode nullCheckSucceeded = new LabelNode();
            insns.add(new InsnNode(89));
            insns.add(new JumpInsnNode(199, nullCheckSucceeded));
            this.throwException(insns, "java/lang/NullPointerException", String.format("%s constructor handler %s returned null for %s", this.annotationType, this, newNode.desc.replace('/', '.')));
            insns.add(nullCheckSucceeded);
            target.addToStack(1);
        } else {
            insns.add(new InsnNode(87));
        }
        target.replaceNode((AbstractInsnNode)initNode, insns);
        ++meta.injected;
    }

    private static String getGetArrayHandlerDescriptor(AbstractInsnNode varNode, Type returnType, Type fieldType) {
        if (varNode != null && varNode.getOpcode() == 190) {
            return Bytecode.generateDescriptor(Type.INT_TYPE, RedirectInjector.getArrayArgs(fieldType, 0, new Type[0]));
        }
        return Bytecode.generateDescriptor(returnType, RedirectInjector.getArrayArgs(fieldType, 1, new Type[0]));
    }

    private static Type[] getArrayArgs(Type fieldType, int extraDimensions, Type ... extra) {
        int dimensions = fieldType.getDimensions() + extraDimensions;
        Type[] args = new Type[dimensions + extra.length];
        for (int i = 0; i < args.length; ++i) {
            args[i] = i == 0 ? fieldType : (i < dimensions ? Type.INT_TYPE : extra[dimensions - i]);
        }
        return args;
    }

    static class RedirectedInvoke {
        final Target target;
        final MethodInsnNode node;
        final Type returnType;
        final Type[] args;
        final Type[] locals;
        boolean captureTargetArgs = false;

        RedirectedInvoke(Target target, MethodInsnNode node) {
            this.target = target;
            this.node = node;
            this.returnType = Type.getReturnType(node.desc);
            this.args = Type.getArgumentTypes(node.desc);
            this.locals = node.getOpcode() == 184 ? this.args : ObjectArrays.concat(Type.getType("L" + node.owner + ";"), this.args);
        }
    }

    static class ConstructorRedirectData {
        public static final String KEY = "ctor";
        public boolean wildcard = false;
        public int injected = 0;

        ConstructorRedirectData() {
        }
    }

    class Meta {
        public static final String KEY = "redirector";
        final int priority;
        final boolean isFinal;
        final String name;
        final String desc;

        public Meta(int priority, boolean isFinal, String name, String desc) {
            this.priority = priority;
            this.isFinal = isFinal;
            this.name = name;
            this.desc = desc;
        }

        RedirectInjector getOwner() {
            return RedirectInjector.this;
        }
    }
}
