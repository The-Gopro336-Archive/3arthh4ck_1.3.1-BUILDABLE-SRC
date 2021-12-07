/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.render.voidesp;

import java.awt.Color;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.voidesp.ListenerRender;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class VoidESP
extends BlockESPModule {
    protected final Setting<Integer> radius = this.register(new NumberSetting<Integer>("Radius", 20, 0, 200));
    protected final Setting<Integer> holes = this.register(new NumberSetting<Integer>("Holes", 500, 0, 1000));
    protected final Setting<Integer> update = this.register(new NumberSetting<Integer>("Update", 500, 0, 10000));
    protected final Setting<Boolean> liveHoles = this.register(new BooleanSetting("Live-Holes", false));
    protected final Setting<Integer> maxY = this.register(new NumberSetting<Integer>("Max-Y", 256, -256, 256));
    protected List<BlockPos> voidHoles = Collections.emptyList();
    protected final StopWatch timer = new StopWatch();

    public VoidESP() {
        super("VoidESP", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.color.setValue(new Color(255, 0, 0, 76));
        this.outline.setValue(new Color(255, 0, 0, 242));
        this.height.setValue(Float.valueOf(0.0f));
        SimpleData data = new SimpleData(this, "Renders void holes.");
        data.register(this.radius, "The radius in which holes will get rendered.");
        data.register(this.holes, "The maximum amount of holes to render.");
        data.register(this.color, "The color void holes will be rendered in.");
        data.register(this.update, "Time in milliseconds until Holes get updated again.");
        data.register(this.height, "If the Hole should be rendered flat or not.");
        data.register(this.liveHoles, "Saves some CPU but shouldn't matter at all.");
        data.register(this.maxY, "If your Y-Level is higher than this Holes won't be rendered.");
        this.setData(data);
    }

    protected void updateHoles() {
        if (this.timer.passed(this.update.getValue().intValue())) {
            Managers.THREAD.submit(() -> {
                BlockPos playerPos = PositionUtil.getPosition();
                int r = this.radius.getValue();
                AbstractList voidHolesIn = this.voidHoles.size() != 0 ? new ArrayList(this.voidHoles.size()) : new LinkedList();
                int cx = playerPos.getX();
                int cz = playerPos.getZ();
                int holeAmount = 0;
                block0: for (int x = cx - r; x <= cx + r; ++x) {
                    for (int z = cz - r; z <= cz + r; ++z) {
                        BlockPos pos2;
                        double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                        if (!(dist < (double)(r * r)) || VoidESP.mc.world.getBlockState(pos2 = new BlockPos(x, 0, z)).getBlock() == Blocks.BEDROCK) continue;
                        voidHolesIn.add(pos2);
                        if (this.liveHoles.getValue().booleanValue() && ++holeAmount >= this.holes.getValue()) continue block0;
                    }
                }
                voidHolesIn.sort(Comparator.comparingDouble(pos -> VoidESP.mc.player.getDistanceSq(pos)));
                mc.addScheduledTask(() -> {
                    this.voidHoles = voidHolesIn;
                    return this.voidHoles;
                });
            });
            this.timer.reset();
        }
    }
}

