package me.earth.earthhack.impl.modules.player.speedmine.mode;

import java.awt.Color;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;

public enum ESPMode {
    None{

        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage) {
        }
    }
    ,
    Outline{

        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage) {
            RenderUtil.startRender();
            float red = 255.0f - 255.0f * damage;
            float green = 255.0f * damage;
            RenderUtil.drawOutline(bb, 1.5f, new Color((int)red, (int)green, 0, module.getOutlineAlpha()));
            RenderUtil.endRender();
        }
    }
    ,
    Block{

        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage) {
            RenderUtil.startRender();
            float red = 255.0f - 255.0f * damage;
            float green = 255.0f * damage;
            RenderUtil.drawBox(bb, new Color((int)red, (int)green, 0, module.getBlockAlpha()));
            RenderUtil.endRender();
        }
    }
    ,
    Box{

        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage) {
            Outline.drawEsp(module, bb, damage);
            Block.drawEsp(module, bb, damage);
        }
    };


    public abstract void drawEsp(Speedmine var1, AxisAlignedBB var2, float var3);
}
