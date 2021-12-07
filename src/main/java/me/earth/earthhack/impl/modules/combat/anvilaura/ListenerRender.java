/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.AxisAlignedBB
 */
package me.earth.earthhack.impl.modules.combat.anvilaura;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerRender
extends ModuleListener<AnvilAura, Render3DEvent> {
    public ListenerRender(AnvilAura module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        long time;
        double factor;
        AxisAlignedBB mineBB = ((AnvilAura)this.module).mineBB;
        if (((AnvilAura)this.module).mineESP.getValue().booleanValue() && mineBB != null && (factor = MathUtil.clamp(1.0 - (double)(time = ((AnvilAura)this.module).mineTimer.getTime()) / 1000.0, 0.0, 1.0)) != 0.0) {
            Color b = (Color)((AnvilAura)this.module).box.getValue();
            Color c = (Color)((AnvilAura)this.module).outline.getValue();
            b = new Color(b.getRed(), b.getGreen(), b.getBlue(), (int)((double)b.getAlpha() * factor));
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)((double)c.getAlpha() * factor));
            AxisAlignedBB ib = Interpolation.interpolateAxis(mineBB);
            RenderUtil.renderBox(ib, b, c, ((AnvilAura)this.module).lineWidth.getValue().floatValue());
        }
        if (((AnvilAura)this.module).mode.getValue() == AnvilMode.Render && (!((AnvilAura)this.module).holdingAnvil.getValue().booleanValue() || InventoryUtil.isHolding(Blocks.ANVIL))) {
            for (AxisAlignedBB bb : ((AnvilAura)this.module).renderBBs) {
                AxisAlignedBB ib = Interpolation.interpolateAxis(bb);
                RenderUtil.renderBox(ib, (Color)((AnvilAura)this.module).box.getValue(), (Color)((AnvilAura)this.module).outline.getValue(), ((AnvilAura)this.module).lineWidth.getValue().floatValue());
            }
        }
    }
}

