package org.spongepowered.tools.obfuscation;

import javax.lang.model.element.Element;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;

class AnnotatedMixinElementHandler$ShadowElementName
extends AnnotatedMixinElementHandler.AliasedElementName {
    private final boolean hasPrefix;
    private final String prefix;
    private final String baseName;
    private String obfuscated;

    AnnotatedMixinElementHandler$ShadowElementName(Element element, AnnotationHandle shadow) {
        super(element, shadow);
        this.prefix = shadow.getValue("prefix", "shadow$");
        boolean hasPrefix = false;
        String name = this.originalName;
        if (name.startsWith(this.prefix)) {
            hasPrefix = true;
            name = name.substring(this.prefix.length());
        }
        this.hasPrefix = hasPrefix;
        this.obfuscated = this.baseName = name;
    }

    public String toString() {
        return this.baseName;
    }

    @Override
    public String baseName() {
        return this.baseName;
    }

    public AnnotatedMixinElementHandler$ShadowElementName setObfuscatedName(IMapping<?> name) {
        this.obfuscated = name.getName();
        return this;
    }

    public AnnotatedMixinElementHandler$ShadowElementName setObfuscatedName(String name) {
        this.obfuscated = name;
        return this;
    }

    @Override
    public boolean hasPrefix() {
        return this.hasPrefix;
    }

    public String prefix() {
        return this.hasPrefix ? this.prefix : "";
    }

    public String name() {
        return this.prefix(this.baseName);
    }

    public String obfuscated() {
        return this.prefix(this.obfuscated);
    }

    public String prefix(String name) {
        return this.hasPrefix ? this.prefix + name : name;
    }
}
