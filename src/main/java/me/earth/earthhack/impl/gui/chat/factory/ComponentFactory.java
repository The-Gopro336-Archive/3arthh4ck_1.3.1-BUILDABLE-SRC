/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.event.HoverEvent
 *  net.minecraft.util.text.event.HoverEvent$Action
 */
package me.earth.earthhack.impl.gui.chat.factory;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.BindComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.BooleanComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.ColorComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.DefaultComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.EnumComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.NumberComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.StringComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

public class ComponentFactory {
    private static final Map<Class<? extends Setting>, IComponentFactory<?, ?>> FACTORIES = new HashMap();

    public static <E, T extends Setting<E>> IComponentFactory<?, ?> register(Class<T> clazz, IComponentFactory<E, T> factory) {
        return FACTORIES.put(clazz, factory);
    }

    public static <T, S extends Setting<T>> SettingComponent<T, S> create(S setting) {
        IComponentFactory<?, ?> factory = FACTORIES.get(setting.getClass());
        if (factory == null) {
            return new DefaultComponent(setting);
        }
        return factory.create(setting);
    }

    public static HoverEvent getHoverEvent(Setting<?> setting) {
        String dataDescription;
        if (setting == null) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString("null"));
        }
        ModuleData data = null;
        if (setting.getContainer() instanceof Module) {
            data = ((Module)setting.getContainer()).getData();
        }
        String description = "A Setting. (" + setting.getInitial().getClass().getSimpleName() + ")";
        if (data != null && (dataDescription = data.settingDescriptions().get(setting)) != null) {
            description = dataDescription;
        }
        return ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString(description)));
    }

    static {
        ComponentFactory.register(ColorSetting.class, ColorComponent::new);
        ComponentFactory.register(BindSetting.class, BindComponent::new);
        ComponentFactory.register(BooleanSetting.class, BooleanComponent::new);
        ComponentFactory.register(StringSetting.class, StringComponent::new);
        FACTORIES.put(EnumSetting.class, EnumComponent.FACTORY);
        FACTORIES.put(NumberSetting.class, NumberComponent.FACTORY);
    }
}

