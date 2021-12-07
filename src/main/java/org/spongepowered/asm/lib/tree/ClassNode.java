/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.lib.tree.InnerClassNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeAnnotationNode;

public class ClassNode
extends ClassVisitor {
    public int version;
    public int access;
    public String name;
    public String signature;
    public String superName;
    public List<String> interfaces = new ArrayList<String>();
    public String sourceFile;
    public String sourceDebug;
    public String outerClass;
    public String outerMethod;
    public String outerMethodDesc;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;
    public List<InnerClassNode> innerClasses = new ArrayList<InnerClassNode>();
    public List<FieldNode> fields = new ArrayList<FieldNode>();
    public List<MethodNode> methods = new ArrayList<MethodNode>();

    public ClassNode() {
        this(327680);
        if (this.getClass() != ClassNode.class) {
            throw new IllegalStateException();
        }
    }

    public ClassNode(int api) {
        super(api);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        if (interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
    }

    public void visitSource(String file, String debug) {
        this.sourceFile = file;
        this.sourceDebug = debug;
    }

    public void visitOuterClass(String owner, String name, String desc) {
        this.outerClass = owner;
        this.outerMethod = name;
        this.outerMethodDesc = desc;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.visibleAnnotations.add(an);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.invisibleAnnotations.add(an);
        }
        return an;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (this.visibleTypeAnnotations == null) {
                this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.visibleTypeAnnotations.add(an);
        } else {
            if (this.invisibleTypeAnnotations == null) {
                this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    public void visitAttribute(Attribute attr) {
        if (this.attrs == null) {
            this.attrs = new ArrayList<Attribute>(1);
        }
        this.attrs.add(attr);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        InnerClassNode icn = new InnerClassNode(name, outerName, innerName, access);
        this.innerClasses.add(icn);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldNode fn = new FieldNode(access, name, desc, signature, value);
        this.fields.add(fn);
        return fn;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);
        this.methods.add(mn);
        return mn;
    }

    public void visitEnd() {
    }

    public void check(int api) {
        if (api == 262144) {
            if (this.visibleTypeAnnotations != null && this.visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (this.invisibleTypeAnnotations != null && this.invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            for (FieldNode f : this.fields) {
                f.check(api);
            }
            for (MethodNode m : this.methods) {
                m.check(api);
            }
        }
    }

    public void accept(ClassVisitor cv) {
        AnnotationNode an;
        int i;
        String[] interfaces = new String[this.interfaces.size()];
        this.interfaces.toArray(interfaces);
        cv.visit(this.version, this.access, this.name, this.signature, this.superName, interfaces);
        if (this.sourceFile != null || this.sourceDebug != null) {
            cv.visitSource(this.sourceFile, this.sourceDebug);
        }
        if (this.outerClass != null) {
            cv.visitOuterClass(this.outerClass, this.outerMethod, this.outerMethodDesc);
        }
        int n = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            an = this.visibleAnnotations.get(i);
            an.accept(cv.visitAnnotation(an.desc, true));
        }
        n = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            an = this.invisibleAnnotations.get(i);
            an.accept(cv.visitAnnotation(an.desc, false));
        }
        n = this.visibleTypeAnnotations == null ? 0 : this.visibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            an = this.visibleTypeAnnotations.get(i);
            an.accept(cv.visitTypeAnnotation(((TypeAnnotationNode)an).typeRef, ((TypeAnnotationNode)an).typePath, ((TypeAnnotationNode)an).desc, true));
        }
        n = this.invisibleTypeAnnotations == null ? 0 : this.invisibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            an = this.invisibleTypeAnnotations.get(i);
            an.accept(cv.visitTypeAnnotation(((TypeAnnotationNode)an).typeRef, ((TypeAnnotationNode)an).typePath, ((TypeAnnotationNode)an).desc, false));
        }
        n = this.attrs == null ? 0 : this.attrs.size();
        for (i = 0; i < n; ++i) {
            cv.visitAttribute(this.attrs.get(i));
        }
        for (i = 0; i < this.innerClasses.size(); ++i) {
            this.innerClasses.get(i).accept(cv);
        }
        for (i = 0; i < this.fields.size(); ++i) {
            this.fields.get(i).accept(cv);
        }
        for (i = 0; i < this.methods.size(); ++i) {
            this.methods.get(i).accept(cv);
        }
        cv.visitEnd();
    }
}

