/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3i
 *  org.objectweb.asm.Label
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FrameNode
 *  org.objectweb.asm.tree.LabelNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TypeInsnNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package me.earth.earthhack.impl.core.transfomer.patch.patches;

import java.lang.reflect.Method;
import java.util.Collections;
import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import me.earth.earthhack.impl.core.util.AsmUtil;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import net.minecraft.util.math.Vec3i;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BlockPosPatch
extends ArgumentPatch {
    public BlockPosPatch() {
        super("et", "net.minecraft.util.math.BlockPos", "leijurvpos");
    }

    @Override
    protected void applyPatch(ClassNode node) {
        OffsetPatch[] patches;
        for (OffsetPatch patch : patches = new OffsetPatch[]{new OffsetPatch(Direction.UP, "b", "(I)Let;", "func_177981_b", "up", true), new OffsetPatch(Direction.UP, "a", "()Let;", "func_177984_a", "up", false), new OffsetPatch(Direction.DOWN, "c", "(I)Let;", "func_177979_c", "down", true), new OffsetPatch(Direction.DOWN, "b", "()Let;", "func_177977_b", "down", false), new OffsetPatch(Direction.NORTH, "d", "(I)Let;", "func_177964_d", "north", true), new OffsetPatch(Direction.NORTH, "c", "()Let;", "func_177978_c", "north", false), new OffsetPatch(Direction.SOUTH, "e", "(I)Let;", "func_177970_e", "south", true), new OffsetPatch(Direction.SOUTH, "d", "()Let;", "func_177968_d", "south", false), new OffsetPatch(Direction.EAST, "g", "(I)Let;", "func_177965_g", "east", true), new OffsetPatch(Direction.EAST, "f", "()Let;", "func_177974_f", "east", false), new OffsetPatch(Direction.WEST, "f", "(I)Let;", "func_177985_f", "west", true), new OffsetPatch(Direction.WEST, "e", "()Let;", "func_177976_e", "west", false)}) {
            this.patch(node, patch);
        }
        this.patchOffset(node);
    }

    private void patchOffset(ClassNode node) {
        MethodNode offset = AsmUtil.findMappedMethod(node, "a", "(Lfa;)Let;", "func_177972_a", "offset", "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;");
        MethodNode offsetI = AsmUtil.findMappedMethod(node, "a", "(Lfa;I)Let;", "func_177967_a", "offset", "(Lnet/minecraft/util/EnumFacing;I)Lnet/minecraft/util/math/BlockPos;");
        if (offset == null || offsetI == null) {
            Core.LOGGER.error("Couldn't find " + (Object)offset + " or " + (Object)offsetI + "!");
            return;
        }
        offset.instructions.clear();
        boolean newFound = false;
        for (int i = 0; i < offsetI.instructions.size(); ++i) {
            AbstractInsnNode insn = offsetI.instructions.get(i);
            if (insn instanceof TypeInsnNode && insn.getOpcode() == 187 && ((TypeInsnNode)insn).desc.equals(node.name)) {
                newFound = true;
            } else if (!newFound || insn instanceof FrameNode || insn instanceof LabelNode || insn instanceof VarInsnNode && ((VarInsnNode)insn).var == 2 || insn.getOpcode() == 104) continue;
            offset.instructions.add(insn.clone(Collections.emptyMap()));
        }
    }

    private void patch(ClassNode cn, OffsetPatch op) {
        MethodNode mn = AsmUtil.findMappedMethod(cn, op.notch, op.notchDesc, op.searge, op.mcp, op.srgMcpDesc);
        Method x = ReflectionUtil.getMethod(Vec3i.class, "p", "func_177958_n", "getX", new Class[0]);
        Method y = ReflectionUtil.getMethod(Vec3i.class, "q", "func_177956_o", "getY", new Class[0]);
        Method z = ReflectionUtil.getMethod(Vec3i.class, "r", "func_177952_p", "getZ", new Class[0]);
        if (mn == null) {
            Core.LOGGER.error("Couldn't find " + op + " in BlockPos!");
            return;
        }
        mn.instructions.clear();
        mn.visitCode();
        if (op.notchDesc.startsWith("(I)")) {
            Label L1 = new Label();
            mn.visitVarInsn(21, 1);
            mn.visitJumpInsn(154, L1);
            mn.visitVarInsn(25, 0);
            mn.visitInsn(176);
            mn.visitLabel(L1);
        }
        mn.visitTypeInsn(187, cn.name);
        mn.visitInsn(89);
        mn.visitVarInsn(25, 0);
        mn.visitMethodInsn(182, cn.name, x.getName(), "()I", false);
        switch (op.direction) {
            case UP: 
            case DOWN: {
                mn.visitVarInsn(25, 0);
                mn.visitMethodInsn(182, cn.name, y.getName(), "()I", false);
                this.addOrSub(mn, op);
                mn.visitVarInsn(25, 0);
                mn.visitMethodInsn(182, cn.name, z.getName(), "()I", false);
                break;
            }
            case NORTH: 
            case SOUTH: {
                mn.visitVarInsn(25, 0);
                mn.visitMethodInsn(182, cn.name, y.getName(), "()I", false);
                mn.visitVarInsn(25, 0);
                mn.visitMethodInsn(182, cn.name, z.getName(), "()I", false);
                this.addOrSub(mn, op);
                break;
            }
            case EAST: 
            case WEST: {
                this.addOrSub(mn, op);
                mn.visitVarInsn(25, 0);
                mn.visitMethodInsn(182, cn.name, y.getName(), "()I", false);
                mn.visitVarInsn(25, 0);
                mn.visitMethodInsn(182, cn.name, z.getName(), "()I", false);
            }
        }
        mn.visitMethodInsn(183, cn.name, "<init>", "(III)V", false);
        mn.visitInsn(176);
        mn.visitMaxs(0, 0);
        mn.visitEnd();
    }

    private void addOrSub(MethodNode mn, OffsetPatch op) {
        if (op.notchDesc.startsWith("(I)")) {
            mn.visitVarInsn(21, 1);
        } else {
            mn.visitInsn(4);
        }
        mn.visitInsn(op.direction.offset > 0 ? 96 : 100);
    }

    private static enum Direction {
        UP(1),
        DOWN(-1),
        NORTH(-1),
        SOUTH(1),
        WEST(-1),
        EAST(1);

        public final int offset;

        private Direction(int offset) {
            this.offset = offset;
        }
    }

    private static final class OffsetPatch {
        public final Direction direction;
        public final String notch;
        public final String notchDesc;
        public final String searge;
        public final String mcp;
        public final String srgMcpDesc;

        public OffsetPatch(Direction direction, String notch, String notchDesc, String searge, String mcp, boolean takesInt) {
            this.direction = direction;
            this.notch = notch;
            this.notchDesc = notchDesc;
            this.searge = searge;
            this.mcp = mcp;
            this.srgMcpDesc = takesInt ? "(I)Lnet/minecraft/util/math/BlockPos;" : "()Lnet/minecraft/util/math/BlockPos;";
        }

        public String toString() {
            return this.notch + this.notchDesc + " -> " + this.mcp + this.srgMcpDesc + " (" + this.searge + ")";
        }
    }
}

