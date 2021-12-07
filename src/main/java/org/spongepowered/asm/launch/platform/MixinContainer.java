package org.spongepowered.asm.launch.platform;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.platform.IMixinPlatformAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.service.MixinService;

public class MixinContainer {
    private static final List<String> agentClasses = new ArrayList<String>();
    private final Logger logger = LogManager.getLogger("mixin");
    private final URI uri;
    private final List<IMixinPlatformAgent> agents = new ArrayList<IMixinPlatformAgent>();

    public MixinContainer(MixinPlatformManager manager, URI uri) {
        this.uri = uri;
        for (String agentClass : agentClasses) {
            try {
                Class<?> clazz = Class.forName(agentClass);
                Constructor<?> ctor = clazz.getDeclaredConstructor(MixinPlatformManager.class, URI.class);
                this.logger.debug("Instancing new {} for {}", new Object[]{clazz.getSimpleName(), this.uri});
                IMixinPlatformAgent agent = (IMixinPlatformAgent)ctor.newInstance(manager, uri);
                this.agents.add(agent);
            }
            catch (Exception ex) {
                this.logger.catching(ex);
            }
        }
    }

    public URI getURI() {
        return this.uri;
    }

    public Collection<String> getPhaseProviders() {
        ArrayList<String> phaseProviders = new ArrayList<String>();
        for (IMixinPlatformAgent agent : this.agents) {
            String phaseProvider = agent.getPhaseProvider();
            if (phaseProvider == null) continue;
            phaseProviders.add(phaseProvider);
        }
        return phaseProviders;
    }

    public void prepare() {
        for (IMixinPlatformAgent agent : this.agents) {
            this.logger.debug("Processing prepare() for {}", new Object[]{agent});
            agent.prepare();
        }
    }

    public void initPrimaryContainer() {
        for (IMixinPlatformAgent agent : this.agents) {
            this.logger.debug("Processing launch tasks for {}", new Object[]{agent});
            agent.initPrimaryContainer();
        }
    }

    public void inject() {
        for (IMixinPlatformAgent agent : this.agents) {
            this.logger.debug("Processing inject() for {}", new Object[]{agent});
            agent.inject();
        }
    }

    public String getLaunchTarget() {
        for (IMixinPlatformAgent agent : this.agents) {
            String launchTarget = agent.getLaunchTarget();
            if (launchTarget == null) continue;
            return launchTarget;
        }
        return null;
    }

    static {
        GlobalProperties.put("mixin.agents", agentClasses);
        for (String agent : MixinService.getService().getPlatformAgents()) {
            agentClasses.add(agent);
        }
        agentClasses.add("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    }
}
