package org.spongepowered.asm.service.mojang;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.ILegacyClassTransformer;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.mojang.LaunchClassLoaderUtil;
import org.spongepowered.asm.service.mojang.LegacyTransformerHandle;
import org.spongepowered.asm.util.ReEntranceLock;
import org.spongepowered.asm.util.perf.Profiler;

public class MixinServiceLaunchWrapper
implements IMixinService,
IClassProvider,
IClassBytecodeProvider {
    public static final String BLACKBOARD_KEY_TWEAKCLASSES = "TweakClasses";
    public static final String BLACKBOARD_KEY_TWEAKS = "Tweaks";
    private static final String LAUNCH_PACKAGE = "org.spongepowered.asm.launch.";
    private static final String MIXIN_PACKAGE = "org.spongepowered.asm.mixin.";
    private static final String STATE_TWEAKER = "org.spongepowered.asm.mixin.EnvironmentStateTweaker";
    private static final String TRANSFORMER_PROXY_CLASS = "org.spongepowered.asm.mixin.transformer.Proxy";
    private static final Logger logger = LogManager.getLogger("mixin");
    private final LaunchClassLoaderUtil classLoaderUtil = new LaunchClassLoaderUtil(Launch.classLoader);
    private final ReEntranceLock lock = new ReEntranceLock(1);
    private IClassNameTransformer nameTransformer;

    @Override
    public String getName() {
        return "LaunchWrapper";
    }

    @Override
    public boolean isValid() {
        try {
            Launch.classLoader.hashCode();
        }
        catch (Throwable ex) {
            return false;
        }
        return true;
    }

    @Override
    public void prepare() {
        Launch.classLoader.addClassLoaderExclusion(LAUNCH_PACKAGE);
    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        if (MixinServiceLaunchWrapper.findInStackTrace("net.minecraft.launchwrapper.Launch", "launch") > 132) {
            return MixinEnvironment.Phase.DEFAULT;
        }
        return MixinEnvironment.Phase.PREINIT;
    }

    @Override
    public void init() {
        List tweakClasses;
        if (MixinServiceLaunchWrapper.findInStackTrace("net.minecraft.launchwrapper.Launch", "launch") < 4) {
            logger.error("MixinBootstrap.doInit() called during a tweak constructor!");
        }
        if ((tweakClasses = (List)GlobalProperties.get(BLACKBOARD_KEY_TWEAKCLASSES)) != null) {
            tweakClasses.add(STATE_TWEAKER);
        }
    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return this.lock;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return ImmutableList.of("org.spongepowered.asm.launch.platform.MixinPlatformAgentFML");
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Launch.classLoader.findClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Launch.classLoader);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Launch.class.getClassLoader());
    }

    @Override
    public void beginPhase() {
        Launch.classLoader.registerTransformer(TRANSFORMER_PROXY_CLASS);
    }

    @Override
    public void checkEnv(Object bootSource) {
        if (bootSource.getClass().getClassLoader() != Launch.class.getClassLoader()) {
            throw new MixinException("Attempted to init the mixin environment in the wrong classloader");
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Launch.classLoader.getResourceAsStream(name);
    }

    @Override
    public void registerInvalidClass(String className) {
        this.classLoaderUtil.registerInvalidClass(className);
    }

    @Override
    public boolean isClassLoaded(String className) {
        return this.classLoaderUtil.isClassLoaded(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        String restrictions = "";
        if (this.classLoaderUtil.isClassClassLoaderExcluded(className, null)) {
            restrictions = "PACKAGE_CLASSLOADER_EXCLUSION";
        }
        if (this.classLoaderUtil.isClassTransformerExcluded(className, null)) {
            restrictions = (restrictions.length() > 0 ? restrictions + "," : "") + "PACKAGE_TRANSFORMER_EXCLUSION";
        }
        return restrictions;
    }

    @Override
    public URL[] getClassPath() {
        return Launch.classLoader.getSources().toArray(new URL[0]);
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        List transformers = Launch.classLoader.getTransformers();
        ArrayList<ITransformer> wrapped = new ArrayList<ITransformer>(transformers.size());
        for (IClassTransformer transformer : transformers) {
            if (transformer instanceof ITransformer) {
                wrapped.add((ITransformer)((Object)transformer));
            } else {
                wrapped.add(new LegacyTransformerHandle(transformer));
            }
            if (!(transformer instanceof IClassNameTransformer)) continue;
            logger.debug("Found name transformer: {}", new Object[]{transformer.getClass().getName()});
            this.nameTransformer = (IClassNameTransformer)((Object)transformer);
        }
        return wrapped;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getClassBytes(String name, String transformedName) throws IOException {
        byte[] classBytes = Launch.classLoader.getClassBytes(name);
        if (classBytes != null) {
            return classBytes;
        }
        URLClassLoader appClassLoader = (URLClassLoader)Launch.class.getClassLoader();
        InputStream classStream = null;
        try {
            String resourcePath = transformedName.replace('.', '/').concat(".class");
            classStream = appClassLoader.getResourceAsStream(resourcePath);
            byte[] byArray = IOUtils.toByteArray(classStream);
            IOUtils.closeQuietly(classStream);
            return byArray;
        }
        catch (Exception ex) {
            byte[] byArray = null;
            return byArray;
        }
        finally {
            IOUtils.closeQuietly(classStream);
        }
    }

    @Override
    public byte[] getClassBytes(String className, boolean runTransformers) throws ClassNotFoundException, IOException {
        String transformedName = className.replace('/', '.');
        String name = this.unmapClassName(transformedName);
        Profiler profiler = MixinEnvironment.getProfiler();
        Profiler.Section loadTime = profiler.begin(1, "class.load");
        byte[] classBytes = this.getClassBytes(name, transformedName);
        loadTime.end();
        if (runTransformers) {
            Profiler.Section transformTime = profiler.begin(1, "class.transform");
            classBytes = this.applyTransformers(name, transformedName, classBytes, profiler);
            transformTime.end();
        }
        if (classBytes == null) {
            throw new ClassNotFoundException(String.format("The specified class '%s' was not found", transformedName));
        }
        return classBytes;
    }

    private byte[] applyTransformers(String name, String transformedName, byte[] basicClass, Profiler profiler) {
        if (this.classLoaderUtil.isClassExcluded(name, transformedName)) {
            return basicClass;
        }
        MixinEnvironment environment = MixinEnvironment.getCurrentEnvironment();
        for (ILegacyClassTransformer transformer : environment.getTransformers()) {
            this.lock.clear();
            int pos = transformer.getName().lastIndexOf(46);
            String simpleName = transformer.getName().substring(pos + 1);
            Profiler.Section transformTime = profiler.begin(2, simpleName.toLowerCase());
            transformTime.setInfo(transformer.getName());
            basicClass = transformer.transformClassBytes(name, transformedName, basicClass);
            transformTime.end();
            if (!this.lock.isSet()) continue;
            environment.addTransformerExclusion(transformer.getName());
            this.lock.clear();
            logger.info("A re-entrant transformer '{}' was detected and will no longer process meta class data", new Object[]{transformer.getName()});
        }
        return basicClass;
    }

    private String unmapClassName(String className) {
        if (this.nameTransformer == null) {
            this.findNameTransformer();
        }
        if (this.nameTransformer != null) {
            return this.nameTransformer.unmapClassName(className);
        }
        return className;
    }

    private void findNameTransformer() {
        List transformers = Launch.classLoader.getTransformers();
        for (IClassTransformer transformer : transformers) {
            if (!(transformer instanceof IClassNameTransformer)) continue;
            logger.debug("Found name transformer: {}", new Object[]{transformer.getClass().getName()});
            this.nameTransformer = (IClassNameTransformer)((Object)transformer);
        }
    }

    @Override
    public ClassNode getClassNode(String className) throws ClassNotFoundException, IOException {
        return this.getClassNode(this.getClassBytes(className, true), 0);
    }

    private ClassNode getClassNode(byte[] classBytes, int flags) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, flags);
        return classNode;
    }

    @Override
    public final String getSideName() {
        for (ITweaker tweaker : (List)GlobalProperties.get(BLACKBOARD_KEY_TWEAKS)) {
            if (tweaker.getClass().getName().endsWith(".common.launcher.FMLServerTweaker")) {
                return "SERVER";
            }
            if (!tweaker.getClass().getName().endsWith(".common.launcher.FMLTweaker")) continue;
            return "CLIENT";
        }
        String name = this.getSideName("net.minecraftforge.fml.relauncher.FMLLaunchHandler", "side");
        if (name != null) {
            return name;
        }
        name = this.getSideName("cpw.mods.fml.relauncher.FMLLaunchHandler", "side");
        if (name != null) {
            return name;
        }
        name = this.getSideName("com.mumfrey.liteloader.launch.LiteLoaderTweaker", "getEnvironmentType");
        if (name != null) {
            return name;
        }
        return "UNKNOWN";
    }

    private String getSideName(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className, false, Launch.classLoader);
            Method method = clazz.getDeclaredMethod(methodName, new Class[0]);
            return ((Enum)method.invoke(null, new Object[0])).name();
        }
        catch (Exception ex) {
            return null;
        }
    }

    private static int findInStackTrace(String className, String methodName) {
        StackTraceElement[] stackTrace;
        Thread currentThread = Thread.currentThread();
        if (!"main".equals(currentThread.getName())) {
            return 0;
        }
        for (StackTraceElement s : stackTrace = currentThread.getStackTrace()) {
            if (!className.equals(s.getClassName()) || !methodName.equals(s.getMethodName())) continue;
            return s.getLineNumber();
        }
        return 0;
    }
}
