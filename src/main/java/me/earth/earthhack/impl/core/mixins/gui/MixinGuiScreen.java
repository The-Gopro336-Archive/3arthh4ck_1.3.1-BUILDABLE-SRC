/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiConfirmOpenLink
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiYesNoCallback
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.event.ClickEvent
 *  net.minecraft.util.text.event.ClickEvent$Action
 *  net.minecraft.util.text.event.HoverEvent
 *  net.minecraft.util.text.event.HoverEvent$Action
 *  org.apache.logging.log4j.Logger
 */
package me.earth.earthhack.impl.core.mixins.gui;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import me.earth.earthhack.impl.core.ducks.util.IHoverEvent;
import me.earth.earthhack.impl.core.ducks.util.IStyle;
import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GuiScreen.class})
public abstract class MixinGuiScreen
extends Gui
implements GuiYesNoCallback {
    private static final ModuleCache<NoRender> NO_RENDER = Caches.getModule(NoRender.class);
    private static final ResourceLocation BLACK_LOC = new ResourceLocation("earthhack:textures/gui/black.png");
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private static Set<String> PROTOCOLS;
    @Shadow
    public Minecraft mc;
    @Shadow
    private URI clickedLinkURI;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected FontRenderer fontRenderer;

    @Shadow
    protected abstract void openWebLink(URI var1);

    @Shadow
    protected abstract void setText(String var1, boolean var2);

    @Shadow
    public abstract void sendChatMessage(String var1, boolean var2);

    @Shadow
    public static boolean isShiftKeyDown() {
        throw new IllegalStateException("isShiftKeyDown was not shadowed!");
    }

    @Inject(method={"renderToolTip"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        ToolTipEvent event = new ToolTipEvent(stack, x, y);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Redirect(method={"drawBackground"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"))
    private void bindTextureHook(TextureManager textureManager, ResourceLocation resource) {
        if (((NoRender)NO_RENDER.get()).isEnabled() && ((NoRender)MixinGuiScreen.NO_RENDER.get()).defaultBackGround.getValue().booleanValue()) {
            textureManager.bindTexture(BLACK_LOC);
            return;
        }
        textureManager.bindTexture(OPTIONS_BACKGROUND);
    }

    @Inject(method={"handleComponentClick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiScreen;sendChatMessage(Ljava/lang/String;Z)V", shift=At.Shift.BEFORE)}, cancellable=true)
    public void handleComponentClick(ITextComponent component, CallbackInfoReturnable<Boolean> info) {
        IClickEvent event = (IClickEvent)component.getStyle().getClickEvent();
        if (event != null && event.getRunnable() != null) {
            event.getRunnable().run();
            info.setReturnValue(true);
        }
    }

    protected boolean handleClick(ITextComponent component, int button) {
        if (component == null) {
            return false;
        }
        IStyle style = (IStyle)component.getStyle();
        ClickEvent event = null;
        if (button == 1) {
            event = style.getRightClickEvent();
        } else if (button == 2) {
            event = style.getMiddleClickEvent();
        }
        if (MixinGuiScreen.isShiftKeyDown()) {
            String insertion = null;
            if (button == 1) {
                insertion = style.getRightInsertion();
            } else if (button == 2) {
                insertion = style.getMiddleInsertion();
            }
            if (insertion != null) {
                this.setText(insertion, false);
            }
        } else if (event != null) {
            block26: {
                if (event.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }
                    try {
                        URI uri = new URI(event.getValue());
                        String s = uri.getScheme();
                        if (s == null) {
                            throw new URISyntaxException(event.getValue(), "Missing protocol");
                        }
                        if (!PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(event.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                        }
                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen((GuiScreen)new GuiConfirmOpenLink((GuiYesNoCallback)this, event.getValue(), 31102009, false));
                            break block26;
                        }
                        this.openWebLink(uri);
                    }
                    catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for {}", (Object)event, (Object)urisyntaxexception);
                    }
                } else if (event.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = new File(event.getValue()).toURI();
                    this.openWebLink(uri1);
                } else if (event.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(event.getValue(), true);
                } else if (event.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    if (((IClickEvent)event).getRunnable() != null) {
                        ((IClickEvent)event).getRunnable().run();
                        return true;
                    }
                    this.sendChatMessage(event.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)event);
                }
            }
            return true;
        }
        return false;
    }

    @Inject(method={"handleComponentHover"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleComponentHoverHook(ITextComponent component, int x, int y, CallbackInfo info) {
        HoverEvent event;
        if (component != null && component.getStyle().getHoverEvent() != null && (event = component.getStyle().getHoverEvent()).getAction() == HoverEvent.Action.SHOW_TEXT && !((IHoverEvent)event).hasOffset()) {
            this.drawHoveringTextShadow(this.fontRenderer.listFormattedStringToWidth(event.getValue().getFormattedText(), Math.max(this.width / 2, 200)), x, y, this.width, this.height, -1, this.fontRenderer);
            GlStateManager.disableLighting();
            info.cancel();
        }
    }

    private void drawHoveringTextShadow(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tttW = 0;
            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);
                if (textLineWidth <= tttW) continue;
                tttW = textLineWidth;
            }
            boolean needsWrap = false;
            int ttX = mouseX + 12;
            if (ttX + tttW + 4 > screenWidth && (ttX = mouseX - 16 - tttW) < 4) {
                tttW = mouseX > screenWidth / 2 ? mouseX - 12 - 8 : screenWidth - 16 - mouseX;
                needsWrap = true;
            }
            if (maxTextWidth > 0 && tttW > maxTextWidth) {
                tttW = maxTextWidth;
                needsWrap = true;
            }
            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                ArrayList<String> wrappedTextLines = new ArrayList<String>();
                for (String textLine : textLines) {
                    List wrappedLine = font.listFormattedStringToWidth(textLine, tttW);
                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tttW = wrappedTooltipWidth;
                textLines = wrappedTextLines;
                ttX = mouseX > screenWidth / 2 ? mouseX - 16 - tttW : mouseX + 12;
            }
            int ttY = mouseY - 12;
            int ttH = 8;
            if (textLines.size() > 1) {
                ttH += (textLines.size() - 1) * 10;
            }
            if (ttY < 4) {
                ttY = 4;
            } else if (ttY + ttH + 4 > screenHeight) {
                ttY = screenHeight - ttH - 4;
            }
            int zLevel = 300;
            int bgc = -267386864;
            int bgcs = 0x505000FF;
            int bgce = (bgcs & 0xFEFEFE) >> 1 | bgcs & 0xFF000000;
            MixinGuiScreen.drawGradientRectForge(300, ttX - 3, ttY - 4, ttX + tttW + 3, ttY - 3, bgc, bgc);
            MixinGuiScreen.drawGradientRectForge(300, ttX - 3, ttY + ttH + 3, ttX + tttW + 3, ttY + ttH + 4, bgc, bgc);
            MixinGuiScreen.drawGradientRectForge(300, ttX - 3, ttY - 3, ttX + tttW + 3, ttY + ttH + 3, bgc, bgc);
            MixinGuiScreen.drawGradientRectForge(300, ttX - 4, ttY - 3, ttX - 3, ttY + ttH + 3, bgc, bgc);
            MixinGuiScreen.drawGradientRectForge(300, ttX + tttW + 3, ttY - 3, ttX + tttW + 4, ttY + ttH + 3, bgc, bgc);
            MixinGuiScreen.drawGradientRectForge(300, ttX - 3, ttY - 3 + 1, ttX - 3 + 1, ttY + ttH + 3 - 1, bgcs, bgce);
            MixinGuiScreen.drawGradientRectForge(300, ttX + tttW + 2, ttY - 3 + 1, ttX + tttW + 3, ttY + ttH + 3 - 1, bgcs, bgce);
            MixinGuiScreen.drawGradientRectForge(300, ttX - 3, ttY - 3, ttX + tttW + 3, ttY - 3 + 1, bgcs, bgcs);
            MixinGuiScreen.drawGradientRectForge(300, ttX - 3, ttY + ttH + 2, ttX + tttW + 3, ttY + ttH + 3, bgce, bgce);
            for (String line : textLines) {
                font.drawStringWithShadow(line, (float)ttX, (float)ttY, -1);
                ttY += 10;
            }
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    private static void drawGradientRectForge(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float startRed = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float startGreen = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float startBlue = (float)(startColor & 0xFF) / 255.0f;
        float endAlpha = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float endRed = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float endGreen = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float endBlue = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos((double)right, (double)top, (double)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos((double)left, (double)top, (double)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos((double)left, (double)bottom, (double)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.pos((double)right, (double)bottom, (double)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}

