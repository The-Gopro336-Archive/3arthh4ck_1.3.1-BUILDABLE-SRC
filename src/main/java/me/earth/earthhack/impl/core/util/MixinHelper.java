package me.earth.earthhack.impl.core.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.util.ReplacePlugin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;

public class MixinHelper {
    private static final MixinHelper INSTANCE = new MixinHelper();
    private static final IMixinConfigPlugin PLUGIN = new ReplacePlugin();
    private final Set<String> exclusions = new HashSet<String>();

    private MixinHelper() {
    }

    public static MixinHelper getHelper() {
        return INSTANCE;
    }

    public void addConfigExclusion(String exclusion) {
        this.exclusions.add(exclusion);
    }

    public void establishDominance() throws NoSuchFieldException, IllegalAccessException {
        MixinEnvironment env = MixinEnvironment.getCurrentEnvironment();
        Object trns = env.getActiveTransformer();
        if (trns instanceof MixinTransformer) {
            Field configsField = trns.getClass().getDeclaredField("configs");
            configsField.setAccessible(true);
            List configs = (List)configsField.get(trns);
            for (Object config : configs) {
                if (config == null) continue;
                Field nameField = config.getClass().getDeclaredField("name");
                nameField.setAccessible(true);
                String name = (String)nameField.get(config);
                if (this.exclusions.contains(name)) continue;
                this.setPriorityAndPlugin(config);
                Field mixinMappingField = config.getClass().getDeclaredField("mixinMapping");
                mixinMappingField.setAccessible(true);
                Map mixinMapping = (Map)mixinMappingField.get(config);
                for (List mixins : mixinMapping.values()) {
                    for (Object mixin : mixins) {
                        if (mixin == null) continue;
                        this.setPriorityAndPlugin(mixin);
                    }
                }
            }
        }
    }

    private void setPriorityAndPlugin(Object object) throws IllegalAccessException, NoSuchFieldException {
        Field pluginField = object.getClass().getDeclaredField("plugin");
        pluginField.setAccessible(true);
        Object plugin = pluginField.get(object);
        if (plugin != null && plugin != PLUGIN) {
            Core.LOGGER.info("Replacing Plugin : ");
            pluginField.set(object, PLUGIN);
        }
        Field priority = object.getClass().getDeclaredField("priority");
        priority.setAccessible(true);
        int prio = (Integer)priority.get(object);
        if (prio > 0x7FFFFFFC) {
            priority.set(object, prio - 4);
        }
    }
}
