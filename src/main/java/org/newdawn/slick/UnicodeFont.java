package org.newdawn.slick;

import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.font.Glyph;
import org.newdawn.slick.font.GlyphPage;
import org.newdawn.slick.font.HieroSettings;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.ResourceLoader;

public class UnicodeFont
implements Font {
    private static final int DISPLAY_LIST_CACHE_SIZE = 200;
    private static final int MAX_GLYPH_CODE = 0x10FFFF;
    private static final int PAGE_SIZE = 512;
    private static final int PAGES = 2175;
    private static final SGL GL = Renderer.get();
    private static final DisplayList EMPTY_DISPLAY_LIST = new DisplayList();
    private static final Comparator heightComparator = new Comparator(){

        public int compare(Object o1, Object o2) {
            return ((Glyph)o1).getHeight() - ((Glyph)o2).getHeight();
        }
    };
    private java.awt.Font font;
    private String ttfFileRef;
    private int ascent;
    private int descent;
    private int leading;
    private int spaceWidth;
    private final Glyph[][] glyphs = new Glyph[2175][];
    private final List glyphPages = new ArrayList();
    private final List queuedGlyphs = new ArrayList(256);
    private final List effects = new ArrayList();
    private int paddingTop;
    private int paddingLeft;
    private int paddingBottom;
    private int paddingRight;
    private int paddingAdvanceX;
    private int paddingAdvanceY;
    private Glyph missingGlyph;
    private int glyphPageWidth = 512;
    private int glyphPageHeight = 512;
    private boolean displayListCaching = true;
    private int baseDisplayListID = -1;
    private int eldestDisplayListID;
    private DisplayList eldestDisplayList;
    private final LinkedHashMap displayLists = new LinkedHashMap(200, 1.0f, true){

        protected boolean removeEldestEntry(Map.Entry eldest) {
            DisplayList displayList = (DisplayList)eldest.getValue();
            if (displayList != null) {
                UnicodeFont.this.eldestDisplayListID = displayList.id;
            }
            return this.size() > 200;
        }
    };

    private static java.awt.Font createFont(String ttfFileRef) throws SlickException {
        try {
            return java.awt.Font.createFont(0, ResourceLoader.getResourceAsStream(ttfFileRef));
        }
        catch (FontFormatException ex) {
            throw new SlickException("Invalid font: " + ttfFileRef, ex);
        }
        catch (IOException ex) {
            throw new SlickException("Error reading font: " + ttfFileRef, ex);
        }
    }

    public UnicodeFont(String ttfFileRef, String hieroFileRef) throws SlickException {
        this(ttfFileRef, new HieroSettings(hieroFileRef));
    }

    public UnicodeFont(String ttfFileRef, HieroSettings settings) throws SlickException {
        this.ttfFileRef = ttfFileRef;
        java.awt.Font font = UnicodeFont.createFont(ttfFileRef);
        this.initializeFont(font, settings.getFontSize(), settings.isBold(), settings.isItalic());
        this.loadSettings(settings);
    }

    public UnicodeFont(String ttfFileRef, int size, boolean bold, boolean italic) throws SlickException {
        this.ttfFileRef = ttfFileRef;
        this.initializeFont(UnicodeFont.createFont(ttfFileRef), size, bold, italic);
    }

    public UnicodeFont(java.awt.Font font, String hieroFileRef) throws SlickException {
        this(font, new HieroSettings(hieroFileRef));
    }

    public UnicodeFont(java.awt.Font font, HieroSettings settings) {
        this.initializeFont(font, settings.getFontSize(), settings.isBold(), settings.isItalic());
        this.loadSettings(settings);
    }

    public UnicodeFont(java.awt.Font font) {
        this.initializeFont(font, font.getSize(), font.isBold(), font.isItalic());
    }

    public UnicodeFont(java.awt.Font font, int size, boolean bold, boolean italic) {
        this.initializeFont(font, size, bold, italic);
    }

    private void initializeFont(java.awt.Font baseFont, int size, boolean bold, boolean italic) {
        Map<TextAttribute, ?> attributes = baseFont.getAttributes();
        attributes.put(TextAttribute.SIZE, new Float(size));
        attributes.put(TextAttribute.WEIGHT, bold ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        attributes.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
        try {
            attributes.put((TextAttribute)TextAttribute.class.getDeclaredField("KERNING").get(null), TextAttribute.class.getDeclaredField("KERNING_ON").get(null));
        }
        catch (Exception ignored) {
            // empty catch block
        }
        this.font = baseFont.deriveFont(attributes);
        FontMetrics metrics = GlyphPage.getScratchGraphics().getFontMetrics(this.font);
        this.ascent = metrics.getAscent();
        this.descent = metrics.getDescent();
        this.leading = metrics.getLeading();
        char[] chars = " ".toCharArray();
        GlyphVector vector = this.font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, 0);
        this.spaceWidth = vector.getGlyphLogicalBounds((int)0).getBounds().width;
    }

    private void loadSettings(HieroSettings settings) {
        this.paddingTop = settings.getPaddingTop();
        this.paddingLeft = settings.getPaddingLeft();
        this.paddingBottom = settings.getPaddingBottom();
        this.paddingRight = settings.getPaddingRight();
        this.paddingAdvanceX = settings.getPaddingAdvanceX();
        this.paddingAdvanceY = settings.getPaddingAdvanceY();
        this.glyphPageWidth = settings.getGlyphPageWidth();
        this.glyphPageHeight = settings.getGlyphPageHeight();
        this.effects.addAll(settings.getEffects());
    }

    public void addGlyphs(int startCodePoint, int endCodePoint) {
        for (int codePoint = startCodePoint; codePoint <= endCodePoint; ++codePoint) {
            this.addGlyphs(new String(Character.toChars(codePoint)));
        }
    }

    public void addGlyphs(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        char[] chars = text.toCharArray();
        GlyphVector vector = this.font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, 0);
        int n = vector.getNumGlyphs();
        for (int i = 0; i < n; ++i) {
            int codePoint = text.codePointAt(vector.getGlyphCharIndex(i));
            Rectangle bounds = this.getGlyphBounds(vector, i, codePoint);
            this.getGlyph(vector.getGlyphCode(i), codePoint, bounds, vector, i);
        }
    }

    public void addAsciiGlyphs() {
        this.addGlyphs(32, 255);
    }

    public void addNeheGlyphs() {
        this.addGlyphs(32, 128);
    }

    public boolean loadGlyphs() throws SlickException {
        return this.loadGlyphs(-1);
    }

    public boolean loadGlyphs(int maxGlyphsToLoad) throws SlickException {
        if (this.queuedGlyphs.isEmpty()) {
            return false;
        }
        if (this.effects.isEmpty()) {
            throw new IllegalStateException("The UnicodeFont must have at least one effect before any glyphs can be loaded.");
        }
        Iterator iter = this.queuedGlyphs.iterator();
        while (iter.hasNext()) {
            Glyph glyph = (Glyph)iter.next();
            int codePoint = glyph.getCodePoint();
            if (glyph.getWidth() == 0 || codePoint == 32) {
                iter.remove();
                continue;
            }
            if (!glyph.isMissing()) continue;
            if (this.missingGlyph != null) {
                if (glyph == this.missingGlyph) continue;
                iter.remove();
                continue;
            }
            this.missingGlyph = glyph;
        }
        Collections.sort(this.queuedGlyphs, heightComparator);
        for (GlyphPage glyphPage : this.glyphPages) {
            if ((maxGlyphsToLoad -= glyphPage.loadGlyphs(this.queuedGlyphs, maxGlyphsToLoad)) != 0 && !this.queuedGlyphs.isEmpty()) continue;
            return true;
        }
        while (!this.queuedGlyphs.isEmpty()) {
            GlyphPage glyphPage = new GlyphPage(this, this.glyphPageWidth, this.glyphPageHeight);
            this.glyphPages.add(glyphPage);
            if ((maxGlyphsToLoad -= glyphPage.loadGlyphs(this.queuedGlyphs, maxGlyphsToLoad)) != 0) continue;
            return true;
        }
        return true;
    }

    public void clearGlyphs() {
        for (int i = 0; i < 2175; ++i) {
            this.glyphs[i] = null;
        }
        for (GlyphPage page : this.glyphPages) {
            try {
                page.getImage().destroy();
            }
            catch (SlickException slickException) {}
        }
        this.glyphPages.clear();
        if (this.baseDisplayListID != -1) {
            GL.glDeleteLists(this.baseDisplayListID, this.displayLists.size());
            this.baseDisplayListID = -1;
        }
        this.queuedGlyphs.clear();
        this.missingGlyph = null;
    }

    public void destroy() {
        this.clearGlyphs();
    }

    /*
     * Enabled aggressive block sorting
     */
    public DisplayList drawDisplayList(float x, float y, String text, Color color, int startIndex, int endIndex) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        if (text.length() == 0) {
            return EMPTY_DISPLAY_LIST;
        }
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null.");
        }
        x -= (float)this.paddingLeft;
        y -= (float)this.paddingTop;
        String displayListKey = text.substring(startIndex, endIndex);
        color.bind();
        TextureImpl.bindNone();
        DisplayList displayList = null;
        if (this.displayListCaching && this.queuedGlyphs.isEmpty()) {
            if (this.baseDisplayListID == -1) {
                this.baseDisplayListID = GL.glGenLists(200);
                if (this.baseDisplayListID == 0) {
                    this.baseDisplayListID = -1;
                    this.displayListCaching = false;
                    return new DisplayList();
                }
            }
            if ((displayList = (DisplayList)this.displayLists.get(displayListKey)) != null) {
                if (!displayList.invalid) {
                    GL.glTranslatef(x, y, 0.0f);
                    GL.glCallList(displayList.id);
                    GL.glTranslatef(-x, -y, 0.0f);
                    return displayList;
                }
                displayList.invalid = false;
            } else if (displayList == null) {
                displayList = new DisplayList();
                int displayListCount = this.displayLists.size();
                this.displayLists.put(displayListKey, displayList);
                displayList.id = displayListCount < 200 ? this.baseDisplayListID + displayListCount : this.eldestDisplayListID;
            }
            this.displayLists.put(displayListKey, displayList);
        }
        GL.glTranslatef(x, y, 0.0f);
        if (displayList != null) {
            GL.glNewList(displayList.id, 4865);
        }
        char[] chars = text.substring(0, endIndex).toCharArray();
        GlyphVector vector = this.font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, 0);
        int maxWidth = 0;
        int totalHeight = 0;
        int lines = 0;
        int extraX = 0;
        int extraY = this.ascent;
        boolean startNewLine = false;
        Texture lastBind = null;
        int n = vector.getNumGlyphs();
        for (int glyphIndex = 0; glyphIndex < n; ++glyphIndex) {
            Image image;
            int charIndex = vector.getGlyphCharIndex(glyphIndex);
            if (charIndex < startIndex) continue;
            if (charIndex > endIndex) break;
            int codePoint = text.codePointAt(charIndex);
            Rectangle bounds = this.getGlyphBounds(vector, glyphIndex, codePoint);
            Glyph glyph = this.getGlyph(vector.getGlyphCode(glyphIndex), codePoint, bounds, vector, glyphIndex);
            if (startNewLine && codePoint != 10) {
                extraX = -bounds.x;
                startNewLine = false;
            }
            if ((image = glyph.getImage()) == null && this.missingGlyph != null && glyph.isMissing()) {
                image = this.missingGlyph.getImage();
            }
            if (image != null) {
                Texture texture = image.getTexture();
                if (lastBind != null && lastBind != texture) {
                    GL.glEnd();
                    lastBind = null;
                }
                if (lastBind == null) {
                    texture.bind();
                    GL.glBegin(7);
                    lastBind = texture;
                }
                image.drawEmbedded(bounds.x + extraX, bounds.y + extraY, image.getWidth(), image.getHeight());
            }
            if (glyphIndex >= 0) {
                extraX += this.paddingRight + this.paddingLeft + this.paddingAdvanceX;
            }
            maxWidth = Math.max(maxWidth, bounds.x + extraX + bounds.width);
            totalHeight = Math.max(totalHeight, this.ascent + bounds.y + bounds.height);
            if (codePoint != 10) continue;
            startNewLine = true;
            extraY += this.getLineHeight();
            ++lines;
            totalHeight = 0;
        }
        if (lastBind != null) {
            GL.glEnd();
        }
        if (displayList != null) {
            GL.glEndList();
            if (!this.queuedGlyphs.isEmpty()) {
                displayList.invalid = true;
            }
        }
        GL.glTranslatef(-x, -y, 0.0f);
        if (displayList == null) {
            displayList = new DisplayList();
        }
        displayList.width = (short)maxWidth;
        displayList.height = (short)(lines * this.getLineHeight() + totalHeight);
        return displayList;
    }

    @Override
    public void drawString(float x, float y, String text, Color color, int startIndex, int endIndex) {
        this.drawDisplayList(x, y, text, color, startIndex, endIndex);
    }

    @Override
    public void drawString(float x, float y, String text) {
        this.drawString(x, y, text, Color.white);
    }

    @Override
    public void drawString(float x, float y, String text, Color col) {
        this.drawString(x, y, text, col, 0, text.length());
    }

    private Glyph getGlyph(int glyphCode, int codePoint, Rectangle bounds, GlyphVector vector, int index) {
        if (glyphCode < 0 || glyphCode >= 0x10FFFF) {
            return new Glyph(codePoint, bounds, vector, index, this){

                @Override
                public boolean isMissing() {
                    return true;
                }
            };
        }
        int pageIndex = glyphCode / 512;
        int glyphIndex = glyphCode & 0x1FF;
        Glyph glyph = null;
        Glyph[] page = this.glyphs[pageIndex];
        if (page != null) {
            glyph = page[glyphIndex];
            if (glyph != null) {
                return glyph;
            }
        } else {
            this.glyphs[pageIndex] = new Glyph[512];
            page = this.glyphs[pageIndex];
        }
        glyph = page[glyphIndex] = new Glyph(codePoint, bounds, vector, index, this);
        this.queuedGlyphs.add(glyph);
        return glyph;
    }

    private Rectangle getGlyphBounds(GlyphVector vector, int index, int codePoint) {
        Rectangle bounds = vector.getGlyphPixelBounds(index, GlyphPage.renderContext, 0.0f, 0.0f);
        if (codePoint == 32) {
            bounds.width = this.spaceWidth;
        }
        return bounds;
    }

    public int getSpaceWidth() {
        return this.spaceWidth;
    }

    @Override
    public int getWidth(String text) {
        DisplayList displayList;
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        if (text.length() == 0) {
            return 0;
        }
        if (this.displayListCaching && (displayList = (DisplayList)this.displayLists.get(text)) != null) {
            return displayList.width;
        }
        char[] chars = text.toCharArray();
        GlyphVector vector = this.font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, 0);
        int width = 0;
        int extraX = 0;
        boolean startNewLine = false;
        int n = vector.getNumGlyphs();
        for (int glyphIndex = 0; glyphIndex < n; ++glyphIndex) {
            int charIndex = vector.getGlyphCharIndex(glyphIndex);
            int codePoint = text.codePointAt(charIndex);
            Rectangle bounds = this.getGlyphBounds(vector, glyphIndex, codePoint);
            if (startNewLine && codePoint != 10) {
                extraX = -bounds.x;
            }
            if (glyphIndex > 0) {
                extraX += this.paddingLeft + this.paddingRight + this.paddingAdvanceX;
            }
            width = Math.max(width, bounds.x + extraX + bounds.width);
            if (codePoint != 10) continue;
            startNewLine = true;
        }
        return width;
    }

    @Override
    public int getHeight(String text) {
        DisplayList displayList;
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        if (text.length() == 0) {
            return 0;
        }
        if (this.displayListCaching && (displayList = (DisplayList)this.displayLists.get(text)) != null) {
            return displayList.height;
        }
        char[] chars = text.toCharArray();
        GlyphVector vector = this.font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, 0);
        int lines = 0;
        int height = 0;
        int n = vector.getNumGlyphs();
        for (int i = 0; i < n; ++i) {
            int charIndex = vector.getGlyphCharIndex(i);
            int codePoint = text.codePointAt(charIndex);
            if (codePoint == 32) continue;
            Rectangle bounds = this.getGlyphBounds(vector, i, codePoint);
            height = Math.max(height, this.ascent + bounds.y + bounds.height);
            if (codePoint != 10) continue;
            ++lines;
            height = 0;
        }
        return lines * this.getLineHeight() + height;
    }

    public int getYOffset(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        DisplayList displayList = null;
        if (this.displayListCaching && (displayList = (DisplayList)this.displayLists.get(text)) != null && displayList.yOffset != null) {
            return displayList.yOffset.intValue();
        }
        int index = text.indexOf(10);
        if (index != -1) {
            text = text.substring(0, index);
        }
        char[] chars = text.toCharArray();
        GlyphVector vector = this.font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, 0);
        int yOffset = this.ascent + vector.getPixelBounds(null, (float)0.0f, (float)0.0f).y;
        if (displayList != null) {
            displayList.yOffset = new Short((short)yOffset);
        }
        return yOffset;
    }

    public java.awt.Font getFont() {
        return this.font;
    }

    public int getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getPaddingAdvanceX() {
        return this.paddingAdvanceX;
    }

    public void setPaddingAdvanceX(int paddingAdvanceX) {
        this.paddingAdvanceX = paddingAdvanceX;
    }

    public int getPaddingAdvanceY() {
        return this.paddingAdvanceY;
    }

    public void setPaddingAdvanceY(int paddingAdvanceY) {
        this.paddingAdvanceY = paddingAdvanceY;
    }

    @Override
    public int getLineHeight() {
        return this.descent + this.ascent + this.leading + this.paddingTop + this.paddingBottom + this.paddingAdvanceY;
    }

    public int getAscent() {
        return this.ascent;
    }

    public int getDescent() {
        return this.descent;
    }

    public int getLeading() {
        return this.leading;
    }

    public int getGlyphPageWidth() {
        return this.glyphPageWidth;
    }

    public void setGlyphPageWidth(int glyphPageWidth) {
        this.glyphPageWidth = glyphPageWidth;
    }

    public int getGlyphPageHeight() {
        return this.glyphPageHeight;
    }

    public void setGlyphPageHeight(int glyphPageHeight) {
        this.glyphPageHeight = glyphPageHeight;
    }

    public List getGlyphPages() {
        return this.glyphPages;
    }

    public List getEffects() {
        return this.effects;
    }

    public boolean isCaching() {
        return this.displayListCaching;
    }

    public void setDisplayListCaching(boolean displayListCaching) {
        this.displayListCaching = displayListCaching;
    }

    public String getFontFile() {
        if (this.ttfFileRef == null) {
            try {
                Object font2D = Class.forName("sun.font.FontManager").getDeclaredMethod("getFont2D", java.awt.Font.class).invoke(null, this.font);
                Field platNameField = Class.forName("sun.font.PhysicalFont").getDeclaredField("platName");
                platNameField.setAccessible(true);
                this.ttfFileRef = (String)platNameField.get(font2D);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.ttfFileRef == null) {
                this.ttfFileRef = "";
            }
        }
        if (this.ttfFileRef.length() == 0) {
            return null;
        }
        return this.ttfFileRef;
    }

    public static class DisplayList {
        boolean invalid;
        int id;
        Short yOffset;
        public short width;
        public short height;
        public Object userData;

        DisplayList() {
        }
    }
}
