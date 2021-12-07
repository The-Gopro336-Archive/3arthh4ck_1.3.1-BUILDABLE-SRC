package org.spongepowered.asm.mixin.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.Annotations;

public class ClassInfo$Method
extends ClassInfo.Member {
    private final List<ClassInfo.FrameData> frames;
    private boolean isAccessor;

    public ClassInfo$Method(ClassInfo.Member member) {
        super(member);
        this.frames = member instanceof ClassInfo$Method ? ((ClassInfo$Method)member).frames : null;
    }

    public ClassInfo$Method(MethodNode method) {
        this(method, false);
        this.setUnique(Annotations.getVisible(method, Unique.class) != null);
        this.isAccessor = Annotations.getSingleVisible(method, Accessor.class, Invoker.class) != null;
    }

    public ClassInfo$Method(MethodNode method, boolean injected) {
        super(ClassInfo.Member.Type.METHOD, method.name, method.desc, method.access, injected);
        this.frames = this.gatherFrames(method);
        this.setUnique(Annotations.getVisible(method, Unique.class) != null);
        this.isAccessor = Annotations.getSingleVisible(method, Accessor.class, Invoker.class) != null;
    }

    public ClassInfo$Method(String name, String desc) {
        super(ClassInfo.Member.Type.METHOD, name, desc, 1, false);
        this.frames = null;
    }

    public ClassInfo$Method(String name, String desc, int access) {
        super(ClassInfo.Member.Type.METHOD, name, desc, access, false);
        this.frames = null;
    }

    public ClassInfo$Method(String name, String desc, int access, boolean injected) {
        super(ClassInfo.Member.Type.METHOD, name, desc, access, injected);
        this.frames = null;
    }

    private List<ClassInfo.FrameData> gatherFrames(MethodNode method) {
        ArrayList<ClassInfo.FrameData> frames = new ArrayList<ClassInfo.FrameData>();
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof FrameNode)) continue;
            frames.add(new ClassInfo.FrameData(method.instructions.indexOf(insn), (FrameNode)insn));
        }
        return frames;
    }

    public List<ClassInfo.FrameData> getFrames() {
        return this.frames;
    }

    @Override
    public ClassInfo getOwner() {
        return ClassInfo.this;
    }

    public boolean isAccessor() {
        return this.isAccessor;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassInfo$Method)) {
            return false;
        }
        return super.equals(obj);
    }
}
