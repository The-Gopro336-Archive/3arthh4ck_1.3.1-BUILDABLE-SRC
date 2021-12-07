/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LineNumberNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.lib.tree.analysis.Analyzer;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.Frame;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.asm.MixinVerifier;
import org.spongepowered.asm.util.throwables.LVTGeneratorException;

public final class Locals {
    private static final Map<String, List<LocalVariableNode>> calculatedLocalVariables = new HashMap<String, List<LocalVariableNode>>();

    private Locals() {
    }

    public static void loadLocals(Type[] locals, InsnList insns, int pos, int limit) {
        while (pos < locals.length && limit > 0) {
            if (locals[pos] != null) {
                insns.add(new VarInsnNode(locals[pos].getOpcode(21), pos));
                --limit;
            }
            ++pos;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static LocalVariableNode[] getLocalsAt(ClassNode classNode, MethodNode method, AbstractInsnNode node) {
        for (int i = 0; i < 3 && (node instanceof LabelNode || node instanceof LineNumberNode); ++i) {
            node = Locals.nextNode(method.instructions, node);
        }
        ClassInfo classInfo = ClassInfo.forName(classNode.name);
        if (classInfo == null) {
            throw new LVTGeneratorException("Could not load class metadata for " + classNode.name + " generating LVT for " + method.name);
        }
        ClassInfo.Method methodInfo = classInfo.findMethod(method);
        if (methodInfo == null) {
            throw new LVTGeneratorException("Could not locate method metadata for " + method.name + " generating LVT in " + classNode.name);
        }
        List<ClassInfo.FrameData> frames = methodInfo.getFrames();
        LocalVariableNode[] frame = new LocalVariableNode[method.maxLocals];
        int local = 0;
        int index = 0;
        if ((method.access & 8) == 0) {
            frame[local++] = new LocalVariableNode("this", classNode.name, null, null, null, 0);
        }
        for (Type argType : Type.getArgumentTypes(method.desc)) {
            frame[local] = new LocalVariableNode("arg" + index++, argType.toString(), null, null, null, local);
            local += argType.getSize();
        }
        int initialFrameSize = local;
        int frameIndex = -1;
        int locals = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn instanceof FrameNode) {
                FrameNode frameNode = (FrameNode)insn;
                ClassInfo.FrameData frameData = ++frameIndex < frames.size() ? frames.get(frameIndex) : null;
                locals = frameData != null && frameData.type == 0 ? Math.min(locals, frameData.locals) : frameNode.local.size();
                int localPos = 0;
                int framePos = 0;
                while (framePos < frame.length) {
                    Object localType;
                    Object object = localType = localPos < frameNode.local.size() ? frameNode.local.get(localPos) : null;
                    if (localType instanceof String) {
                        frame[framePos] = Locals.getLocalVariableAt(classNode, method, node, framePos);
                    } else if (localType instanceof Integer) {
                        boolean is64bitValue;
                        boolean isMarkerType = localType == Opcodes.UNINITIALIZED_THIS || localType == Opcodes.NULL;
                        boolean is32bitValue = localType == Opcodes.INTEGER || localType == Opcodes.FLOAT;
                        boolean bl = is64bitValue = localType == Opcodes.DOUBLE || localType == Opcodes.LONG;
                        if (localType != Opcodes.TOP) {
                            if (isMarkerType) {
                                frame[framePos] = null;
                            } else {
                                if (!is32bitValue && !is64bitValue) throw new LVTGeneratorException("Unrecognised locals opcode " + localType + " in locals array at position " + localPos + " in " + classNode.name + "." + method.name + method.desc);
                                frame[framePos] = Locals.getLocalVariableAt(classNode, method, node, framePos);
                                if (is64bitValue) {
                                    frame[++framePos] = null;
                                }
                            }
                        }
                    } else {
                        if (localType != null) throw new LVTGeneratorException("Invalid value " + localType + " in locals array at position " + localPos + " in " + classNode.name + "." + method.name + method.desc);
                        if (framePos >= initialFrameSize && framePos >= locals && locals > 0) {
                            frame[framePos] = null;
                        }
                    }
                    ++framePos;
                    ++localPos;
                }
            } else if (insn instanceof VarInsnNode) {
                VarInsnNode varNode = (VarInsnNode)insn;
                frame[varNode.var] = Locals.getLocalVariableAt(classNode, method, node, varNode.var);
            }
            if (insn != node) continue;
            break;
        }
        for (int l = 0; l < frame.length; ++l) {
            if (frame[l] == null || frame[l].desc != null) continue;
            frame[l] = null;
        }
        return frame;
    }

    public static LocalVariableNode getLocalVariableAt(ClassNode classNode, MethodNode method, AbstractInsnNode node, int var) {
        return Locals.getLocalVariableAt(classNode, method, method.instructions.indexOf(node), var);
    }

    private static LocalVariableNode getLocalVariableAt(ClassNode classNode, MethodNode method, int pos, int var) {
        LocalVariableNode localVariableNode = null;
        LocalVariableNode fallbackNode = null;
        for (LocalVariableNode local : Locals.getLocalVariableTable(classNode, method)) {
            if (local.index != var) continue;
            if (Locals.isOpcodeInRange(method.instructions, local, pos)) {
                localVariableNode = local;
                continue;
            }
            if (localVariableNode != null) continue;
            fallbackNode = local;
        }
        if (localVariableNode == null && !method.localVariables.isEmpty()) {
            for (LocalVariableNode local : Locals.getGeneratedLocalVariableTable(classNode, method)) {
                if (local.index != var || !Locals.isOpcodeInRange(method.instructions, local, pos)) continue;
                localVariableNode = local;
            }
        }
        return localVariableNode != null ? localVariableNode : fallbackNode;
    }

    private static boolean isOpcodeInRange(InsnList insns, LocalVariableNode local, int pos) {
        return insns.indexOf(local.start) < pos && insns.indexOf(local.end) > pos;
    }

    public static List<LocalVariableNode> getLocalVariableTable(ClassNode classNode, MethodNode method) {
        if (method.localVariables.isEmpty()) {
            return Locals.getGeneratedLocalVariableTable(classNode, method);
        }
        return method.localVariables;
    }

    public static List<LocalVariableNode> getGeneratedLocalVariableTable(ClassNode classNode, MethodNode method) {
        String methodId = String.format("%s.%s%s", classNode.name, method.name, method.desc);
        List<LocalVariableNode> localVars = calculatedLocalVariables.get(methodId);
        if (localVars != null) {
            return localVars;
        }
        localVars = Locals.generateLocalVariableTable(classNode, method);
        calculatedLocalVariables.put(methodId, localVars);
        return localVars;
    }

    public static List<LocalVariableNode> generateLocalVariableTable(ClassNode classNode, MethodNode method) {
        ArrayList<Type> interfaces = null;
        if (classNode.interfaces != null) {
            interfaces = new ArrayList<Type>();
            for (String iface : classNode.interfaces) {
                interfaces.add(Type.getObjectType(iface));
            }
        }
        Type objectType = null;
        if (classNode.superName != null) {
            objectType = Type.getObjectType(classNode.superName);
        }
        Analyzer<BasicValue> analyzer = new Analyzer<BasicValue>(new MixinVerifier(Type.getObjectType(classNode.name), objectType, interfaces, false));
        try {
            analyzer.analyze(classNode.name, method);
        }
        catch (AnalyzerException ex) {
            ex.printStackTrace();
        }
        Frame<BasicValue>[] frames = analyzer.getFrames();
        int methodSize = method.instructions.size();
        ArrayList<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();
        LocalVariableNode[] localNodes = new LocalVariableNode[method.maxLocals];
        BasicValue[] locals = new BasicValue[method.maxLocals];
        LabelNode[] labels = new LabelNode[methodSize];
        String[] lastKnownType = new String[method.maxLocals];
        for (int i = 0; i < methodSize; ++i) {
            Frame<BasicValue> f = frames[i];
            if (f == null) continue;
            LabelNode label = null;
            for (int j = 0; j < f.getLocals(); ++j) {
                BasicValue local = f.getLocal(j);
                if (local == null && locals[j] == null || local != null && local.equals(locals[j])) continue;
                if (label == null) {
                    AbstractInsnNode existingLabel = method.instructions.get(i);
                    if (existingLabel instanceof LabelNode) {
                        label = (LabelNode)existingLabel;
                    } else {
                        labels[i] = label = new LabelNode();
                    }
                }
                if (local == null && locals[j] != null) {
                    localVariables.add(localNodes[j]);
                    localNodes[j].end = label;
                    localNodes[j] = null;
                } else if (local != null) {
                    if (locals[j] != null) {
                        localVariables.add(localNodes[j]);
                        localNodes[j].end = label;
                        localNodes[j] = null;
                    }
                    String desc = local.getType() != null ? local.getType().getDescriptor() : lastKnownType[j];
                    localNodes[j] = new LocalVariableNode("var" + j, desc, null, label, null, j);
                    if (desc != null) {
                        lastKnownType[j] = desc;
                    }
                }
                locals[j] = local;
            }
        }
        LabelNode label = null;
        for (int k = 0; k < localNodes.length; ++k) {
            if (localNodes[k] == null) continue;
            if (label == null) {
                label = new LabelNode();
                method.instructions.add(label);
            }
            localNodes[k].end = label;
            localVariables.add(localNodes[k]);
        }
        for (int n = methodSize - 1; n >= 0; --n) {
            if (labels[n] == null) continue;
            method.instructions.insert(method.instructions.get(n), labels[n]);
        }
        return localVariables;
    }

    private static AbstractInsnNode nextNode(InsnList insns, AbstractInsnNode insn) {
        int index = insns.indexOf(insn) + 1;
        if (index > 0 && index < insns.size()) {
            return insns.get(index);
        }
        return insn;
    }
}

