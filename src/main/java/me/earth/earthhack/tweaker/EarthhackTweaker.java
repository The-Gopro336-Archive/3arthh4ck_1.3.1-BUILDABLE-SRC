/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.ITweaker
 *  net.minecraft.launchwrapper.Launch
 *  net.minecraft.launchwrapper.LaunchClassLoader
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package me.earth.earthhack.tweaker;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.earth.earthhack.tweaker.TweakerCore;
import me.earth.earthhack.tweaker.launch.DevArguments;
import me.earth.earthhack.tweaker.launch.arguments.BooleanArgument;
import me.earth.earthhack.tweaker.launch.arguments.LongArgument;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinTweaker;

public class EarthhackTweaker
implements ITweaker {
    private Map<String, String> launchArgs;
    private final MixinTweaker wrapped = new MixinTweaker();

    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        try {
            String className = "me.earth.earthhack.vanilla.Environment";
            Class<?> env = Class.forName(className, true, (ClassLoader)Launch.classLoader);
            Method load = env.getDeclaredMethod("loadEnvironment", new Class[0]);
            load.setAccessible(true);
            load.invoke(null, new Object[0]);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Object obj = Launch.blackboard.get("launchArgs");
        if (obj == null) {
            this.launchArgs = new HashMap<String, String>();
            String classifier = null;
            for (String arg : args) {
                if (arg.startsWith("-")) {
                    if (classifier != null) {
                        classifier = this.launchArgs.put(classifier, "");
                        continue;
                    }
                    if (arg.contains("=")) {
                        classifier = this.launchArgs.put(arg.substring(0, arg.indexOf(61)), arg.substring(arg.indexOf(61) + 1));
                        continue;
                    }
                    classifier = arg;
                    continue;
                }
                if (classifier == null) continue;
                classifier = this.launchArgs.put(classifier, arg);
            }
            if (!this.launchArgs.containsKey("--version")) {
                this.launchArgs.put("--version", profile != null ? profile : "3arthh4ck-Profile");
            }
            if (!this.launchArgs.containsKey("--gameDir") && gameDir != null) {
                this.launchArgs.put("--gameDir", gameDir.getAbsolutePath());
            }
            if (!this.launchArgs.containsKey("--assetsDir") && assetsDir != null) {
                this.launchArgs.put("--assetsDir", assetsDir.getAbsolutePath());
            }
        }
        this.wrapped.acceptOptions(args, gameDir, assetsDir, profile);
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        this.wrapped.injectIntoClassLoader(classLoader);
        try {
            String className = "me.earth.earthhack.impl.core.Core";
            classLoader.addTransformerExclusion(className);
            Class<?> coreClass = Class.forName(className, true, (ClassLoader)classLoader);
            TweakerCore core = (TweakerCore)coreClass.newInstance();
            Logger logger = LogManager.getLogger((String)"3arthh4ck-Core");
            logger.info("\n\n");
            this.loadDevArguments();
            core.init((ClassLoader)classLoader);
            for (String transformer : core.getTransformers()) {
                classLoader.registerTransformer(transformer);
            }
            logger.info("\n\n");
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getLaunchTarget() {
        return this.wrapped.getLaunchTarget();
    }

    public String[] getLaunchArguments() {
        List al = (List)Launch.blackboard.get("ArgumentList");
        if (al.isEmpty() && this.launchArgs != null && Launch.blackboard.get("launchArgs") == null) {
            ArrayList<String> args = new ArrayList<String>(this.launchArgs.size() * 2);
            for (Map.Entry<String, String> arg : this.launchArgs.entrySet()) {
                args.add(arg.getKey());
                args.add(arg.getValue());
            }
            return args.toArray(new String[0]);
        }
        return this.wrapped.getLaunchArguments();
    }

    private void loadDevArguments() {
        DevArguments dev = DevArguments.getInstance();
        dev.addArgument("inventory", new BooleanArgument());
        dev.addArgument("inventorymp", new BooleanArgument());
        dev.addArgument("totems", new BooleanArgument());
        dev.addArgument("dead", new BooleanArgument(Boolean.FALSE));
        dev.addArgument("jsrn", new BooleanArgument());
        dev.addArgument("jsnull", new BooleanArgument(Boolean.FALSE));
        dev.addArgument("connection", new LongArgument(800L));
        dev.addArgument("leijurvpos", new BooleanArgument(Boolean.TRUE));
        dev.loadArguments();
    }
}

