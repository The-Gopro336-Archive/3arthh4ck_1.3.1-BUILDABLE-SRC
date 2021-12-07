package me.earth.earthhack.impl.modules.client.customfont;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.customfont.FontData;
import me.earth.earthhack.impl.modules.client.customfont.mode.FontStyle;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

public class FontMod
extends Module {
    protected final Setting<String> fontName = this.register(new StringSetting("Font", "Verdana"));
    protected final Setting<FontStyle> fontStyle = this.register(new EnumSetting<FontStyle>("FontStyle", FontStyle.Plain));
    protected final Setting<Integer> fontSize = this.register(new NumberSetting<Integer>("FontSize", 18, 15, 25));
    protected final Setting<Boolean> antiAlias = this.register(new BooleanSetting("AntiAlias", true));
    protected final Setting<Boolean> metrics = this.register(new BooleanSetting("Metrics", true));
    protected final Setting<Boolean> shadow = this.register(new BooleanSetting("Shadow", true));
    protected final Setting<Boolean> showFonts = this.register(new BooleanSetting("Fonts", false));
    protected final List<String> fonts = new ArrayList<String>();

    public FontMod() {
        super("CustomFont", Category.Client);
        Collections.addAll(this.fonts, GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        this.registerObservers();
        this.setData(new FontData(this));
    }

    private void registerObservers() {
        for (Setting<?> setting : this.getSettings()) {
            if (setting.equals(this.showFonts)) {
                setting.addObserver(event -> {
                    event.setCancelled(true);
                    this.sendFonts();
                });
                continue;
            }
            setting.addObserver(e -> Scheduler.getInstance().schedule(this::setFont));
        }
    }

    public void sendFonts() {
        SimpleComponent component = new SimpleComponent("Available Fonts: ");
        component.setWrap(true);
        for (int i = 0; i < this.fonts.size(); ++i) {
            final String font = this.fonts.get(i);
            if (font == null) continue;
            int finalI = i;
            component.appendSibling(new SuppliedComponent(() -> (font.equals(this.fontName.getValue()) ? "\u00a7a" : "\u00a7c") + font + (finalI == this.fonts.size() - 1 ? "" : ", ")).setStyle(new Style().setClickEvent(new SmartClickEvent(ClickEvent.Action.RUN_COMMAND){

                @Override
                public String getValue() {
                    return Commands.getPrefix() + "CustomFont Font \"" + font + "\"";
                }
            })));
        }
        Managers.CHAT.sendDeleteComponent(component, "Fonts", 2000);
    }

    private void setFont() {
        Managers.TEXT.setFontRenderer(new Font(this.fontName.getValue(), this.fontStyle.getValue().getFontStyle(), this.fontSize.getValue()), this.antiAlias.getValue(), this.metrics.getValue());
    }
}
