/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.SoundEventAccessor
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.modules.render.sounds;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.mixins.audio.ISoundHandler;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.render.sounds.ListenerClientSound;
import me.earth.earthhack.impl.modules.render.sounds.ListenerCustomSound;
import me.earth.earthhack.impl.modules.render.sounds.ListenerEffect;
import me.earth.earthhack.impl.modules.render.sounds.ListenerRender;
import me.earth.earthhack.impl.modules.render.sounds.ListenerSound;
import me.earth.earthhack.impl.modules.render.sounds.ListenerSpawnMob;
import me.earth.earthhack.impl.modules.render.sounds.ListenerTick;
import me.earth.earthhack.impl.modules.render.sounds.util.CoordLogger;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPages;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.text.ITextComponent;

public class Sounds
extends RegisteringModule<Boolean, SimpleRemovingSetting> {
    protected final Setting<SoundPages> pages = this.register(new EnumSetting<SoundPages>("Page", SoundPages.ESP));
    protected final Setting<Boolean> render = this.register(new BooleanSetting("Render", true));
    protected final Setting<Boolean> custom = this.register(new BooleanSetting("Custom", true));
    protected final Setting<Boolean> packet = this.register(new BooleanSetting("Packet", true));
    protected final Setting<Boolean> client = this.register(new BooleanSetting("Client-Side", false));
    protected final ColorSetting color = this.register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Integer> remove = this.register(new NumberSetting<Integer>("Remove", 750, 0, 20000));
    protected final Setting<Boolean> fade = this.register(new BooleanSetting("Fade", false));
    protected final Setting<Boolean> rect = this.register(new BooleanSetting("Rectangle", false));
    protected final Setting<Float> scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(0.003f), Float.valueOf(0.001f), Float.valueOf(0.01f)));
    protected final Setting<Boolean> cancelled = this.register(new BooleanSetting("Cancelled", true));
    protected final Setting<CoordLogger> coordLogger = this.register(new EnumSetting<CoordLogger>("Coord-Logger", CoordLogger.Vanilla));
    protected final Setting<Boolean> chat = this.register(new BooleanSetting("Chat", false));
    protected final Setting<Boolean> thunder = this.register(new BooleanSetting("Thunder", false));
    protected final Setting<Boolean> dragon = this.register(new BooleanSetting("Dragon", false));
    protected final Setting<Boolean> wither = this.register(new BooleanSetting("Wither", false));
    protected final Setting<Boolean> portal = this.register(new BooleanSetting("Portal", false));
    protected final Setting<Boolean> slimes = this.register(new BooleanSetting("Slimes", false));
    protected final Map<SoundPosition, Long> sounds = new ConcurrentHashMap<SoundPosition, Long>();

    public Sounds() {
        super("Sounds", Category.Render, "Add_Sound", "sound", SimpleRemovingSetting::new, s -> "White/Blacklist " + s.getName() + "sounds.");
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerCustomSound(this));
        this.listeners.add(new ListenerClientSound(this));
        this.listeners.add(new ListenerEffect(this));
        this.listeners.add(new ListenerSpawnMob(this));
        new PageBuilder<SoundPages>(this, this.pages).addPage(v -> v == SoundPages.ESP, (Setting<?>)this.render, (Setting<?>)this.cancelled).addPage(v -> v == SoundPages.CoordLogger, (Setting<?>)this.coordLogger, (Setting<?>)this.slimes).register(Visibilities.VISIBILITY_MANAGER);
        this.listType.setValue(ListType.BlackList);
    }

    @Override
    public String getInput(String input, boolean add) {
        if (add) {
            String itemName = Sounds.getSoundStartingWith(input);
            if (itemName != null) {
                return TextUtil.substring(itemName, input.length());
            }
            return "";
        }
        return super.getInput(input, false);
    }

    public void log(String s) {
        if (this.chat.getValue().booleanValue()) {
            mc.addScheduledTask(() -> ChatUtil.sendMessage(s));
        } else {
            Earthhack.getLogger().info(s);
        }
    }

    public static String getSoundStartingWith(String prefix) {
        ISoundHandler handler = (ISoundHandler)mc.getSoundHandler();
        for (SoundEventAccessor soundEventAccessor : handler.getRegistry()) {
            if (TextUtil.startsWith(soundEventAccessor.getLocation().toString(), prefix)) {
                return soundEventAccessor.getLocation().toString();
            }
            ITextComponent component = soundEventAccessor.getSubtitle();
            if (component == null || !TextUtil.startsWith(component.getUnformattedComponentText(), prefix)) continue;
            return component.getUnformattedComponentText();
        }
        return null;
    }
}

