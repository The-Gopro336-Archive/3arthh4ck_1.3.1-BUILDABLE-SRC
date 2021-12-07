package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.world.storage.MapData;

final class ListenerPostToolTip
extends ModuleListener<ToolTips, ToolTipEvent.Post> {
    public ListenerPostToolTip(ToolTips module) {
        super(module, ToolTipEvent.Post.class);
    }

    @Override
    public void invoke(ToolTipEvent.Post event) {
        MapData mapData;
        if (((ToolTips)this.module).maps.getValue().booleanValue() && !event.getStack().isEmpty() && event.getStack().getItem() instanceof ItemMap && (mapData = Items.FILLED_MAP.getMapData(event.getStack(), ListenerPostToolTip.mc.world)) != null) {
            GlStateManager.pushMatrix();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
            RenderHelper.disableStandardItemLighting();
            mc.getTextureManager().bindTexture(ToolTips.MAP);
            Tessellator instance = Tessellator.getInstance();
            BufferBuilder buffer = instance.getBuffer();
            GlStateManager.translate((float)event.getX(), (float)((float)event.getY() - 67.5f - 5.0f), (float)0.0f);
            GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
            buffer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
            buffer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
            buffer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
            instance.draw();
            ListenerPostToolTip.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
