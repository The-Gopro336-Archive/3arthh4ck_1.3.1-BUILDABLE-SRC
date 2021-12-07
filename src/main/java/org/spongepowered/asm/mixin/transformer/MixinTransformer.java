/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.mixin.transformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.ArgsClassGenerator;
import org.spongepowered.asm.mixin.throwables.ClassAlreadyLoadedException;
import org.spongepowered.asm.mixin.throwables.MixinApplyError;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.mixin.throwables.MixinPrepareError;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.mixin.transformer.InnerClassGenerator;
import org.spongepowered.asm.mixin.transformer.MixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPostProcessor;
import org.spongepowered.asm.mixin.transformer.TargetClassContext;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IClassGenerator;
import org.spongepowered.asm.mixin.transformer.ext.IHotSwap;
import org.spongepowered.asm.mixin.transformer.ext.extensions.ExtensionCheckClass;
import org.spongepowered.asm.mixin.transformer.ext.extensions.ExtensionCheckInterfaces;
import org.spongepowered.asm.mixin.transformer.ext.extensions.ExtensionClassExporter;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.transformers.TreeTransformer;
import org.spongepowered.asm.util.PrettyPrinter;
import org.spongepowered.asm.util.ReEntranceLock;
import org.spongepowered.asm.util.perf.Profiler;

public class MixinTransformer
extends TreeTransformer {
    private static final String MIXIN_AGENT_CLASS = "org.spongepowered.tools.agent.MixinAgent";
    private static final String METRONOME_AGENT_CLASS = "org.spongepowered.metronome.Agent";
    static final Logger logger = LogManager.getLogger((String)"mixin");
    private final IMixinService service = MixinService.getService();
    private final List<MixinConfig> configs = new ArrayList<MixinConfig>();
    private final List<MixinConfig> pendingConfigs = new ArrayList<MixinConfig>();
    private final ReEntranceLock lock;
    private final String sessionId = UUID.randomUUID().toString();
    private final Extensions extensions;
    private final IHotSwap hotSwapper;
    private final MixinPostProcessor postProcessor;
    private final Profiler profiler;
    private MixinEnvironment currentEnvironment;
    private Level verboseLoggingLevel = Level.DEBUG;
    private boolean errorState = false;
    private int transformedCount = 0;

    MixinTransformer() {
        MixinEnvironment environment = MixinEnvironment.getCurrentEnvironment();
        Object globalMixinTransformer = environment.getActiveTransformer();
        if (globalMixinTransformer instanceof ITransformer) {
            throw new MixinException("Terminating MixinTransformer instance " + this);
        }
        environment.setActiveTransformer(this);
        this.lock = this.service.getReEntranceLock();
        this.extensions = new Extensions(this);
        this.hotSwapper = this.initHotSwapper(environment);
        this.postProcessor = new MixinPostProcessor();
        this.extensions.add(new ArgsClassGenerator());
        this.extensions.add(new InnerClassGenerator());
        this.extensions.add(new ExtensionClassExporter(environment));
        this.extensions.add(new ExtensionCheckClass());
        this.extensions.add(new ExtensionCheckInterfaces());
        this.profiler = MixinEnvironment.getProfiler();
    }

    private IHotSwap initHotSwapper(MixinEnvironment environment) {
        if (!environment.getOption(MixinEnvironment.Option.HOT_SWAP)) {
            return null;
        }
        try {
            logger.info("Attempting to load Hot-Swap agent");
            Class<?> clazz = Class.forName(MIXIN_AGENT_CLASS);
            Constructor<?> ctor = clazz.getDeclaredConstructor(MixinTransformer.class);
            return (IHotSwap)ctor.newInstance(this);
        }
        catch (Throwable th) {
            logger.info("Hot-swap agent could not be loaded, hot swapping of mixins won't work. {}: {}", new Object[]{th.getClass().getSimpleName(), th.getMessage()});
            return null;
        }
    }

    public void audit(MixinEnvironment environment) {
        HashSet<String> unhandled = new HashSet<String>();
        for (MixinConfig config : this.configs) {
            unhandled.addAll(config.getUnhandledTargets());
        }
        Logger auditLogger = LogManager.getLogger((String)"mixin/audit");
        for (String target : unhandled) {
            try {
                auditLogger.info("Force-loading class {}", new Object[]{target});
                this.service.getClassProvider().findClass(target, true);
            }
            catch (ClassNotFoundException ex) {
                auditLogger.error("Could not force-load " + target, (Throwable)ex);
            }
        }
        for (MixinConfig config : this.configs) {
            for (String target : config.getUnhandledTargets()) {
                ClassAlreadyLoadedException ex = new ClassAlreadyLoadedException(target + " was already classloaded");
                auditLogger.error("Could not force-load " + target, (Throwable)ex);
            }
        }
        if (environment.getOption(MixinEnvironment.Option.DEBUG_PROFILER)) {
            this.printProfilerSummary();
        }
    }

    private void printProfilerSummary() {
        DecimalFormat threedp = new DecimalFormat("(###0.000");
        DecimalFormat onedp = new DecimalFormat("(###0.0");
        PrettyPrinter printer = this.profiler.printer(false, false);
        long prepareTime = this.profiler.get("mixin.prepare").getTotalTime();
        long readTime = this.profiler.get("mixin.read").getTotalTime();
        long applyTime = this.profiler.get("mixin.apply").getTotalTime();
        long writeTime = this.profiler.get("mixin.write").getTotalTime();
        long totalMixinTime = this.profiler.get("mixin").getTotalTime();
        long loadTime = this.profiler.get("class.load").getTotalTime();
        long transformTime = this.profiler.get("class.transform").getTotalTime();
        long exportTime = this.profiler.get("mixin.debug.export").getTotalTime();
        long actualTime = totalMixinTime - loadTime - transformTime - exportTime;
        double timeSliceMixin = (double)actualTime / (double)totalMixinTime * 100.0;
        double timeSliceLoad = (double)loadTime / (double)totalMixinTime * 100.0;
        double timeSliceTransform = (double)transformTime / (double)totalMixinTime * 100.0;
        double timeSliceExport = (double)exportTime / (double)totalMixinTime * 100.0;
        long worstTransformerTime = 0L;
        Profiler.Section worstTransformer = null;
        for (Profiler.Section section : this.profiler.getSections()) {
            long transformerTime = section.getName().startsWith("class.transform.") ? section.getTotalTime() : 0L;
            if (transformerTime <= worstTransformerTime) continue;
            worstTransformerTime = transformerTime;
            worstTransformer = section;
        }
        printer.hr().add("Summary").hr().add();
        String format = "%9d ms %12s seconds)";
        printer.kv("Total mixin time", format, totalMixinTime, threedp.format((double)totalMixinTime * 0.001)).add();
        printer.kv("Preparing mixins", format, prepareTime, threedp.format((double)prepareTime * 0.001));
        printer.kv("Reading input", format, readTime, threedp.format((double)readTime * 0.001));
        printer.kv("Applying mixins", format, applyTime, threedp.format((double)applyTime * 0.001));
        printer.kv("Writing output", format, writeTime, threedp.format((double)writeTime * 0.001)).add();
        printer.kv("of which", "");
        printer.kv("Time spent loading from disk", format, loadTime, threedp.format((double)loadTime * 0.001));
        printer.kv("Time spent transforming classes", format, transformTime, threedp.format((double)transformTime * 0.001)).add();
        if (worstTransformer != null) {
            printer.kv("Worst transformer", worstTransformer.getName());
            printer.kv("Class", worstTransformer.getInfo());
            printer.kv("Time spent", "%s seconds", worstTransformer.getTotalSeconds());
            printer.kv("called", "%d times", worstTransformer.getTotalCount()).add();
        }
        printer.kv("   Time allocation:     Processing mixins", "%9d ms %10s%% of total)", actualTime, onedp.format(timeSliceMixin));
        printer.kv("Loading classes", "%9d ms %10s%% of total)", loadTime, onedp.format(timeSliceLoad));
        printer.kv("Running transformers", "%9d ms %10s%% of total)", transformTime, onedp.format(timeSliceTransform));
        if (exportTime > 0L) {
            printer.kv("Exporting classes (debug)", "%9d ms %10s%% of total)", exportTime, onedp.format(timeSliceExport));
        }
        printer.add();
        try {
            Class<?> agent = this.service.getClassProvider().findAgentClass(METRONOME_AGENT_CLASS, false);
            Method mdGetTimes = agent.getDeclaredMethod("getTimes", new Class[0]);
            Map times = (Map)mdGetTimes.invoke(null, new Object[0]);
            printer.hr().add("Transformer Times").hr().add();
            int longest = 10;
            for (Map.Entry entry : times.entrySet()) {
                longest = Math.max(longest, ((String)entry.getKey()).length());
            }
            for (Map.Entry entry : times.entrySet()) {
                String name = (String)entry.getKey();
                long mixinTime = 0L;
                for (Profiler.Section section : this.profiler.getSections()) {
                    if (!name.equals(section.getInfo())) continue;
                    mixinTime = section.getTotalTime();
                    break;
                }
                if (mixinTime > 0L) {
                    printer.add("%-" + longest + "s %8s ms %8s ms in mixin)", name, (Long)entry.getValue() + mixinTime, "(" + mixinTime);
                    continue;
                }
                printer.add("%-" + longest + "s %8s ms", name, entry.getValue());
            }
            printer.add();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        printer.print();
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        return true;
    }

    @Override
    public synchronized byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        if (transformedName == null || this.errorState) {
            return basicClass;
        }
        MixinEnvironment environment = MixinEnvironment.getCurrentEnvironment();
        if (basicClass == null) {
            for (IClassGenerator generator : this.extensions.getGenerators()) {
                Profiler.Section genTimer = this.profiler.begin("generator", generator.getClass().getSimpleName().toLowerCase());
                basicClass = generator.generate(transformedName);
                genTimer.end();
                if (basicClass == null) continue;
                this.extensions.export(environment, transformedName.replace('.', '/'), false, basicClass);
                return basicClass;
            }
            return basicClass;
        }
        boolean locked = this.lock.push().check();
        Profiler.Section mixinTimer = this.profiler.begin("mixin");
        if (!locked) {
            try {
                this.checkSelect(environment);
            }
            catch (Exception ex) {
                this.lock.pop();
                mixinTimer.end();
                throw new MixinException(ex);
            }
        }
        try {
            if (this.postProcessor.canTransform(transformedName)) {
                Profiler.Section postTimer = this.profiler.begin("postprocessor");
                byte[] bytes = this.postProcessor.transformClassBytes(name, transformedName, basicClass);
                postTimer.end();
                this.extensions.export(environment, transformedName, false, bytes);
                byte[] arrby = bytes;
                return arrby;
            }
            TreeSet<MixinInfo> mixins = null;
            boolean invalidRef = false;
            for (MixinConfig config : this.configs) {
                if (config.packageMatch(transformedName)) {
                    invalidRef = true;
                    continue;
                }
                if (!config.hasMixinsFor(transformedName)) continue;
                if (mixins == null) {
                    mixins = new TreeSet<MixinInfo>();
                }
                mixins.addAll(config.getMixinsFor(transformedName));
            }
            if (invalidRef) {
                throw new NoClassDefFoundError(String.format("%s is a mixin class and cannot be referenced directly", transformedName));
            }
            if (mixins != null) {
                if (locked) {
                    logger.warn("Re-entrance detected, this will cause serious problems.", (Throwable)new MixinException());
                    throw new MixinApplyError("Re-entrance error.");
                }
                if (this.hotSwapper != null) {
                    this.hotSwapper.registerTargetClass(transformedName, basicClass);
                }
                try {
                    Profiler.Section timer = this.profiler.begin("read");
                    ClassNode targetClassNode = this.readClass(basicClass, true);
                    TargetClassContext context = new TargetClassContext(environment, this.extensions, this.sessionId, transformedName, targetClassNode, mixins);
                    timer.end();
                    basicClass = this.applyMixins(environment, context);
                    ++this.transformedCount;
                }
                catch (InvalidMixinException th) {
                    this.dumpClassOnFailure(transformedName, basicClass, environment);
                    this.handleMixinApplyError(transformedName, th, environment);
                }
            }
            Object object = basicClass;
            return object;
        }
        catch (Throwable th) {
            th.printStackTrace();
            this.dumpClassOnFailure(transformedName, basicClass, environment);
            throw new MixinTransformerError("An unexpected critical error was encountered", th);
        }
        finally {
            this.lock.pop();
            mixinTimer.end();
        }
    }

    public List<String> reload(String mixinClass, byte[] bytes) {
        if (this.lock.getDepth() > 0) {
            throw new MixinApplyError("Cannot reload mixin if re-entrant lock entered");
        }
        ArrayList<String> targets = new ArrayList<String>();
        for (MixinConfig config : this.configs) {
            targets.addAll(config.reloadMixin(mixinClass, bytes));
        }
        return targets;
    }

    private void checkSelect(MixinEnvironment environment) {
        if (this.currentEnvironment != environment) {
            this.select(environment);
            return;
        }
        int unvisitedCount = Mixins.getUnvisitedCount();
        if (unvisitedCount > 0 && this.transformedCount == 0) {
            this.select(environment);
        }
    }

    private void select(MixinEnvironment environment) {
        Level level = this.verboseLoggingLevel = environment.getOption(MixinEnvironment.Option.DEBUG_VERBOSE) ? Level.INFO : Level.DEBUG;
        if (this.transformedCount > 0) {
            logger.log(this.verboseLoggingLevel, "Ending {}, applied {} mixins", new Object[]{this.currentEnvironment, this.transformedCount});
        }
        String action = this.currentEnvironment == environment ? "Checking for additional" : "Preparing";
        logger.log(this.verboseLoggingLevel, "{} mixins for {}", new Object[]{action, environment});
        this.profiler.setActive(true);
        this.profiler.mark(environment.getPhase().toString() + ":prepare");
        Profiler.Section prepareTimer = this.profiler.begin("prepare");
        this.selectConfigs(environment);
        this.extensions.select(environment);
        int totalMixins = this.prepareConfigs(environment);
        this.currentEnvironment = environment;
        this.transformedCount = 0;
        prepareTimer.end();
        long elapsedMs = prepareTimer.getTime();
        double elapsedTime = prepareTimer.getSeconds();
        if (elapsedTime > 0.25) {
            long loadTime = this.profiler.get("class.load").getTime();
            long transformTime = this.profiler.get("class.transform").getTime();
            long pluginTime = this.profiler.get("mixin.plugin").getTime();
            String elapsed = new DecimalFormat("###0.000").format(elapsedTime);
            String perMixinTime = new DecimalFormat("###0.0").format((double)elapsedMs / (double)totalMixins);
            logger.log(this.verboseLoggingLevel, "Prepared {} mixins in {} sec ({}ms avg) ({}ms load, {}ms transform, {}ms plugin)", new Object[]{totalMixins, elapsed, perMixinTime, loadTime, transformTime, pluginTime});
        }
        this.profiler.mark(environment.getPhase().toString() + ":apply");
        this.profiler.setActive(environment.getOption(MixinEnvironment.Option.DEBUG_PROFILER));
    }

    private void selectConfigs(MixinEnvironment environment) {
        Iterator<Config> iter = Mixins.getConfigs().iterator();
        while (iter.hasNext()) {
            Config handle = iter.next();
            try {
                MixinConfig config = handle.get();
                if (!config.select(environment)) continue;
                iter.remove();
                logger.log(this.verboseLoggingLevel, "Selecting config {}", new Object[]{config});
                config.onSelect();
                this.pendingConfigs.add(config);
            }
            catch (Exception ex) {
                logger.warn(String.format("Failed to select mixin config: %s", handle), (Throwable)ex);
            }
        }
        Collections.sort(this.pendingConfigs);
    }

    private int prepareConfigs(MixinEnvironment environment) {
        String message;
        int totalMixins = 0;
        final IHotSwap hotSwapper = this.hotSwapper;
        for (MixinConfig config : this.pendingConfigs) {
            config.addListener(this.postProcessor);
            if (hotSwapper == null) continue;
            config.addListener(new MixinConfig.IListener(){

                @Override
                public void onPrepare(MixinInfo mixin) {
                    hotSwapper.registerMixinClass(mixin.getClassName());
                }

                @Override
                public void onInit(MixinInfo mixin) {
                }
            });
        }
        for (MixinConfig config : this.pendingConfigs) {
            try {
                logger.log(this.verboseLoggingLevel, "Preparing {} ({})", new Object[]{config, config.getDeclaredMixinCount()});
                config.prepare();
                totalMixins += config.getMixinCount();
            }
            catch (InvalidMixinException ex) {
                this.handleMixinPrepareError(config, ex, environment);
            }
            catch (Exception ex) {
                message = ex.getMessage();
                logger.error("Error encountered whilst initialising mixin config '" + config.getName() + "': " + message, (Throwable)ex);
            }
        }
        for (MixinConfig config : this.pendingConfigs) {
            IMixinConfigPlugin plugin = config.getPlugin();
            if (plugin == null) continue;
            HashSet<String> otherTargets = new HashSet<String>();
            for (MixinConfig otherConfig : this.pendingConfigs) {
                if (otherConfig.equals(config)) continue;
                otherTargets.addAll(otherConfig.getTargets());
            }
            plugin.acceptTargets(config.getTargets(), Collections.unmodifiableSet(otherTargets));
        }
        for (MixinConfig config : this.pendingConfigs) {
            try {
                config.postInitialise();
            }
            catch (InvalidMixinException ex) {
                this.handleMixinPrepareError(config, ex, environment);
            }
            catch (Exception ex) {
                message = ex.getMessage();
                logger.error("Error encountered during mixin config postInit step'" + config.getName() + "': " + message, (Throwable)ex);
            }
        }
        this.configs.addAll(this.pendingConfigs);
        Collections.sort(this.configs);
        this.pendingConfigs.clear();
        return totalMixins;
    }

    private byte[] applyMixins(MixinEnvironment environment, TargetClassContext context) {
        Profiler.Section timer;
        block2: {
            timer = this.profiler.begin("preapply");
            this.extensions.preApply(context);
            timer = timer.next("apply");
            this.apply(context);
            timer = timer.next("postapply");
            try {
                this.extensions.postApply(context);
            }
            catch (ExtensionCheckClass.ValidationFailedException ex) {
                logger.info(ex.getMessage());
                if (!context.isExportForced() && !environment.getOption(MixinEnvironment.Option.DEBUG_EXPORT)) break block2;
                this.writeClass(context);
            }
        }
        timer.end();
        return this.writeClass(context);
    }

    private void apply(TargetClassContext context) {
        context.applyMixins();
    }

    private void handleMixinPrepareError(MixinConfig config, InvalidMixinException ex, MixinEnvironment environment) throws MixinPrepareError {
        this.handleMixinError(config.getName(), ex, environment, ErrorPhase.PREPARE);
    }

    private void handleMixinApplyError(String targetClass, InvalidMixinException ex, MixinEnvironment environment) throws MixinApplyError {
        this.handleMixinError(targetClass, ex, environment, ErrorPhase.APPLY);
    }

    private void handleMixinError(String context, InvalidMixinException ex, MixinEnvironment environment, ErrorPhase errorPhase) throws Error {
        IMixinErrorHandler.ErrorAction action;
        this.errorState = true;
        IMixinInfo mixin = ex.getMixin();
        if (mixin == null) {
            logger.error("InvalidMixinException has no mixin!", (Throwable)ex);
            throw ex;
        }
        IMixinConfig config = mixin.getConfig();
        MixinEnvironment.Phase phase = mixin.getPhase();
        IMixinErrorHandler.ErrorAction errorAction = action = config.isRequired() ? IMixinErrorHandler.ErrorAction.ERROR : IMixinErrorHandler.ErrorAction.WARN;
        if (environment.getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
            new PrettyPrinter().add("Invalid Mixin").centre().hr('-').kvWidth(10).kv("Action", errorPhase.name()).kv("Mixin", mixin.getClassName()).kv("Config", config.getName()).kv("Phase", phase).hr('-').add("    %s", ex.getClass().getName()).hr('-').addWrapped("    %s", ex.getMessage()).hr('-').add(ex, 8).trace(action.logLevel);
        }
        for (IMixinErrorHandler handler : this.getErrorHandlers(mixin.getPhase())) {
            IMixinErrorHandler.ErrorAction newAction = errorPhase.onError(handler, context, ex, mixin, action);
            if (newAction == null) continue;
            action = newAction;
        }
        logger.log(action.logLevel, errorPhase.getLogMessage(context, ex, mixin), (Throwable)ex);
        this.errorState = false;
        if (action == IMixinErrorHandler.ErrorAction.ERROR) {
            throw new MixinApplyError(errorPhase.getErrorMessage(mixin, config, phase), ex);
        }
    }

    private List<IMixinErrorHandler> getErrorHandlers(MixinEnvironment.Phase phase) {
        ArrayList<IMixinErrorHandler> handlers = new ArrayList<IMixinErrorHandler>();
        for (String handlerClassName : Mixins.getErrorHandlerClasses()) {
            try {
                logger.info("Instancing error handler class {}", new Object[]{handlerClassName});
                Class<?> handlerClass = this.service.getClassProvider().findClass(handlerClassName, true);
                IMixinErrorHandler handler = (IMixinErrorHandler)handlerClass.newInstance();
                if (handler == null) continue;
                handlers.add(handler);
            }
            catch (Throwable throwable) {}
        }
        return handlers;
    }

    private byte[] writeClass(TargetClassContext context) {
        return this.writeClass(context.getClassName(), context.getClassNode(), context.isExportForced());
    }

    private byte[] writeClass(String transformedName, ClassNode targetClass, boolean forceExport) {
        Profiler.Section writeTimer = this.profiler.begin("write");
        byte[] bytes = this.writeClass(targetClass);
        writeTimer.end();
        this.extensions.export(this.currentEnvironment, transformedName, forceExport, bytes);
        return bytes;
    }

    private void dumpClassOnFailure(String className, byte[] bytes, MixinEnvironment env) {
        if (env.getOption(MixinEnvironment.Option.DUMP_TARGET_ON_FAILURE)) {
            ExtensionClassExporter exporter = (ExtensionClassExporter)this.extensions.getExtension(ExtensionClassExporter.class);
            exporter.dumpClass(className.replace('.', '/') + ".target", bytes);
        }
    }

    static enum ErrorPhase {
        PREPARE{

            @Override
            IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler handler, String context, InvalidMixinException ex, IMixinInfo mixin, IMixinErrorHandler.ErrorAction action) {
                try {
                    return handler.onPrepareError(mixin.getConfig(), ex, mixin, action);
                }
                catch (AbstractMethodError ame) {
                    return action;
                }
            }

            @Override
            protected String getContext(IMixinInfo mixin, String context) {
                return String.format("preparing %s in %s", mixin.getName(), context);
            }
        }
        ,
        APPLY{

            @Override
            IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler handler, String context, InvalidMixinException ex, IMixinInfo mixin, IMixinErrorHandler.ErrorAction action) {
                try {
                    return handler.onApplyError(context, ex, mixin, action);
                }
                catch (AbstractMethodError ame) {
                    return action;
                }
            }

            @Override
            protected String getContext(IMixinInfo mixin, String context) {
                return String.format("%s -> %s", mixin, context);
            }
        };

        private final String text = this.name().toLowerCase();

        abstract IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler var1, String var2, InvalidMixinException var3, IMixinInfo var4, IMixinErrorHandler.ErrorAction var5);

        protected abstract String getContext(IMixinInfo var1, String var2);

        public String getLogMessage(String context, InvalidMixinException ex, IMixinInfo mixin) {
            return String.format("Mixin %s failed %s: %s %s", this.text, this.getContext(mixin, context), ex.getClass().getName(), ex.getMessage());
        }

        public String getErrorMessage(IMixinInfo mixin, IMixinConfig config, MixinEnvironment.Phase phase) {
            return String.format("Mixin [%s] from phase [%s] in config [%s] FAILED during %s", mixin, phase, config, this.name());
        }
    }
}

