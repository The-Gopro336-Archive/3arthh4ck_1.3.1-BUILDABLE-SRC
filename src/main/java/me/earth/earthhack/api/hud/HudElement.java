package me.earth.earthhack.api.hud;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.hud.HudEditorGui;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.util.math.MathHelper;

public abstract class HudElement
extends Module {
    private final Setting<Float> x = this.register(new NumberSetting<Float>("X", Float.valueOf(2.0f), Float.valueOf(-2000.0f), Float.valueOf(2000.0f)));
    private final Setting<Float> y = this.register(new NumberSetting<Float>("Y", Float.valueOf(2.0f), Float.valueOf(-2000.0f), Float.valueOf(2000.0f)));
    private final Setting<Float> scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    private float z = 0.0f;
    private float width = 100.0f;
    private float height = 100.0f;
    private final boolean scalable;
    private boolean scaling = false;
    private GuiUtil.Edge currentEdge;
    private boolean dragging;
    private float draggingX;
    private float draggingY;
    private float originalWidth;
    private float originalHeight;
    private float stretchingWidth;
    private float stretchingHeight;
    private float stretchingX;
    private float stretchingY;
    private float stretchingX2;
    private float stretchingY2;
    private boolean shouldScale;

    public HudElement(String name, boolean scalable) {
        super(name, Category.Client);
        this.scalable = scalable;
        this.stretchingWidth = this.width;
        this.stretchingHeight = this.height;
        this.originalWidth = this.width;
        this.originalHeight = this.height;
    }

    public HudElement(String name, boolean scalable, float x, float y, float width, float height) {
        super(name, Category.Client);
        this.scalable = scalable;
        this.x.setValue(Float.valueOf(x));
        this.y.setValue(Float.valueOf(y));
        this.width = width;
        this.height = height;
        this.stretchingWidth = width;
        this.stretchingHeight = height;
        this.originalWidth = width;
        this.originalHeight = height;
    }

    @Override
    public void onEnable() {
        if (HudEditorGui.getInstance() != null) {
            HudEditorGui.getInstance().onToggle();
        }
    }

    @Override
    public void onDisable() {
        if (HudEditorGui.getInstance() != null) {
            HudEditorGui.getInstance().onToggle();
        }
    }

    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        if (this.dragging) {
            this.setX((float)mouseX - this.draggingX);
            this.setY((float)mouseY - this.draggingY);
        }
        if (this.scalable) {
            if (this.scaling && this.currentEdge != null) {
                if (mouseX < mouseY && (float)mouseX < this.stretchingX || mouseY < mouseX && (float)mouseY < this.stretchingY) {
                    this.scale.setValue(Float.valueOf(Math.min(this.getHeight(), this.stretchingHeight + ((float)mouseY - this.stretchingY)) * Math.min(this.getWidth(), this.stretchingWidth + ((float)mouseX - this.stretchingX)) / (this.getHeight() * this.getWidth())));
                } else if (mouseX > mouseY && (float)mouseX > this.stretchingX || mouseY > mouseX && (float)mouseY > this.stretchingY) {
                    this.scale.setValue(Float.valueOf(Math.max(this.getHeight(), this.stretchingHeight + ((float)mouseY - this.stretchingY)) * Math.max(this.getWidth(), this.stretchingWidth + ((float)mouseX - this.stretchingX)) / (this.getHeight() * this.getWidth())));
                }
                this.scale.setValue(Float.valueOf(MathHelper.clamp((float)this.scale.getValue().floatValue(), (float)0.1f, (float)10.0f)));
                this.setWidth(this.getWidth() * this.scale.getValue().floatValue());
                this.setHeight(this.getHeight() * this.scale.getValue().floatValue());
            }
            if (this.shouldScale) {
                this.shouldScale = false;
                this.scale.setValue(Float.valueOf(MathHelper.clamp((float)this.scale.getValue().floatValue(), (float)0.1f, (float)10.0f)));
                this.setWidth(this.originalWidth * this.scale.getValue().floatValue());
                this.setHeight(this.originalHeight * this.scale.getValue().floatValue());
            }
        }
    }

    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        Render2DUtil.drawRect(this.x.getValue().floatValue(), this.y.getValue().floatValue(), this.x.getValue().floatValue() + this.width, this.y.getValue().floatValue() + this.height, new Color(40, 40, 40, 255).getRGB());
    }

    public void guiKeyPressed(char eventChar, int key) {
    }

    public void guiMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isHovered(this, mouseX, mouseY)) {
            this.dragging = true;
        }
        this.currentEdge = GuiUtil.getHoveredEdge(this, mouseX, mouseY, 5);
        if (GuiUtil.isHovered(this, mouseX, mouseY)) {
            if (this.currentEdge != null && this.scalable) {
                this.scaling = true;
                this.dragging = false;
                this.stretchingWidth = this.getWidth();
                this.stretchingHeight = this.getHeight();
                this.stretchingX = mouseX;
                this.stretchingY = mouseY;
                this.stretchingX2 = this.getX() + this.getWidth();
                this.stretchingY2 = this.getY() + this.getHeight();
            } else {
                this.dragging = true;
                this.scaling = false;
                this.draggingX = (float)mouseX - this.getX();
                this.draggingY = (float)mouseY - this.getY();
            }
        }
    }

    public void guiMouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
        this.scaling = false;
    }

    public void hudUpdate(float partialTicks) {
        if (this.shouldScale) {
            this.shouldScale = false;
            this.scale.setValue(Float.valueOf(MathHelper.clamp((float)this.scale.getValue().floatValue(), (float)0.1f, (float)10.0f)));
            this.setWidth(this.originalWidth * this.scale.getValue().floatValue());
            this.setHeight(this.originalHeight * this.scale.getValue().floatValue());
        }
    }

    public abstract void hudDraw(float var1);

    public float getX() {
        return this.x.getValue().floatValue();
    }

    public void setX(float x) {
        this.x.setValue(Float.valueOf(x));
    }

    public float getY() {
        return this.y.getValue().floatValue();
    }

    public void setY(float y) {
        this.y.setValue(Float.valueOf(y));
    }

    public float getZ() {
        return this.z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getScale() {
        return this.scale.getValue().floatValue();
    }

    public void setScale(float scale) {
        this.scale.setValue(Float.valueOf(scale));
    }

    public boolean isScalable() {
        return this.scalable;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDraggingX() {
        return this.draggingX;
    }

    public void setDraggingX(float draggingX) {
        this.draggingX = draggingX;
    }

    public float getDraggingY() {
        return this.draggingY;
    }

    public void setDraggingY(float draggingY) {
        this.draggingY = draggingY;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}
