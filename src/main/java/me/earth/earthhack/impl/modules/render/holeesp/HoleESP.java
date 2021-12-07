/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 */
package me.earth.earthhack.impl.modules.render.holeesp;

import java.awt.Color;
import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.modules.render.holeesp.HoleESPData;
import me.earth.earthhack.impl.modules.render.holeesp.ListenerRender;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class HoleESP
extends Module
implements HoleObserver {
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Integer> holes = this.register(new NumberSetting<Integer>("Holes", 10, 0, 1000));
    protected final Setting<Integer> safeHole = this.register(new NumberSetting<Integer>("S-Holes", 10, 0, 1000));
    protected final Setting<Integer> wide = this.register(new NumberSetting<Integer>("2x1-Holes", 1, 0, 1000));
    protected final Setting<Integer> big = this.register(new NumberSetting<Integer>("2x2-Holes", 1, 0, 1000));
    protected final Setting<Boolean> fov = this.register(new BooleanSetting("Fov", true));
    protected final Setting<Boolean> own = this.register(new BooleanSetting("Own", false));
    protected final Setting<Boolean> fade = this.register(new BooleanSetting("Fade", false));
    protected final Setting<Float> fadeRange = this.register(new NumberSetting<Float>("Fade-Range", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Float> minFade = this.register(new NumberSetting<Float>("Min-Fade", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Double> alphaFactor = this.register(new NumberSetting<Double>("AlphaFactor", 0.3, 0.0, 1.0));
    protected final Setting<Float> height = this.register(new NumberSetting<Float>("SafeHeight", Float.valueOf(1.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    protected final Setting<Float> unsafeHeight = this.register(new NumberSetting<Float>("UnsafeHeight", Float.valueOf(1.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    protected final Setting<Float> wideHeight = this.register(new NumberSetting<Float>("2x1-Height", Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    protected final Setting<Float> bigHeight = this.register(new NumberSetting<Float>("2x2-Height", Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    protected final Setting<Color> unsafeColor = this.register(new ColorSetting("UnsafeColor", Color.RED));
    protected final Setting<Color> safeColor = this.register(new ColorSetting("SafeColor", Color.GREEN));
    protected final Setting<Color> wideColor = this.register(new ColorSetting("2x1-Color", new Color(90, 9, 255)));
    protected final Setting<Color> bigColor = this.register(new ColorSetting("2x2-Color", new Color(0, 80, 255)));
    private final BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

    public HoleESP() {
        super("HoleESP", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.setData(new HoleESPData(this));
    }

    @Override
    public void onLoad() {
        if (this.isEnabled()) {
            Managers.HOLES.register(this);
        }
    }

    @Override
    public void onEnable() {
        Managers.HOLES.register(this);
    }

    @Override
    public void onDisable() {
        Managers.HOLES.unregister(this);
    }

    protected void onRender3D() {
        this.renderList(Managers.HOLES.getUnsafe(), this.unsafeColor.getValue(), this.unsafeHeight.getValue().floatValue(), this.holes.getValue());
        this.renderList(Managers.HOLES.getSafe(), this.safeColor.getValue(), this.height.getValue().floatValue(), this.safeHole.getValue());
        this.renderList(Managers.HOLES.getLongHoles(), this.wideColor.getValue(), this.wideHeight.getValue().floatValue(), this.wide.getValue());
        BlockPos playerPos = new BlockPos((Entity)HoleESP.mc.player);
        if (this.big.getValue() != 0 && !Managers.HOLES.getBigHoles().isEmpty()) {
            int i = 1;
            for (BlockPos pos : Managers.HOLES.getBigHoles()) {
                if (i > this.big.getValue()) {
                    return;
                }
                if (!this.checkPos(pos, playerPos)) continue;
                Color bC = this.bigColor.getValue();
                float bH = this.bigHeight.getValue().floatValue();
                if (this.fade.getValue().booleanValue()) {
                    double distance = HoleESP.mc.player.getDistanceSq((double)(pos.getX() + 1), (double)pos.getY(), (double)(pos.getZ() + 1));
                    double alpha = ((double)(MathUtil.square(this.fadeRange.getValue().floatValue()) + MathUtil.square(this.minFade.getValue().floatValue())) - distance) / (double)MathUtil.square(this.fadeRange.getValue().floatValue());
                    if (alpha > 0.0 && alpha < 1.0) {
                        int alphaInt = MathUtil.clamp((int)(alpha * 255.0), 0, 255);
                        Color bC1 = new Color(bC.getRed(), bC.getGreen(), bC.getBlue(), alphaInt);
                        int boxInt = (int)((double)alphaInt * this.alphaFactor.getValue());
                        RenderUtil.renderBox(pos, bC1, bH, boxInt);
                        this.mPos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                        RenderUtil.renderBox((BlockPos)this.mPos, bC1, bH, boxInt);
                        this.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
                        RenderUtil.renderBox((BlockPos)this.mPos, bC1, bH, boxInt);
                        this.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                        RenderUtil.renderBox((BlockPos)this.mPos, bC1, bH, boxInt);
                    } else if (alpha < 0.0) continue;
                }
                RenderUtil.renderBox(pos, bC, bH);
                this.mPos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                RenderUtil.renderBox((BlockPos)this.mPos, bC, bH);
                this.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
                RenderUtil.renderBox((BlockPos)this.mPos, bC, bH);
                this.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                RenderUtil.renderBox((BlockPos)this.mPos, bC, bH);
                ++i;
            }
        }
    }

    private void renderList(List<BlockPos> positions, Color color, float height, int max) {
        BlockPos playerPos = new BlockPos((Entity)HoleESP.mc.player);
        if (max != 0 && !positions.isEmpty()) {
            int i = 1;
            for (BlockPos pos : positions) {
                if (i > max) {
                    return;
                }
                if (!this.checkPos(pos, playerPos)) continue;
                if (this.fade.getValue().booleanValue()) {
                    double alpha = ((double)(MathUtil.square(this.fadeRange.getValue().floatValue()) + MathUtil.square(this.minFade.getValue().floatValue())) - HoleESP.mc.player.getDistanceSq(pos)) / (double)MathUtil.square(this.fadeRange.getValue().floatValue());
                    if (alpha > 0.0 && alpha < 1.0) {
                        int alphaInt = MathUtil.clamp((int)(alpha * 255.0), 0, 255);
                        Color color1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaInt);
                        RenderUtil.renderBox(pos, color1, height, (int)((double)alphaInt * this.alphaFactor.getValue()));
                        continue;
                    }
                    if (!(alpha >= 1.0)) continue;
                    RenderUtil.renderBox(pos, color, height);
                    continue;
                }
                RenderUtil.renderBox(pos, color, height);
                ++i;
            }
        }
    }

    private boolean checkPos(BlockPos pos, BlockPos playerPos) {
        return !(this.fov.getValue() != false && !RotationUtil.inFov(pos) || this.own.getValue() == false && pos.equals((Object)playerPos));
    }

    @Override
    public double getRange() {
        return this.range.getValue().floatValue();
    }

    @Override
    public int getSafeHoles() {
        return this.safeHole.getValue();
    }

    @Override
    public int getUnsafeHoles() {
        return this.holes.getValue();
    }

    @Override
    public int get2x1Holes() {
        return this.wide.getValue();
    }

    @Override
    public int get2x2Holes() {
        return this.big.getValue();
    }
}

