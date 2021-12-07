package org.spongepowered.asm.launch.platform;

import java.net.URI;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;

public class MixinPlatformAgentDefault
extends MixinPlatformAgentAbstract {
    public MixinPlatformAgentDefault(MixinPlatformManager manager, URI uri) {
        super(manager, uri);
    }

    @Override
    public void prepare() {
        String tokenProviders;
        String mixinConfigs;
        String compatibilityLevel = this.attributes.get("MixinCompatibilityLevel");
        if (compatibilityLevel != null) {
            this.manager.setCompatibilityLevel(compatibilityLevel);
        }
        if ((mixinConfigs = this.attributes.get("MixinConfigs")) != null) {
            for (String config : mixinConfigs.split(",")) {
                this.manager.addConfig(config.trim());
            }
        }
        if ((tokenProviders = this.attributes.get("MixinTokenProviders")) != null) {
            for (String provider : tokenProviders.split(",")) {
                this.manager.addTokenProvider(provider.trim());
            }
        }
    }

    @Override
    public void initPrimaryContainer() {
    }

    @Override
    public void inject() {
    }

    @Override
    public String getLaunchTarget() {
        return this.attributes.get("Main-Class");
    }
}
