/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package me.earth.earthhack.impl.gui.click.component.impl;

import java.util.ArrayList;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.BooleanComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.EnumComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.NumberComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class ModuleComponent
extends Component {
    private static final SettingCache<Boolean, BooleanSetting, ClickGui> WHITE = Caches.getSetting(ClickGui.class, BooleanSetting.class, "White-Settings", true);
    private final Module module;
    private final ArrayList<Component> components = new ArrayList();

    public ModuleComponent(Module module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(module.getName(), posX, posY, offsetX, offsetY, width, height);
        this.module = module;
    }

    @Override
    public void init() {
        this.getComponents().clear();
        float offY = this.getHeight();
        ModuleData data = this.getModule().getData();
        if (data != null) {
            this.setDescription(data.getDescription());
        }
        if (!this.getModule().getSettings().isEmpty()) {
            for (Setting<?> setting : this.getModule().getSettings()) {
                float before = offY;
                if (setting instanceof BooleanSetting && !setting.getName().equalsIgnoreCase("enabled")) {
                    this.getComponents().add(new BooleanComponent((BooleanSetting)setting, this.getFinishedX(), this.getFinishedY(), 0.0f, offY, this.getWidth(), 14.0f));
                    offY += 14.0f;
                }
                if (setting instanceof BindSetting) {
                    this.getComponents().add(new KeybindComponent((BindSetting)setting, this.getFinishedX(), this.getFinishedY(), 0.0f, offY, this.getWidth(), 14.0f));
                    offY += 14.0f;
                }
                if (setting instanceof NumberSetting) {
                    this.getComponents().add(new NumberComponent((NumberSetting)setting, this.getFinishedX(), this.getFinishedY(), 0.0f, offY, this.getWidth(), 14.0f));
                    offY += 14.0f;
                }
                if (setting instanceof EnumSetting) {
                    this.getComponents().add(new EnumComponent((EnumSetting)setting, this.getFinishedX(), this.getFinishedY(), 0.0f, offY, this.getWidth(), 14.0f));
                    offY += 14.0f;
                }
                if (setting instanceof ColorSetting) {
                    this.getComponents().add(new ColorComponent((ColorSetting)setting, this.getFinishedX(), this.getFinishedY(), 0.0f, offY, this.getWidth(), 14.0f));
                    offY += 14.0f;
                }
                if (setting instanceof StringSetting) {
                    this.getComponents().add(new StringComponent((StringSetting)setting, this.getFinishedX(), this.getFinishedY(), 0.0f, offY, this.getWidth(), 14.0f));
                    offY += 14.0f;
                }
                if (data == null || before == offY) continue;
                String desc = data.settingDescriptions().get(setting);
                if (desc == null) {
                    desc = "A Setting (" + setting.getInitial().getClass().getSimpleName() + ").";
                }
                this.getComponents().get(this.getComponents().size() - 1).setDescription(desc);
            }
        }
        this.getComponents().forEach(Component::init);
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
        this.getComponents().forEach(component -> component.moved(this.getFinishedX(), this.getFinishedY()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX(), this.getFinishedY(), this.getWidth(), this.getHeight());
        if (hovered) {
            Render2DUtil.drawRect(this.getFinishedX() + 1.0f, this.getFinishedY() + 0.5f, this.getFinishedX() + this.getWidth() - 1.0f, this.getFinishedY() + this.getHeight() - 0.5f, 0x66333333);
        }
        if (this.getModule().isEnabled()) {
            Render2DUtil.drawRect(this.getFinishedX() + 1.0f, this.getFinishedY() + 0.5f, this.getFinishedX() + this.getWidth() - 1.0f, this.getFinishedY() + this.getHeight() - 0.5f, hovered ? ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().getRGB());
        }
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.getLabel(), this.getFinishedX() + 4.0f, this.getFinishedY() + this.getHeight() / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), this.getModule().isEnabled() ? -1 : -5592406);
        if (!this.getComponents().isEmpty()) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.isExtended() ? ((ClickGui)ModuleComponent.getClickGui().get()).close.getValue() : ((ClickGui)ModuleComponent.getClickGui().get()).open.getValue(), this.getFinishedX() + this.getWidth() - 4.0f - (float)Minecraft.getMinecraft().fontRenderer.getStringWidth(this.isExtended() ? ((ClickGui)ModuleComponent.getClickGui().get()).close.getValue() : ((ClickGui)ModuleComponent.getClickGui().get()).open.getValue()), this.getFinishedY() + this.getHeight() / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), this.getModule().isEnabled() ? -1 : -5592406);
        }
        if (this.isExtended()) {
            for (Component component : this.getComponents()) {
                if (component instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component).getBooleanSetting())) {
                    component.drawScreen(mouseX, mouseY, partialTicks);
                }
                if (component instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component).getBindSetting())) {
                    component.drawScreen(mouseX, mouseY, partialTicks);
                }
                if (component instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component).getNumberSetting())) {
                    component.drawScreen(mouseX, mouseY, partialTicks);
                }
                if (component instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component).getEnumSetting())) {
                    component.drawScreen(mouseX, mouseY, partialTicks);
                }
                if (component instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component).getColorSetting())) {
                    component.drawScreen(mouseX, mouseY, partialTicks);
                }
                if (!(component instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component).getStringSetting())) continue;
                component.drawScreen(mouseX, mouseY, partialTicks);
            }
            if (this.getModule().isEnabled()) {
                Render2DUtil.drawRect(this.getFinishedX() + 1.0f, this.getFinishedY() + this.getHeight() - 0.5f, this.getFinishedX() + 3.0f, this.getFinishedY() + this.getHeight() + this.getComponentsSize(), hovered ? ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().getRGB());
                Render2DUtil.drawRect(this.getFinishedX() + 1.0f, this.getFinishedY() + this.getHeight() + this.getComponentsSize(), this.getFinishedX() + this.getWidth() - 1.0f, this.getFinishedY() + this.getHeight() + this.getComponentsSize() + 2.0f, hovered ? ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().getRGB());
                Render2DUtil.drawRect(this.getFinishedX() + this.getWidth() - 3.0f, this.getFinishedY() + this.getHeight() - 0.5f, this.getFinishedX() + this.getWidth() - 1.0f, this.getFinishedY() + this.getHeight() + this.getComponentsSize(), hovered ? ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().brighter().getRGB() : ((ClickGui)ModuleComponent.getClickGui().get()).color.getValue().getRGB());
            }
            Render2DUtil.drawBorderedRect(this.getFinishedX() + 3.0f, this.getFinishedY() + this.getHeight() - 0.5f, this.getFinishedX() + this.getWidth() - 3.0f, this.getFinishedY() + this.getHeight() + this.getComponentsSize() + 0.5f, 0.5f, 0, WHITE.getValue() != false ? -1 : -16777216);
        }
        Render2DUtil.drawBorderedRect(this.getFinishedX() + 1.0f, this.getFinishedY() + 0.5f, this.getFinishedX() + 1.0f + this.getWidth() - 2.0f, this.getFinishedY() - 0.5f + this.getHeight() + (this.isExtended() ? this.getComponentsSize() + 3.0f : 0.0f), 0.5f, 0, -16777216);
        this.updatePositions();
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (this.isExtended()) {
            for (Component component : this.getComponents()) {
                if (component instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component).getBooleanSetting())) {
                    component.keyTyped(character, keyCode);
                }
                if (component instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component).getBindSetting())) {
                    component.keyTyped(character, keyCode);
                }
                if (component instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component).getNumberSetting())) {
                    component.keyTyped(character, keyCode);
                }
                if (component instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component).getEnumSetting())) {
                    component.keyTyped(character, keyCode);
                }
                if (component instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component).getColorSetting())) {
                    component.keyTyped(character, keyCode);
                }
                if (!(component instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component).getStringSetting())) continue;
                component.keyTyped(character, keyCode);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getFinishedX(), this.getFinishedY(), this.getWidth(), this.getHeight());
        if (hovered) {
            switch (mouseButton) {
                case 0: {
                    this.getModule().toggle();
                    break;
                }
                case 1: {
                    if (this.getComponents().isEmpty()) break;
                    this.setExtended(!this.isExtended());
                    break;
                }
            }
        }
        if (this.isExtended()) {
            for (Component component : this.getComponents()) {
                if (component instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component).getBooleanSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (component instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component).getBindSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (component instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component).getNumberSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (component instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component).getEnumSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (component instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component).getColorSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (!(component instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component).getStringSetting())) continue;
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (this.isExtended()) {
            for (Component component : this.getComponents()) {
                if (component instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component).getBooleanSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
                if (component instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component).getBindSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
                if (component instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component).getNumberSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
                if (component instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component).getEnumSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
                if (component instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component).getColorSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
                if (!(component instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component).getStringSetting())) continue;
                component.mouseReleased(mouseX, mouseY, mouseButton);
            }
        }
    }

    private float getComponentsSize() {
        float size = 0.0f;
        for (Component component : this.getComponents()) {
            if (component instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component).getBooleanSetting())) {
                size += component.getHeight();
            }
            if (component instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component).getBindSetting())) {
                size += component.getHeight();
            }
            if (component instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component).getNumberSetting())) {
                size += component.getHeight();
            }
            if (component instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component).getEnumSetting())) {
                size += component.getHeight();
            }
            if (component instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component).getColorSetting())) {
                size += component.getHeight();
            }
            if (!(component instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component).getStringSetting())) continue;
            size += component.getHeight();
        }
        return size;
    }

    private void updatePositions() {
        float offsetY = this.getHeight();
        for (Component component : this.getComponents()) {
            if (component instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component).getBooleanSetting())) {
                component.setOffsetY(offsetY);
                component.moved(this.getPosX(), this.getPosY());
                offsetY += component.getHeight();
            }
            if (component instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component).getBindSetting())) {
                component.setOffsetY(offsetY);
                component.moved(this.getPosX(), this.getPosY());
                offsetY += component.getHeight();
            }
            if (component instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component).getNumberSetting())) {
                component.setOffsetY(offsetY);
                component.moved(this.getPosX(), this.getPosY());
                offsetY += component.getHeight();
            }
            if (component instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component).getEnumSetting())) {
                component.setOffsetY(offsetY);
                component.moved(this.getPosX(), this.getPosY());
                offsetY += component.getHeight();
            }
            if (component instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component).getColorSetting())) {
                component.setOffsetY(offsetY);
                component.moved(this.getPosX(), this.getPosY());
                offsetY += component.getHeight();
            }
            if (!(component instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component).getStringSetting())) continue;
            component.setOffsetY(offsetY);
            component.moved(this.getPosX(), this.getPosY());
            offsetY += component.getHeight();
        }
    }

    public Module getModule() {
        return this.module;
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }
}

