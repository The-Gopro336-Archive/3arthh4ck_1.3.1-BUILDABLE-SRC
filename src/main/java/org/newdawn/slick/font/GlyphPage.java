/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.font;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

public class GlyphPage {
    private static final SGL GL = Renderer.get();
    public static final int MAX_GLYPH_SIZE = 256;
    private static ByteBuffer scratchByteBuffer = ByteBuffer.allocateDirect(262144);
    private static IntBuffer scratchIntBuffer;
    private static BufferedImage scratchImage;
    private static Graphics2D scratchGraphics;
    public static FontRenderContext renderContext;
    private final UnicodeFont unicodeFont;
    private final int pageWidth;
    private final int pageHeight;
    private final Image pageImage;
    private int pageX;
    private int pageY;
    private int rowHeight;
    private boolean orderAscending;
    private final List pageGlyphs = new ArrayList(32);

    public static Graphics2D getScratchGraphics() {
        return scratchGraphics;
    }

    public GlyphPage(UnicodeFont unicodeFont, int pageWidth, int pageHeight) throws SlickException {
        this.unicodeFont = unicodeFont;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.pageImage = new Image(pageWidth, pageHeight);
    }

    public int loadGlyphs(List glyphs, int maxGlyphsToLoad) throws SlickException {
        if (this.rowHeight != 0 && maxGlyphsToLoad == -1) {
            int testX = this.pageX;
            int testY = this.pageY;
            int testRowHeight = this.rowHeight;
            Iterator iter = this.getIterator(glyphs);
            while (iter.hasNext()) {
                Glyph glyph = (Glyph)iter.next();
                int width = glyph.getWidth();
                int height = glyph.getHeight();
                if (testX + width >= this.pageWidth) {
                    testX = 0;
                    testY += testRowHeight;
                    testRowHeight = height;
                } else if (height > testRowHeight) {
                    testRowHeight = height;
                }
                if (testY + testRowHeight >= this.pageWidth) {
                    return 0;
                }
                testX += width;
            }
        }
        Color.white.bind();
        this.pageImage.bind();
        int i = 0;
        Iterator iter = this.getIterator(glyphs);
        while (iter.hasNext()) {
            Glyph glyph = (Glyph)iter.next();
            int width = Math.min(256, glyph.getWidth());
            int height = Math.min(256, glyph.getHeight());
            if (this.rowHeight == 0) {
                this.rowHeight = height;
            } else if (this.pageX + width >= this.pageWidth) {
                if (this.pageY + this.rowHeight + height >= this.pageHeight) break;
                this.pageX = 0;
                this.pageY += this.rowHeight;
                this.rowHeight = height;
            } else if (height > this.rowHeight) {
                if (this.pageY + height >= this.pageHeight) break;
                this.rowHeight = height;
            }
            this.renderGlyph(glyph, width, height);
            this.pageGlyphs.add(glyph);
            this.pageX += width;
            iter.remove();
            if (++i != maxGlyphsToLoad) continue;
            this.orderAscending = !this.orderAscending;
            break;
        }
        TextureImpl.bindNone();
        this.orderAscending = !this.orderAscending;
        return i;
    }

    private void renderGlyph(Glyph glyph, int width, int height) throws SlickException {
        scratchGraphics.setComposite(AlphaComposite.Clear);
        scratchGraphics.fillRect(0, 0, 256, 256);
        scratchGraphics.setComposite(AlphaComposite.SrcOver);
        scratchGraphics.setColor(java.awt.Color.white);
        Iterator iter = this.unicodeFont.getEffects().iterator();
        while (iter.hasNext()) {
            ((Effect)iter.next()).draw(scratchImage, scratchGraphics, this.unicodeFont, glyph);
        }
        glyph.setShape(null);
        WritableRaster raster = scratchImage.getRaster();
        int[] row = new int[width];
        for (int y = 0; y < height; ++y) {
            raster.getDataElements(0, y, width, 1, row);
            scratchIntBuffer.put(row);
        }
        GL.glTexSubImage2D(3553, 0, this.pageX, this.pageY, width, height, 32993, 5121, scratchByteBuffer);
        scratchIntBuffer.clear();
        glyph.setImage(this.pageImage.getSubImage(this.pageX, this.pageY, width, height));
    }

    private Iterator getIterator(List glyphs) {
        if (this.orderAscending) {
            return glyphs.iterator();
        }
        final ListIterator iter = glyphs.listIterator(glyphs.size());
        return new Iterator(){

            @Override
            public boolean hasNext() {
                return iter.hasPrevious();
            }

            public Object next() {
                return iter.previous();
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    public List getGlyphs() {
        return this.pageGlyphs;
    }

    public Image getImage() {
        return this.pageImage;
    }

    static {
        scratchByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        scratchIntBuffer = scratchByteBuffer.asIntBuffer();
        scratchImage = new BufferedImage(256, 256, 2);
        scratchGraphics = (Graphics2D)scratchImage.getGraphics();
        scratchGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        scratchGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        scratchGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        renderContext = scratchGraphics.getFontRenderContext();
    }
}

