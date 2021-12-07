package org.spongepowered.asm.mixin.injection.struct;

import java.util.HashMap;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.asm.mixin.injection.throwables.InjectionValidationException;
import org.spongepowered.asm.util.Annotations;

public final class InjectorGroupInfo$Map
extends HashMap<String, InjectorGroupInfo> {
    private static final long serialVersionUID = 1L;
    private static final InjectorGroupInfo NO_GROUP = new InjectorGroupInfo("NONE", true);

    @Override
    public InjectorGroupInfo get(Object key) {
        return this.forName(key.toString());
    }

    public InjectorGroupInfo forName(String name) {
        InjectorGroupInfo value = (InjectorGroupInfo)super.get(name);
        if (value == null) {
            value = new InjectorGroupInfo(name);
            this.put(name, value);
        }
        return value;
    }

    public InjectorGroupInfo parseGroup(MethodNode method, String defaultGroup) {
        return this.parseGroup(Annotations.getInvisible(method, Group.class), defaultGroup);
    }

    public InjectorGroupInfo parseGroup(AnnotationNode annotation, String defaultGroup) {
        Integer max;
        if (annotation == null) {
            return NO_GROUP;
        }
        String name = (String)Annotations.getValue(annotation, "name");
        if (name == null || name.isEmpty()) {
            name = defaultGroup;
        }
        InjectorGroupInfo groupInfo = this.forName(name);
        Integer min = (Integer)Annotations.getValue(annotation, "min");
        if (min != null && min != -1) {
            groupInfo.setMinRequired(min);
        }
        if ((max = (Integer)Annotations.getValue(annotation, "max")) != null && max != -1) {
            groupInfo.setMaxAllowed(max);
        }
        return groupInfo;
    }

    public void validateAll() throws InjectionValidationException {
        for (InjectorGroupInfo group : this.values()) {
            group.validate();
        }
    }
}
