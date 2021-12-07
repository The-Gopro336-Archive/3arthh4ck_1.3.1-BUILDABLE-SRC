package org.spongepowered.asm.mixin.injection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.modify.AfterStoreLocal;
import org.spongepowered.asm.mixin.injection.modify.BeforeLoadLocal;
import org.spongepowered.asm.mixin.injection.points.AfterInvoke;
import org.spongepowered.asm.mixin.injection.points.BeforeConstant;
import org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess;
import org.spongepowered.asm.mixin.injection.points.BeforeFinalReturn;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.points.BeforeNew;
import org.spongepowered.asm.mixin.injection.points.BeforeReturn;
import org.spongepowered.asm.mixin.injection.points.BeforeStringInvoke;
import org.spongepowered.asm.mixin.injection.points.JumpInsnPoint;
import org.spongepowered.asm.mixin.injection.points.MethodHead;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;

public abstract class InjectionPoint {
    public static final int DEFAULT_ALLOWED_SHIFT_BY = 0;
    public static final int MAX_ALLOWED_SHIFT_BY = 5;
    private static Map<String, Class<? extends InjectionPoint>> types = new HashMap<String, Class<? extends InjectionPoint>>();
    private final String slice;
    private final Selector selector;
    private final String id;

    protected InjectionPoint() {
        this("", Selector.DEFAULT, null);
    }

    protected InjectionPoint(InjectionPointData data) {
        this(data.getSlice(), data.getSelector(), data.getId());
    }

    public InjectionPoint(String slice, Selector selector, String id) {
        this.slice = slice;
        this.selector = selector;
        this.id = id;
    }

    public String getSlice() {
        return this.slice;
    }

    public Selector getSelector() {
        return this.selector;
    }

    public String getId() {
        return this.id;
    }

    public boolean checkPriority(int targetPriority, int mixinPriority) {
        return targetPriority < mixinPriority;
    }

    public abstract boolean find(String var1, InsnList var2, Collection<AbstractInsnNode> var3);

    public String toString() {
        return String.format("@At(\"%s\")", this.getAtCode());
    }

    protected static AbstractInsnNode nextNode(InsnList insns, AbstractInsnNode insn) {
        int index = insns.indexOf(insn) + 1;
        if (index > 0 && index < insns.size()) {
            return insns.get(index);
        }
        return insn;
    }

    public static InjectionPoint and(InjectionPoint ... operands) {
        return new Intersection(operands);
    }

    public static InjectionPoint or(InjectionPoint ... operands) {
        return new Union(operands);
    }

    public static InjectionPoint after(InjectionPoint point) {
        return new Shift(point, 1);
    }

    public static InjectionPoint before(InjectionPoint point) {
        return new Shift(point, -1);
    }

    public static InjectionPoint shift(InjectionPoint point, int count) {
        return new Shift(point, count);
    }

    public static List<InjectionPoint> parse(IInjectionPointContext owner, List<AnnotationNode> ats) {
        return InjectionPoint.parse(owner.getContext(), owner.getMethod(), owner.getAnnotation(), ats);
    }

    public static List<InjectionPoint> parse(IMixinContext context, MethodNode method, AnnotationNode parent, List<AnnotationNode> ats) {
        ImmutableList.Builder injectionPoints = ImmutableList.builder();
        for (AnnotationNode at : ats) {
            InjectionPoint injectionPoint = InjectionPoint.parse(context, method, parent, at);
            if (injectionPoint == null) continue;
            injectionPoints.add(injectionPoint);
        }
        return injectionPoints.build();
    }

    public static InjectionPoint parse(IInjectionPointContext owner, At at) {
        return InjectionPoint.parse(owner.getContext(), owner.getMethod(), owner.getAnnotation(), at.value(), at.shift(), at.by(), Arrays.asList(at.args()), at.target(), at.slice(), at.ordinal(), at.opcode(), at.id());
    }

    public static InjectionPoint parse(IMixinContext context, MethodNode method, AnnotationNode parent, At at) {
        return InjectionPoint.parse(context, method, parent, at.value(), at.shift(), at.by(), Arrays.asList(at.args()), at.target(), at.slice(), at.ordinal(), at.opcode(), at.id());
    }

    public static InjectionPoint parse(IInjectionPointContext owner, AnnotationNode node) {
        return InjectionPoint.parse(owner.getContext(), owner.getMethod(), owner.getAnnotation(), node);
    }

    public static InjectionPoint parse(IMixinContext context, MethodNode method, AnnotationNode parent, AnnotationNode node) {
        String at = (String)Annotations.getValue(node, "value");
        ImmutableList<String> args = (ImmutableList<String>)Annotations.getValue(node, "args");
        String target = Annotations.getValue(node, "target", "");
        String slice = Annotations.getValue(node, "slice", "");
        At.Shift shift = Annotations.getValue(node, "shift", At.Shift.class, At.Shift.NONE);
        int by = Annotations.getValue(node, "by", 0);
        int ordinal = Annotations.getValue(node, "ordinal", -1);
        int opcode = Annotations.getValue(node, "opcode", 0);
        String id = (String)Annotations.getValue(node, "id");
        if (args == null) {
            args = ImmutableList.of();
        }
        return InjectionPoint.parse(context, method, parent, at, shift, by, args, target, slice, ordinal, opcode, id);
    }

    public static InjectionPoint parse(IMixinContext context, MethodNode method, AnnotationNode parent, String at, At.Shift shift, int by, List<String> args, String target, String slice, int ordinal, int opcode, String id) {
        InjectionPointData data = new InjectionPointData(context, method, parent, at, args, target, slice, ordinal, opcode, id);
        Class<? extends InjectionPoint> ipClass = InjectionPoint.findClass(context, data);
        InjectionPoint point = InjectionPoint.create(context, data, ipClass);
        return InjectionPoint.shift(context, method, parent, point, shift, by);
    }

    private static Class<? extends InjectionPoint> findClass(IMixinContext context, InjectionPointData data) {
        String type = data.getType();
        Class<InjectionPoint> ipClass = types.get(type);
        if (ipClass == null) {
            if (type.matches("^([A-Za-z_][A-Za-z0-9_]*\\.)+[A-Za-z_][A-Za-z0-9_]*$")) {
                try {
                    ipClass = Class.forName(type);
                    types.put(type, ipClass);
                }
                catch (Exception ex) {
                    throw new InvalidInjectionException(context, data + " could not be loaded or is not a valid InjectionPoint", (Throwable)ex);
                }
            } else {
                throw new InvalidInjectionException(context, data + " is not a valid injection point specifier");
            }
        }
        return ipClass;
    }

    private static InjectionPoint create(IMixinContext context, InjectionPointData data, Class<? extends InjectionPoint> ipClass) {
        Constructor<? extends InjectionPoint> ipCtor = null;
        try {
            ipCtor = ipClass.getDeclaredConstructor(InjectionPointData.class);
            ipCtor.setAccessible(true);
        }
        catch (NoSuchMethodException ex) {
            throw new InvalidInjectionException(context, ipClass.getName() + " must contain a constructor which accepts an InjectionPointData", (Throwable)ex);
        }
        InjectionPoint point = null;
        try {
            point = ipCtor.newInstance(data);
        }
        catch (Exception ex) {
            throw new InvalidInjectionException(context, "Error whilst instancing injection point " + ipClass.getName() + " for " + data.getAt(), (Throwable)ex);
        }
        return point;
    }

    private static InjectionPoint shift(IMixinContext context, MethodNode method, AnnotationNode parent, InjectionPoint point, At.Shift shift, int by) {
        if (point != null) {
            if (shift == At.Shift.BEFORE) {
                return InjectionPoint.before(point);
            }
            if (shift == At.Shift.AFTER) {
                return InjectionPoint.after(point);
            }
            if (shift == At.Shift.BY) {
                InjectionPoint.validateByValue(context, method, parent, point, by);
                return InjectionPoint.shift(point, by);
            }
        }
        return point;
    }

    private static void validateByValue(IMixinContext context, MethodNode method, AnnotationNode parent, InjectionPoint point, int by) {
        MixinEnvironment env = context.getMixin().getConfig().getEnvironment();
        ShiftByViolationBehaviour err = env.getOption(MixinEnvironment.Option.SHIFT_BY_VIOLATION_BEHAVIOUR, ShiftByViolationBehaviour.WARN);
        if (err == ShiftByViolationBehaviour.IGNORE) {
            return;
        }
        String limitBreached = "the maximum allowed value: ";
        String advice = "Increase the value of maxShiftBy to suppress this warning.";
        int allowed = 0;
        if (context instanceof MixinTargetContext) {
            allowed = ((MixinTargetContext)context).getMaxShiftByValue();
        }
        if (by <= allowed) {
            return;
        }
        if (by > 5) {
            limitBreached = "MAX_ALLOWED_SHIFT_BY=";
            advice = "You must use an alternate query or a custom injection point.";
            allowed = 5;
        }
        String message = String.format("@%s(%s) Shift.BY=%d on %s::%s exceeds %s%d. %s", Bytecode.getSimpleName(parent), point, by, context, method.name, limitBreached, allowed, advice);
        if (err == ShiftByViolationBehaviour.WARN && allowed < 5) {
            LogManager.getLogger("mixin").warn(message);
            return;
        }
        throw new InvalidInjectionException(context, message);
    }

    protected String getAtCode() {
        AtCode code = this.getClass().getAnnotation(AtCode.class);
        return code == null ? this.getClass().getName() : code.value();
    }

    public static void register(Class<? extends InjectionPoint> type) {
        AtCode code = type.getAnnotation(AtCode.class);
        if (code == null) {
            throw new IllegalArgumentException("Injection point class " + type + " is not annotated with @AtCode");
        }
        Class<? extends InjectionPoint> existing = types.get(code.value());
        if (existing != null && !existing.equals(type)) {
            LogManager.getLogger("mixin").debug("Overriding InjectionPoint {} with {} (previously {})", new Object[]{code.value(), type.getName(), existing.getName()});
        }
        types.put(code.value(), type);
    }

    static {
        InjectionPoint.register(BeforeFieldAccess.class);
        InjectionPoint.register(BeforeInvoke.class);
        InjectionPoint.register(BeforeNew.class);
        InjectionPoint.register(BeforeReturn.class);
        InjectionPoint.register(BeforeStringInvoke.class);
        InjectionPoint.register(JumpInsnPoint.class);
        InjectionPoint.register(MethodHead.class);
        InjectionPoint.register(AfterInvoke.class);
        InjectionPoint.register(BeforeLoadLocal.class);
        InjectionPoint.register(AfterStoreLocal.class);
        InjectionPoint.register(BeforeFinalReturn.class);
        InjectionPoint.register(BeforeConstant.class);
    }

    static final class Shift
    extends InjectionPoint {
        private final InjectionPoint input;
        private final int shift;

        public Shift(InjectionPoint input, int shift) {
            if (input == null) {
                throw new IllegalArgumentException("Must supply an input injection point for SHIFT");
            }
            this.input = input;
            this.shift = shift;
        }

        @Override
        public String toString() {
            return "InjectionPoint(" + this.getClass().getSimpleName() + ")[" + this.input + "]";
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
            List<Object> list = nodes instanceof List ? (List<Object>)nodes : new ArrayList<AbstractInsnNode>(nodes);
            this.input.find(desc, insns, nodes);
            for (int i = 0; i < list.size(); ++i) {
                list.set(i, insns.get(insns.indexOf((AbstractInsnNode)list.get(i)) + this.shift));
            }
            if (nodes != list) {
                nodes.clear();
                nodes.addAll((Collection<AbstractInsnNode>)list);
            }
            return nodes.size() > 0;
        }
    }

    static final class Union
    extends CompositeInjectionPoint {
        public Union(InjectionPoint ... points) {
            super(points);
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
            LinkedHashSet<AbstractInsnNode> allNodes = new LinkedHashSet<AbstractInsnNode>();
            for (int i = 0; i < this.components.length; ++i) {
                this.components[i].find(desc, insns, allNodes);
            }
            nodes.addAll(allNodes);
            return allNodes.size() > 0;
        }
    }

    static final class Intersection
    extends CompositeInjectionPoint {
        public Intersection(InjectionPoint ... points) {
            super(points);
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
            boolean found = false;
            ArrayList[] allNodes = (ArrayList[])Array.newInstance(ArrayList.class, this.components.length);
            for (int i = 0; i < this.components.length; ++i) {
                allNodes[i] = new ArrayList();
                this.components[i].find(desc, insns, allNodes[i]);
            }
            ArrayList alpha = allNodes[0];
            for (int nodeIndex = 0; nodeIndex < alpha.size(); ++nodeIndex) {
                AbstractInsnNode node = (AbstractInsnNode)alpha.get(nodeIndex);
                boolean in = true;
                for (int b = 1; b < allNodes.length && allNodes[b].contains(node); ++b) {
                }
                if (!in) continue;
                nodes.add(node);
                found = true;
            }
            return found;
        }
    }

    static abstract class CompositeInjectionPoint
    extends InjectionPoint {
        protected final InjectionPoint[] components;

        protected CompositeInjectionPoint(InjectionPoint ... components) {
            if (components == null || components.length < 2) {
                throw new IllegalArgumentException("Must supply two or more component injection points for composite point!");
            }
            this.components = components;
        }

        @Override
        public String toString() {
            return "CompositeInjectionPoint(" + this.getClass().getSimpleName() + ")[" + Joiner.on(',').join(this.components) + "]";
        }
    }

    static enum ShiftByViolationBehaviour {
        IGNORE,
        WARN,
        ERROR;

    }

    public static enum Selector {
        FIRST,
        LAST,
        ONE;

        public static final Selector DEFAULT;

        static {
            DEFAULT = FIRST;
        }
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    public static @interface AtCode {
        public String value();
    }
}
