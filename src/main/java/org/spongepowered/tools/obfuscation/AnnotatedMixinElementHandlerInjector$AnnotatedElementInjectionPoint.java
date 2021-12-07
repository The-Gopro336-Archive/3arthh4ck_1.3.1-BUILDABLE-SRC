package org.spongepowered.tools.obfuscation;

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.ExecutableElement;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.struct.InjectorRemap;

class AnnotatedMixinElementHandlerInjector$AnnotatedElementInjectionPoint
extends AnnotatedMixinElementHandler.AnnotatedElement<ExecutableElement> {
    private final AnnotationHandle at;
    private Map<String, String> args;
    private final InjectorRemap state;

    public AnnotatedMixinElementHandlerInjector$AnnotatedElementInjectionPoint(ExecutableElement element, AnnotationHandle inject, AnnotationHandle at, InjectorRemap state) {
        super(element, inject);
        this.at = at;
        this.state = state;
    }

    public boolean shouldRemap() {
        return this.at.getBoolean("remap", this.state.shouldRemap());
    }

    public AnnotationHandle getAt() {
        return this.at;
    }

    public String getAtArg(String key) {
        if (this.args == null) {
            this.args = new HashMap<String, String>();
            for (String arg : this.at.getList("args")) {
                if (arg == null) continue;
                int eqPos = arg.indexOf(61);
                if (eqPos > -1) {
                    this.args.put(arg.substring(0, eqPos), arg.substring(eqPos + 1));
                    continue;
                }
                this.args.put(arg, "");
            }
        }
        return this.args.get(key);
    }

    public void notifyRemapped() {
        this.state.notifyRemapped();
    }
}
