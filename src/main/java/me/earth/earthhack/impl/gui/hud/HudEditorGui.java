package me.earth.earthhack.impl.gui.hud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.hud.ElementSnapPoint;
import me.earth.earthhack.impl.gui.hud.HudPanel;
import me.earth.earthhack.impl.gui.hud.Orientation;
import me.earth.earthhack.impl.gui.hud.SnapPoint;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class HudEditorGui
extends GuiScreen {
    private static HudEditorGui INSTANCE;
    private static final Minecraft mc;
    private HudPanel panel;
    private Set<SnapPoint> snapPoints;
    private Map<HudElement, SnapPoint> moduleSnapPoints;

    public HudEditorGui() {
        INSTANCE = this;
        this.panel = new HudPanel();
        this.snapPoints = new HashSet<SnapPoint>();
        this.moduleSnapPoints = new HashMap<HudElement, SnapPoint>();
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (!element.isEnabled()) continue;
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.TOP));
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.BOTTOM));
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.LEFT));
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.RIGHT));
        }
        this.snapPoints.add(new SnapPoint(2.0f, -2.0f, HudEditorGui.mc.displayHeight + 4, Orientation.LEFT));
        this.snapPoints.add(new SnapPoint(this.width - 2, -2.0f, HudEditorGui.mc.displayHeight + 4, Orientation.RIGHT));
        this.snapPoints.add(new SnapPoint(-2.0f, 2.0f, HudEditorGui.mc.displayWidth + 4, Orientation.TOP));
        this.snapPoints.add(new SnapPoint(-2.0f, this.height - 2, HudEditorGui.mc.displayWidth + 4, Orientation.BOTTOM));
    }

    public static HudEditorGui getInstance() {
        return INSTANCE;
    }

    public void onToggle() {
        this.snapPoints.clear();
        this.moduleSnapPoints.clear();
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (!element.isEnabled()) continue;
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.TOP));
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.BOTTOM));
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.LEFT));
            this.moduleSnapPoints.put(element, new ElementSnapPoint(element, Orientation.RIGHT));
        }
        this.snapPoints.add(new SnapPoint(2.0f, -2.0f, this.height + 4, Orientation.LEFT));
        this.snapPoints.add(new SnapPoint(this.width - 2, -2.0f, this.height + 4, Orientation.RIGHT));
        this.snapPoints.add(new SnapPoint(-2.0f, 2.0f, this.width + 4, Orientation.TOP));
        this.snapPoints.add(new SnapPoint(-2.0f, this.height - 2, this.width + 4, Orientation.BOTTOM));
    }

    public HudPanel getPanel() {
        return this.panel;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        ArrayList<HudElement> clicked = new ArrayList<HudElement>();
        for (HudElement element2 : Managers.ELEMENTS.getRegistered()) {
            if (!element2.isEnabled() || !GuiUtil.isHovered(element2, mouseX, mouseY)) continue;
            clicked.add(element2);
        }
        clicked.sort(Comparator.comparing(element -> Float.valueOf(element.getZ())));
        if (!clicked.isEmpty()) {
            ((HudElement)clicked.get(0)).guiMouseClicked(mouseX, mouseY, mouseButton);
        }
        this.panel.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (!element.isEnabled()) continue;
            element.guiMouseReleased(mouseX, mouseY, state);
        }
        this.panel.mouseReleased(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (!element.isEnabled()) continue;
            element.guiKeyPressed(typedChar, keyCode);
        }
        this.panel.keyPressed(typedChar, keyCode);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (SnapPoint point : this.snapPoints) {
            point.update(mouseX, mouseY, partialTicks);
            point.draw(mouseX, mouseY, partialTicks);
            Earthhack.getLogger().info(point.orientation.name() + " x: " + point.getX() + " y: " + point.getY() + " length: " + point.getLength());
        }
        for (SnapPoint point : this.moduleSnapPoints.values()) {
            point.update(mouseX, mouseY, partialTicks);
        }
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (!element.isEnabled()) continue;
            element.guiUpdate(mouseX, mouseY, partialTicks);
            element.guiDraw(mouseX, mouseY, partialTicks);
        }
        this.panel.draw(mouseX, mouseY, partialTicks);
    }

    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public Set<SnapPoint> getSnapPoints() {
        return this.snapPoints;
    }

    static {
        mc = Minecraft.getMinecraft();
    }
}
