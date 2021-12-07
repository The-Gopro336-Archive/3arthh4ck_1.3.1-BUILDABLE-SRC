package org.spongepowered.asm.bridge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.commons.Remapper;
import org.spongepowered.asm.mixin.extensibility.IRemapper;
import org.spongepowered.asm.util.ObfuscationUtil;

public abstract class RemapperAdapter
implements IRemapper,
ObfuscationUtil.IClassRemapper {
    protected final Logger logger = LogManager.getLogger("mixin");
    protected final Remapper remapper;

    public RemapperAdapter(Remapper remapper) {
        this.remapper = remapper;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        this.logger.debug("{} is remapping method {}{} for {}", new Object[]{this, name, desc, owner});
        String newName = this.remapper.mapMethodName(owner, name, desc);
        if (!newName.equals(name)) {
            return newName;
        }
        String obfOwner = this.unmap(owner);
        String obfDesc = this.unmapDesc(desc);
        this.logger.debug("{} is remapping obfuscated method {}{} for {}", new Object[]{this, name, obfDesc, obfOwner});
        return this.remapper.mapMethodName(obfOwner, name, obfDesc);
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        this.logger.debug("{} is remapping field {}{} for {}", new Object[]{this, name, desc, owner});
        String newName = this.remapper.mapFieldName(owner, name, desc);
        if (!newName.equals(name)) {
            return newName;
        }
        String obfOwner = this.unmap(owner);
        String obfDesc = this.unmapDesc(desc);
        this.logger.debug("{} is remapping obfuscated field {}{} for {}", new Object[]{this, name, obfDesc, obfOwner});
        return this.remapper.mapFieldName(obfOwner, name, obfDesc);
    }

    @Override
    public String map(String typeName) {
        this.logger.debug("{} is remapping class {}", new Object[]{this, typeName});
        return this.remapper.map(typeName);
    }

    @Override
    public String unmap(String typeName) {
        return typeName;
    }

    @Override
    public String mapDesc(String desc) {
        return this.remapper.mapDesc(desc);
    }

    @Override
    public String unmapDesc(String desc) {
        return ObfuscationUtil.unmapDescriptor(desc, this);
    }
}
