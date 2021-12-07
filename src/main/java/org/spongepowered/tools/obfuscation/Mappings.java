/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.tools.obfuscation;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;

class Mappings
implements IMappingConsumer {
    private final Map<ObfuscationType, IMappingConsumer.MappingSet<MappingField>> fieldMappings = new HashMap<ObfuscationType, IMappingConsumer.MappingSet<MappingField>>();
    private final Map<ObfuscationType, IMappingConsumer.MappingSet<MappingMethod>> methodMappings = new HashMap<ObfuscationType, IMappingConsumer.MappingSet<MappingMethod>>();
    private UniqueMappings unique;

    public Mappings() {
        this.init();
    }

    private void init() {
        for (ObfuscationType obfType : ObfuscationType.types()) {
            this.fieldMappings.put(obfType, new IMappingConsumer.MappingSet());
            this.methodMappings.put(obfType, new IMappingConsumer.MappingSet());
        }
    }

    public IMappingConsumer asUnique() {
        if (this.unique == null) {
            this.unique = new UniqueMappings(this);
        }
        return this.unique;
    }

    @Override
    public IMappingConsumer.MappingSet<MappingField> getFieldMappings(ObfuscationType type) {
        IMappingConsumer.MappingSet mappings = this.fieldMappings.get(type);
        return mappings != null ? mappings : new IMappingConsumer.MappingSet();
    }

    @Override
    public IMappingConsumer.MappingSet<MappingMethod> getMethodMappings(ObfuscationType type) {
        IMappingConsumer.MappingSet mappings = this.methodMappings.get(type);
        return mappings != null ? mappings : new IMappingConsumer.MappingSet();
    }

    @Override
    public void clear() {
        this.fieldMappings.clear();
        this.methodMappings.clear();
        if (this.unique != null) {
            this.unique.clearMaps();
        }
        this.init();
    }

    @Override
    public void addFieldMapping(ObfuscationType type, MappingField from, MappingField to) {
        IMappingConsumer.MappingSet<MappingField> mappings = this.fieldMappings.get(type);
        if (mappings == null) {
            mappings = new IMappingConsumer.MappingSet();
            this.fieldMappings.put(type, mappings);
        }
        mappings.add(new IMappingConsumer.MappingSet.Pair<MappingField>(from, to));
    }

    @Override
    public void addMethodMapping(ObfuscationType type, MappingMethod from, MappingMethod to) {
        IMappingConsumer.MappingSet<MappingMethod> mappings = this.methodMappings.get(type);
        if (mappings == null) {
            mappings = new IMappingConsumer.MappingSet();
            this.methodMappings.put(type, mappings);
        }
        mappings.add(new IMappingConsumer.MappingSet.Pair<MappingMethod>(from, to));
    }

    static class UniqueMappings
    implements IMappingConsumer {
        private final IMappingConsumer mappings;
        private final Map<ObfuscationType, Map<MappingField, MappingField>> fields = new HashMap<ObfuscationType, Map<MappingField, MappingField>>();
        private final Map<ObfuscationType, Map<MappingMethod, MappingMethod>> methods = new HashMap<ObfuscationType, Map<MappingMethod, MappingMethod>>();

        public UniqueMappings(IMappingConsumer mappings) {
            this.mappings = mappings;
        }

        @Override
        public void clear() {
            this.clearMaps();
            this.mappings.clear();
        }

        protected void clearMaps() {
            this.fields.clear();
            this.methods.clear();
        }

        @Override
        public void addFieldMapping(ObfuscationType type, MappingField from, MappingField to) {
            if (!this.checkForExistingMapping(type, from, to, this.fields)) {
                this.mappings.addFieldMapping(type, from, to);
            }
        }

        @Override
        public void addMethodMapping(ObfuscationType type, MappingMethod from, MappingMethod to) {
            if (!this.checkForExistingMapping(type, from, to, this.methods)) {
                this.mappings.addMethodMapping(type, from, to);
            }
        }

        private <TMapping extends IMapping<TMapping>> boolean checkForExistingMapping(ObfuscationType type, TMapping from, TMapping to, Map<ObfuscationType, Map<TMapping, TMapping>> mappings) throws MappingConflictException {
            IMapping existing;
            Map<TMapping, TMapping> existingMappings = mappings.get(type);
            if (existingMappings == null) {
                existingMappings = new HashMap<TMapping, TMapping>();
                mappings.put(type, existingMappings);
            }
            if ((existing = (IMapping)existingMappings.get(from)) != null) {
                if (existing.equals(to)) {
                    return true;
                }
                throw new MappingConflictException(existing, to);
            }
            existingMappings.put(from, to);
            return false;
        }

        @Override
        public IMappingConsumer.MappingSet<MappingField> getFieldMappings(ObfuscationType type) {
            return this.mappings.getFieldMappings(type);
        }

        @Override
        public IMappingConsumer.MappingSet<MappingMethod> getMethodMappings(ObfuscationType type) {
            return this.mappings.getMethodMappings(type);
        }
    }

    public static class MappingConflictException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final IMapping<?> oldMapping;
        private final IMapping<?> newMapping;

        public MappingConflictException(IMapping<?> oldMapping, IMapping<?> newMapping) {
            this.oldMapping = oldMapping;
            this.newMapping = newMapping;
        }

        public IMapping<?> getOld() {
            return this.oldMapping;
        }

        public IMapping<?> getNew() {
            return this.newMapping;
        }
    }
}

