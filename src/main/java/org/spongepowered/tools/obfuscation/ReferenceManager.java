/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.tools.obfuscation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.refmap.ReferenceMapper;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IReferenceManager;

public class ReferenceManager
implements IReferenceManager {
    private final IMixinAnnotationProcessor ap;
    private final String outRefMapFileName;
    private final List<ObfuscationEnvironment> environments;
    private final ReferenceMapper refMapper = new ReferenceMapper();
    private boolean allowConflicts;

    public ReferenceManager(IMixinAnnotationProcessor ap, List<ObfuscationEnvironment> environments) {
        this.ap = ap;
        this.environments = environments;
        this.outRefMapFileName = this.ap.getOption("outRefMapFile");
    }

    @Override
    public boolean getAllowConflicts() {
        return this.allowConflicts;
    }

    @Override
    public void setAllowConflicts(boolean allowConflicts) {
        this.allowConflicts = allowConflicts;
    }

    @Override
    public void write() {
        if (this.outRefMapFileName == null) {
            return;
        }
        PrintWriter writer = null;
        try {
            writer = this.newWriter(this.outRefMapFileName, "refmap");
            this.refMapper.write(writer);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    private PrintWriter newWriter(String fileName, String description) throws IOException {
        if (fileName.matches("^.*[\\\\/:].*$")) {
            File outFile = new File(fileName);
            outFile.getParentFile().mkdirs();
            this.ap.printMessage(Diagnostic.Kind.NOTE, "Writing " + description + " to " + outFile.getAbsolutePath());
            return new PrintWriter(outFile);
        }
        FileObject outResource = this.ap.getProcessingEnvironment().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName, new Element[0]);
        this.ap.printMessage(Diagnostic.Kind.NOTE, "Writing " + description + " to " + new File(outResource.toUri()).getAbsolutePath());
        return new PrintWriter(outResource.openWriter());
    }

    @Override
    public ReferenceMapper getMapper() {
        return this.refMapper;
    }

    @Override
    public void addMethodMapping(String className, String reference, ObfuscationData<MappingMethod> obfMethodData) {
        for (ObfuscationEnvironment env : this.environments) {
            MappingMethod obfMethod = obfMethodData.get(env.getType());
            if (obfMethod == null) continue;
            MemberInfo remappedReference = new MemberInfo(obfMethod);
            this.addMapping(env.getType(), className, reference, remappedReference.toString());
        }
    }

    @Override
    public void addMethodMapping(String className, String reference, MemberInfo context, ObfuscationData<MappingMethod> obfMethodData) {
        for (ObfuscationEnvironment env : this.environments) {
            MappingMethod obfMethod = obfMethodData.get(env.getType());
            if (obfMethod == null) continue;
            MemberInfo remappedReference = context.remapUsing(obfMethod, true);
            this.addMapping(env.getType(), className, reference, remappedReference.toString());
        }
    }

    @Override
    public void addFieldMapping(String className, String reference, MemberInfo context, ObfuscationData<MappingField> obfFieldData) {
        for (ObfuscationEnvironment env : this.environments) {
            MappingField obfField = obfFieldData.get(env.getType());
            if (obfField == null) continue;
            MemberInfo remappedReference = MemberInfo.fromMapping(obfField.transform(env.remapDescriptor(context.desc)));
            this.addMapping(env.getType(), className, reference, remappedReference.toString());
        }
    }

    @Override
    public void addClassMapping(String className, String reference, ObfuscationData<String> obfClassData) {
        for (ObfuscationEnvironment env : this.environments) {
            String remapped = obfClassData.get(env.getType());
            if (remapped == null) continue;
            this.addMapping(env.getType(), className, reference, remapped);
        }
    }

    protected void addMapping(ObfuscationType type, String className, String reference, String newReference) {
        String oldReference = this.refMapper.addMapping(type.getKey(), className, reference, newReference);
        if (type.isDefault()) {
            this.refMapper.addMapping(null, className, reference, newReference);
        }
        if (!this.allowConflicts && oldReference != null && !oldReference.equals(newReference)) {
            throw new ReferenceConflictException(oldReference, newReference);
        }
    }

    public static class ReferenceConflictException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final String oldReference;
        private final String newReference;

        public ReferenceConflictException(String oldReference, String newReference) {
            this.oldReference = oldReference;
            this.newReference = newReference;
        }

        public String getOld() {
            return this.oldReference;
        }

        public String getNew() {
            return this.newReference;
        }
    }
}

