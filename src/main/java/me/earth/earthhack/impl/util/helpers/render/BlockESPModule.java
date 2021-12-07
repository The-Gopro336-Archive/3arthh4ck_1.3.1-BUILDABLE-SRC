/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.util.helpers.render;

import java.awt.Color;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.render.BlockESPBuilder;
import me.earth.earthhack.impl.util.helpers.render.ColorModule;
import me.earth.earthhack.impl.util.helpers.render.IAxisESP;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BlockESPModule
extends ColorModule {
    public final ColorSetting outline = this.register(new ColorSetting("Outline", new Color(255, 255, 255, 240)));
    public final Setting<Float> lineWidth = this.register(new NumberSetting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public final Setting<Float> height = this.register(new NumberSetting<Float>("ESP-Height", Float.valueOf(1.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    protected IAxisESP esp = new BlockESPBuilder().withColor(this.color).withOutlineColor(this.outline).withLineWidth(this.lineWidth).build();

    public BlockESPModule(String name, Category category) {
        super(name, category);
        this.color.setValue(new Color(255, 255, 255, 76));
    }

    public void renderPos(BlockPos pos) {
        this.esp.render(Interpolation.interpolatePos(pos, this.height.getValue().floatValue()));
    }

    public void renderAxis(AxisAlignedBB bb) {
        this.esp.render(Interpolation.interpolateAxis(bb));
    }

    public void renderInterpAxis(AxisAlignedBB bb) {
        this.esp.render(bb);
    }
}

