package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FrameNode
extends AbstractInsnNode {
    public int type;
    public List<Object> local;
    public List<Object> stack;

    private FrameNode() {
        super(-1);
    }

    public FrameNode(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super(-1);
        this.type = type;
        switch (type) {
            case -1: 
            case 0: {
                this.local = FrameNode.asList(nLocal, local);
                this.stack = FrameNode.asList(nStack, stack);
                break;
            }
            case 1: {
                this.local = FrameNode.asList(nLocal, local);
                break;
            }
            case 2: {
                this.local = Arrays.asList(new Object[nLocal]);
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                this.stack = FrameNode.asList(1, stack);
            }
        }
    }

    @Override
    public int getType() {
        return 14;
    }

    @Override
    public void accept(MethodVisitor mv) {
        switch (this.type) {
            case -1: 
            case 0: {
                mv.visitFrame(this.type, this.local.size(), FrameNode.asArray(this.local), this.stack.size(), FrameNode.asArray(this.stack));
                break;
            }
            case 1: {
                mv.visitFrame(this.type, this.local.size(), FrameNode.asArray(this.local), 0, null);
                break;
            }
            case 2: {
                mv.visitFrame(this.type, this.local.size(), null, 0, null);
                break;
            }
            case 3: {
                mv.visitFrame(this.type, 0, null, 0, null);
                break;
            }
            case 4: {
                mv.visitFrame(this.type, 0, null, 1, FrameNode.asArray(this.stack));
            }
        }
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        int i;
        FrameNode clone = new FrameNode();
        clone.type = this.type;
        if (this.local != null) {
            clone.local = new ArrayList<Object>();
            for (i = 0; i < this.local.size(); ++i) {
                Object l = this.local.get(i);
                if (l instanceof LabelNode) {
                    l = labels.get(l);
                }
                clone.local.add(l);
            }
        }
        if (this.stack != null) {
            clone.stack = new ArrayList<Object>();
            for (i = 0; i < this.stack.size(); ++i) {
                Object s = this.stack.get(i);
                if (s instanceof LabelNode) {
                    s = labels.get(s);
                }
                clone.stack.add(s);
            }
        }
        return clone;
    }

    private static List<Object> asList(int n, Object[] o) {
        return Arrays.asList(o).subList(0, n);
    }

    private static Object[] asArray(List<Object> l) {
        Object[] objs = new Object[l.size()];
        for (int i = 0; i < objs.length; ++i) {
            Object o = l.get(i);
            if (o instanceof LabelNode) {
                o = ((LabelNode)o).getLabel();
            }
            objs[i] = o;
        }
        return objs;
    }
}
