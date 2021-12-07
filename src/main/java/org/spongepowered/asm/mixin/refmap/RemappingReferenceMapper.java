package org.spongepowered.asm.mixin.refmap;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;

public final class RemappingReferenceMapper
implements IReferenceMapper {
    private static final String DEFAULT_RESOURCE_PATH_PROPERTY = "net.minecraftforge.gradle.GradleStart.srg.srg-mcp";
    private static final String DEFAULT_MAPPING_ENV = "searge";
    private static final Logger logger = LogManager.getLogger("mixin");
    private static final Map<String, Map<String, String>> srgs = new HashMap<String, Map<String, String>>();
    private final IReferenceMapper refMap;
    private final Map<String, String> mappings;
    private final Map<String, Map<String, String>> cache = new HashMap<String, Map<String, String>>();

    private RemappingReferenceMapper(MixinEnvironment env, IReferenceMapper refMap) {
        this.refMap = refMap;
        this.refMap.setContext(RemappingReferenceMapper.getMappingEnv(env));
        String resource = RemappingReferenceMapper.getResource(env);
        this.mappings = RemappingReferenceMapper.loadSrgs(resource);
        logger.info("Remapping refMap {} using {}", new Object[]{refMap.getResourceName(), resource});
    }

    @Override
    public boolean isDefault() {
        return this.refMap.isDefault();
    }

    @Override
    public String getResourceName() {
        return this.refMap.getResourceName();
    }

    @Override
    public String getStatus() {
        return this.refMap.getStatus();
    }

    @Override
    public String getContext() {
        return this.refMap.getContext();
    }

    @Override
    public void setContext(String context) {
    }

    @Override
    public String remap(String className, String reference) {
        Map<String, String> classCache = this.getCache(className);
        String remapped = classCache.get(reference);
        if (remapped == null) {
            remapped = this.refMap.remap(className, reference);
            for (Map.Entry<String, String> entry : this.mappings.entrySet()) {
                remapped = remapped.replace(entry.getKey(), entry.getValue());
            }
            classCache.put(reference, remapped);
        }
        return remapped;
    }

    private Map<String, String> getCache(String className) {
        Map<String, String> classCache = this.cache.get(className);
        if (classCache == null) {
            classCache = new HashMap<String, String>();
            this.cache.put(className, classCache);
        }
        return classCache;
    }

    @Override
    public String remapWithContext(String context, String className, String reference) {
        return this.refMap.remapWithContext(context, className, reference);
    }

    private static Map<String, String> loadSrgs(String fileName) {
        if (srgs.containsKey(fileName)) {
            return srgs.get(fileName);
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        srgs.put(fileName, map);
        File file = new File(fileName);
        if (!file.isFile()) {
            return map;
        }
        try {
            Files.readLines(file, Charsets.UTF_8, new LineProcessor<Object>(){

                @Override
                public Object getResult() {
                    return null;
                }

                @Override
                public boolean processLine(String line) throws IOException {
                    if (Strings.isNullOrEmpty(line) || line.startsWith("#")) {
                        return true;
                    }
                    int fromPos = 0;
                    int toPos = 0;
                    if ((line.startsWith("MD: ") ? 2 : (toPos = line.startsWith("FD: ") ? 1 : 0)) > 0) {
                        String[] entries = line.substring(4).split(" ", 4);
                        map.put(entries[fromPos].substring(entries[fromPos].lastIndexOf(47) + 1), entries[toPos].substring(entries[toPos].lastIndexOf(47) + 1));
                    }
                    return true;
                }
            });
        }
        catch (IOException ex) {
            logger.warn("Could not read input SRG file: {}", new Object[]{fileName});
            logger.catching(ex);
        }
        return map;
    }

    public static IReferenceMapper of(MixinEnvironment env, IReferenceMapper refMap) {
        if (!refMap.isDefault() && RemappingReferenceMapper.hasData(env)) {
            return new RemappingReferenceMapper(env, refMap);
        }
        return refMap;
    }

    private static boolean hasData(MixinEnvironment env) {
        String fileName = RemappingReferenceMapper.getResource(env);
        return fileName != null && new File(fileName).exists();
    }

    private static String getResource(MixinEnvironment env) {
        String resource = env.getOptionValue(MixinEnvironment.Option.REFMAP_REMAP_RESOURCE);
        return Strings.isNullOrEmpty(resource) ? System.getProperty(DEFAULT_RESOURCE_PATH_PROPERTY) : resource;
    }

    private static String getMappingEnv(MixinEnvironment env) {
        String resource = env.getOptionValue(MixinEnvironment.Option.REFMAP_REMAP_SOURCE_ENV);
        return Strings.isNullOrEmpty(resource) ? DEFAULT_MAPPING_ENV : resource;
    }
}
