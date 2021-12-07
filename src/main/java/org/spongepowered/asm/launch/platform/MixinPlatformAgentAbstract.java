package org.spongepowered.asm.launch.platform;

import java.io.File;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.platform.IMixinPlatformAgent;
import org.spongepowered.asm.launch.platform.MainAttributes;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;

public abstract class MixinPlatformAgentAbstract
implements IMixinPlatformAgent {
    protected static final Logger logger = LogManager.getLogger("mixin");
    protected final MixinPlatformManager manager;
    protected final URI uri;
    protected final File container;
    protected final MainAttributes attributes;

    public MixinPlatformAgentAbstract(MixinPlatformManager manager, URI uri) {
        this.manager = manager;
        this.uri = uri;
        this.container = this.uri != null ? new File(this.uri) : null;
        this.attributes = MainAttributes.of(uri);
    }

    public String toString() {
        return String.format("PlatformAgent[%s:%s]", this.getClass().getSimpleName(), this.uri);
    }

    @Override
    public String getPhaseProvider() {
        return null;
    }
}
