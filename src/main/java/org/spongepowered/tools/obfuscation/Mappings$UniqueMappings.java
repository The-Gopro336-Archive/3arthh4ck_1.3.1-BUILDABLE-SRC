package org.spongepowered.tools.obfuscation;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.Mappings;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;

class Mappings$UniqueMappings
implements IMappingConsumer {
    private final IMappingConsumer mappings;
    private final Map<ObfuscationType, Map<MappingField, MappingField>> fields = new HashMap<ObfuscationType, Map<MappingField, MappingField>>();
    private final Map<ObfuscationType, Map<MappingMethod, MappingMethod>> methods = new HashMap<ObfuscationType, Map<MappingMethod, MappingMethod>>();

    public Mappings$UniqueMappings(IMappingConsumer mappings) {
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

    private <TMapping extends IMapping<TMapping>> boolean checkForExistingMapping(ObfuscationType type, TMapping from, TMapping to, Map<ObfuscationType, Map<TMapping, TMapping>> mappings) throws Mappings.MappingConflictException {
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
            throw new Mappings.MappingConflictException(existing, to);
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
