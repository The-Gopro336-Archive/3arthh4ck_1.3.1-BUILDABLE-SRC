/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package me.earth.earthhack.impl.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.core.transfomer.EarthhackTransformer;
import me.earth.earthhack.impl.core.util.MixinHelper;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.tweaker.TweakerCore;
import me.earth.earthhack.vanilla.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class Core
implements TweakerCore {
    public static final Logger LOGGER = LogManager.getLogger((String)"3arthh4ck-Core");

    @Override
    public void init(ClassLoader pluginClassLoader) {
        String extraMixin;
        LOGGER.info("Initializing 3arthh4cks Core.");
        LOGGER.info("Found Environment: " + (Object)((Object)Environment.getEnvironment()));
        Bus.EVENT_BUS.subscribe(Scheduler.getInstance());
        Path path = Paths.get("earthhack", new String[0]);
        FileUtil.createDirectory(path);
        FileUtil.getDirectory(path, "util");
        FileUtil.getDirectory(path, "plugins");
        MixinHelper helper = MixinHelper.getHelper();
        PluginManager.getInstance().createPluginConfigs(pluginClassLoader);
        MixinBootstrap.init();
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT).setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT).setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT).setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT).setSide(MixinEnvironment.Side.CLIENT);
        if (Environment.hasForge()) {
            LOGGER.info("Forge detected!");
            extraMixin = "mixins.forge.json";
        } else {
            LOGGER.info("No Forge!");
            extraMixin = "mixins.vanilla.json";
        }
        Mixins.addConfiguration(extraMixin);
        for (PluginConfig config : PluginManager.getInstance().getConfigs().values()) {
            if (config.getMixinConfig() == null) continue;
            LOGGER.info("Adding " + config.getName() + "'s MixinConfig: " + config.getMixinConfig());
            helper.addConfigExclusion(config.getMixinConfig());
            Mixins.addConfiguration(config.getMixinConfig());
        }
        helper.addConfigExclusion("mixins.earth.json");
        Mixins.addConfiguration("mixins.earth.json");
        String obfuscationContext = "searge";
        if (Environment.getEnvironment() == Environment.VANILLA) {
            obfuscationContext = "notch";
        }
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext(obfuscationContext);
    }

    @Override
    public String[] getTransformers() {
        return new String[]{EarthhackTransformer.class.getName()};
    }
}

