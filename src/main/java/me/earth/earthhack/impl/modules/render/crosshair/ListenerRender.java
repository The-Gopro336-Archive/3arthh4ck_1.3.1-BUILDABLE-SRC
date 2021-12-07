package me.earth.earthhack.impl.modules.render.crosshair;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.crosshair.Crosshair;
import me.earth.earthhack.impl.modules.render.crosshair.mode.GapMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;

final class ListenerRender
extends ModuleListener<Crosshair, Render2DEvent> {
    public ListenerRender(Crosshair module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        int screenMiddleX = event.getResolution().getScaledWidth() / 2;
        int screenMiddleY = event.getResolution().getScaledHeight() / 2;
        float width = ((Crosshair)this.module).width.getValue().floatValue();
        if (((Crosshair)this.module).gapMode.getValue() != GapMode.NONE) {
            Render2DUtil.drawBorderedRect((float)screenMiddleX - width, (float)screenMiddleY - (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleX + ((Crosshair)this.module).width.getValue().floatValue(), (float)screenMiddleY - ((Crosshair)this.module).gapSize.getValue().floatValue() - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), 0.5f, ((Crosshair)this.module).color.getValue().getRGB(), ((Crosshair)this.module).outlineColor.getValue().getRGB());
            Render2DUtil.drawBorderedRect((float)screenMiddleX - width, (float)screenMiddleY + ((Crosshair)this.module).gapSize.getValue().floatValue() + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleX + ((Crosshair)this.module).width.getValue().floatValue(), (float)screenMiddleY + (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), 0.5f, ((Crosshair)this.module).color.getValue().getRGB(), ((Crosshair)this.module).outlineColor.getValue().getRGB());
            Render2DUtil.drawBorderedRect((float)screenMiddleX - (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleY - ((Crosshair)this.module).width.getValue().floatValue(), (float)screenMiddleX - ((Crosshair)this.module).gapSize.getValue().floatValue() - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleY + ((Crosshair)this.module).width.getValue().floatValue(), 0.5f, ((Crosshair)this.module).color.getValue().getRGB(), ((Crosshair)this.module).outlineColor.getValue().getRGB());
            Render2DUtil.drawBorderedRect((float)screenMiddleX + ((Crosshair)this.module).gapSize.getValue().floatValue() + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleY - ((Crosshair)this.module).width.getValue().floatValue(), (float)screenMiddleX + (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleY + ((Crosshair)this.module).width.getValue().floatValue(), 0.5f, ((Crosshair)this.module).color.getValue().getRGB(), ((Crosshair)this.module).outlineColor.getValue().getRGB());
        }
        if (((Crosshair)this.module).indicator.getValue().booleanValue()) {
            float f = ListenerRender.mc.player.getCooledAttackStrength(0.0f);
            float indWidthInc = ((float)screenMiddleX + (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f) - ((float)screenMiddleX - (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f))) / 17.0f;
            if (f < 1.0f) {
                float finWidth = indWidthInc * (f * 17.0f);
                Render2DUtil.drawBorderedRect((float)screenMiddleX - (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f), (float)screenMiddleY + (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f) + 2.0f, (float)screenMiddleX - (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) - (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f) + finWidth, (float)screenMiddleY + (((Crosshair)this.module).gapSize.getValue().floatValue() + ((Crosshair)this.module).length.getValue().floatValue()) + (MovementUtil.isMoving() && ((Crosshair)this.module).gapMode.getValue() == GapMode.DYNAMIC ? ((Crosshair)this.module).gapSize.getValue().floatValue() : 0.0f) + 2.0f + ((Crosshair)this.module).width.getValue().floatValue() * 2.0f, 0.5f, ((Crosshair)this.module).color.getValue().getRGB(), ((Crosshair)this.module).outlineColor.getValue().getRGB());
            }
        }
    }
}
