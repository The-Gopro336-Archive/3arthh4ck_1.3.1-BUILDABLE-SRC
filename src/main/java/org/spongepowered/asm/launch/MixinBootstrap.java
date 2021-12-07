package org.spongepowered.asm.launch;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.MixinInitialisationError;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.MixinService;

public abstract class MixinBootstrap {
    public static final String VERSION = "0.7.11";
    private static final Logger logger = LogManager.getLogger("mixin");
    private static boolean initialised = false;
    private static boolean initState = true;
    private static MixinPlatformManager platform;

    private MixinBootstrap() {
    }

    @Deprecated
    public static void addProxy() {
        MixinService.getService().beginPhase();
    }

    public static MixinPlatformManager getPlatform() {
        if (platform == null) {
            Object globalPlatformManager = GlobalProperties.get("mixin.platform");
            if (globalPlatformManager instanceof MixinPlatformManager) {
                platform = (MixinPlatformManager)globalPlatformManager;
            } else {
                platform = new MixinPlatformManager();
                GlobalProperties.put("mixin.platform", platform);
                platform.init();
            }
        }
        return platform;
    }

    public static void init() {
        if (!MixinBootstrap.start()) {
            return;
        }
        MixinBootstrap.doInit(null);
    }

    static boolean start() {
        if (MixinBootstrap.isSubsystemRegistered()) {
            if (!MixinBootstrap.checkSubsystemVersion()) {
                throw new MixinInitialisationError("Mixin subsystem version " + MixinBootstrap.getActiveSubsystemVersion() + " was already initialised. Cannot bootstrap version " + VERSION);
            }
            return false;
        }
        MixinBootstrap.registerSubsystem(VERSION);
        if (!initialised) {
            MixinEnvironment.Phase initialPhase;
            initialised = true;
            String command = System.getProperty("sun.java.command");
            if (command != null && command.contains("GradleStart")) {
                System.setProperty("mixin.env.remapRefMap", "true");
            }
            if ((initialPhase = MixinService.getService().getInitialPhase()) == MixinEnvironment.Phase.DEFAULT) {
                logger.error("Initialising mixin subsystem after game pre-init phase! Some mixins may be skipped.");
                MixinEnvironment.init(initialPhase);
                MixinBootstrap.getPlatform().prepare(null);
                initState = false;
            } else {
                MixinEnvironment.init(initialPhase);
            }
            MixinService.getService().beginPhase();
        }
        MixinBootstrap.getPlatform();
        return true;
    }

    static void doInit(List<String> args) {
        if (!initialised) {
            if (MixinBootstrap.isSubsystemRegistered()) {
                logger.warn("Multiple Mixin containers present, init suppressed for 0.7.11");
                return;
            }
            throw new IllegalStateException("MixinBootstrap.doInit() called before MixinBootstrap.start()");
        }
        MixinBootstrap.getPlatform().getPhaseProviderClasses();
        if (initState) {
            MixinBootstrap.getPlatform().prepare(args);
            MixinService.getService().init();
        }
    }

    static void inject() {
        MixinBootstrap.getPlatform().inject();
    }

    private static boolean isSubsystemRegistered() {
        return GlobalProperties.get("mixin.initialised") != null;
    }

    private static boolean checkSubsystemVersion() {
        return VERSION.equals(MixinBootstrap.getActiveSubsystemVersion());
    }

    private static Object getActiveSubsystemVersion() {
        Object version = GlobalProperties.get("mixin.initialised");
        return version != null ? version : "";
    }

    private static void registerSubsystem(String version) {
        GlobalProperties.put("mixin.initialised", version);
    }

    static {
        MixinService.boot();
        MixinService.getService().prepare();
    }
}
