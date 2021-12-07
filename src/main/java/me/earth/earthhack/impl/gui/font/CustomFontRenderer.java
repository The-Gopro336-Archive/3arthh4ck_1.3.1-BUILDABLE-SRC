package me.earth.earthhack.impl.gui.font;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.font.CharData;
import me.earth.earthhack.impl.gui.font.CustomFont;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CustomFontRenderer
extends CustomFont {
    private static final String COLOR_CODES = "0123456789abcdefklmnorzy+-";
    private static final Random CHAR_RANDOM = new Random();
    private static final List<Character> RANDOM_CHARS = new ArrayList<Character>("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".chars().mapToObj(c -> Character.valueOf((char)c)).collect(Collectors.toList()));
    private static final SettingCache<Boolean, BooleanSetting, FontMod> SHADOW = Caches.getSetting(FontMod.class, BooleanSetting.class, "Shadow", false);
    protected CharData[] boldChars = new CharData[256];
    protected CharData[] italicChars = new CharData[256];
    protected CharData[] boldItalicChars = new CharData[256];
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texBoth;
    private final int[] colorCode = new int[32];

    public CustomFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        float shadowWidth = this.drawString(text, x + 1.0, y + 1.0, color, true);
        return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, int color) {
        return this.drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0f, y, color);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, (double)(x - (float)this.getStringWidth(text) / 2.0f) + 1.0, (double)y + 1.0, color, true);
        return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0f, y, color);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {
        x -= 1.0;
        if (text == null) {
            return 0.0f;
        }
        if (shadow) {
            if (SHADOW.getValue().booleanValue()) {
                x -= 0.4;
                y -= 0.4;
            }
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        CharData[] currentData = this.charData;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        boolean random = false;
        boolean bold = false;
        boolean italic = false;
        boolean strike = false;
        boolean underline = false;
        boolean rainbowP = false;
        boolean rainbowM = false;
        x *= 2.0;
        y = (y - 3.0) * 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((int)770, (int)771);
        GlStateManager.color((float)((float)(color >> 16 & 0xFF) / 255.0f), (float)((float)(color >> 8 & 0xFF) / 255.0f), (float)((float)(color & 0xFF) / 255.0f), (float)alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture((int)this.tex.getGlTextureId());
        GL11.glBindTexture(3553, this.tex.getGlTextureId());
        for (int i = 0; i < text.length(); ++i) {
            char character = text.charAt(i);
            if (character == '\u00a7' && i + 1 < text.length()) {
                int colorIndex = COLOR_CODES.indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    random = false;
                    underline = false;
                    strike = false;
                    rainbowP = false;
                    rainbowM = false;
                    GlStateManager.bindTexture((int)this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color((float)((float)(colorcode >> 16 & 0xFF) / 255.0f), (float)((float)(colorcode >> 8 & 0xFF) / 255.0f), (float)((float)(colorcode & 0xFF) / 255.0f), (float)alpha);
                } else if (colorIndex == 16) {
                    random = true;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture((int)this.texBoth.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture((int)this.texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strike = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture((int)this.texBoth.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture((int)this.texItalic.getGlTextureId());
                        currentData = this.italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    random = false;
                    underline = false;
                    strike = false;
                    rainbowP = false;
                    rainbowM = false;
                    GlStateManager.color((float)((float)(color >> 16 & 0xFF) / 255.0f), (float)((float)(color >> 8 & 0xFF) / 255.0f), (float)((float)(color & 0xFF) / 255.0f), (float)alpha);
                    GlStateManager.bindTexture((int)this.tex.getGlTextureId());
                    currentData = this.charData;
                } else {
                    if (colorIndex == 22) {
                        int colorcode;
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = false;
                        rainbowM = false;
                        GlStateManager.bindTexture((int)this.tex.getGlTextureId());
                        currentData = this.charData;
                        char[] h = new char[8];
                        if (i + 9 < text.length()) {
                            for (int j = 0; j < 8; ++j) {
                                h[j] = text.charAt(i + j + 2);
                            }
                        } else {
                            ++i;
                            continue;
                        }
                        try {
                            colorcode = (int)Long.parseLong(new String(h), 16);
                        }
                        catch (Exception e) {
                            continue;
                        }
                        GlStateManager.color((float)((float)(colorcode >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(colorcode >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(colorcode & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(colorcode >> 24 & 0xFF) / 255.0f));
                        i += 9;
                        continue;
                    }
                    if (colorIndex == 23) {
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = false;
                        rainbowM = false;
                        GlStateManager.bindTexture((int)this.tex.getGlTextureId());
                        currentData = this.charData;
                        int rainbow = Color.HSBtoRGB(Managers.COLOR.getHue(), 1.0f, 1.0f);
                        GlStateManager.color((float)((float)(rainbow >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(rainbow >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(rainbow & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)alpha);
                    } else if (colorIndex == 24) {
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = true;
                        rainbowM = false;
                        GlStateManager.bindTexture((int)this.tex.getGlTextureId());
                        currentData = this.charData;
                    } else {
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = false;
                        rainbowM = true;
                        GlStateManager.bindTexture((int)this.tex.getGlTextureId());
                        currentData = this.charData;
                    }
                }
                ++i;
                continue;
            }
            if (character >= currentData.length) continue;
            if (random) {
                int w = currentData[character].width;
                CharData[] finalCurrentData = currentData;
                List randoms = RANDOM_CHARS.stream().filter(c -> {
                    if (c.charValue() < finalCurrentData.length) {
                        return finalCurrentData[c.charValue()].width == w;
                    }
                    return false;
                }).collect(Collectors.toList());
                if (randoms.size() != 0) {
                    character = ((Character)randoms.get(CHAR_RANDOM.nextInt(randoms.size()))).charValue();
                }
            }
            if (rainbowP || rainbowM) {
                int rainbow = Color.HSBtoRGB(Managers.COLOR.getHueByPosition(rainbowM ? y : x), 1.0f, 1.0f);
                GlStateManager.color((float)((float)(rainbow >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(rainbow >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)((float)(rainbow & 0xFF) / 255.0f / (float)(shadow ? 4 : 1)), (float)alpha);
            }
            GL11.glBegin(4);
            this.drawChar(currentData, character, (float)x, (float)y);
            GL11.glEnd();
            if (strike) {
                this.drawLine(x, y + (double)currentData[character].height / 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height / 2.0);
            }
            if (underline) {
                this.drawLine(x, y + (double)currentData[character].height - 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height - 2.0);
            }
            x += (double)(currentData[character].width - 8 + this.charOffset);
        }
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();
        return (float)(x / 2.0);
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        CharData[] currentData = this.charData;
        int width = 0;
        boolean bold = false;
        boolean italic = false;
        for (int i = 0; i < text.length(); ++i) {
            char character = text.charAt(i);
            if (character == '\u00a7' && i + 1 < text.length()) {
                int colorIndex = COLOR_CODES.indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    currentData = italic ? this.boldItalicChars : this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    currentData = bold ? this.boldItalicChars : this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                } else {
                    if (colorIndex == 22) {
                        bold = false;
                        italic = false;
                        currentData = this.charData;
                        i += 9;
                        continue;
                    }
                    if (colorIndex == 23) {
                        bold = false;
                        italic = false;
                    } else if (colorIndex == 24) {
                        bold = false;
                        italic = false;
                    } else {
                        bold = false;
                        italic = false;
                    }
                }
                ++i;
                continue;
            }
            if (character >= currentData.length) continue;
            width += currentData[character].width - 8 + this.charOffset;
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texBoth = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void drawLine(double x, double y, double x1, double y1) {
        GL11.glDisable(3553);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public List<String> wrapWords(String text, double width) {
        ArrayList<String> result = new ArrayList<String>();
        if ((double)this.getStringWidth(text) > width) {
            String[] words = text.split(" ");
            StringBuilder current = new StringBuilder();
            char lastColorCode = '\uffff';
            for (String word : words) {
                char[] array = word.toCharArray();
                for (int i = 0; i < array.length; ++i) {
                    char c = array[i];
                    if (c != '\u00a7' || i + 1 >= array.length) continue;
                    lastColorCode = array[i + 1];
                }
                StringBuilder stringBuilder = new StringBuilder();
                if ((double)this.getStringWidth(stringBuilder.append((Object)current).append(word).append(" ").toString()) < width) {
                    current.append(word).append(" ");
                    continue;
                }
                result.add(current.toString());
                current = new StringBuilder("\u00a7").append(lastColorCode).append(word).append(" ");
            }
            if (current.length() > 0) {
                if ((double)this.getStringWidth(current.toString()) < width) {
                    result.add("\u00a7" + lastColorCode + current + " ");
                } else {
                    result.addAll(this.formatString(current.toString(), width));
                }
            }
        } else {
            result.add(text);
        }
        return result;
    }

    public List<String> formatString(String string, double width) {
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        char lastColorCode = '\uffff';
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == '\u00a7' && i < chars.length - 1) {
                lastColorCode = chars[i + 1];
            }
            StringBuilder stringBuilder = new StringBuilder();
            if ((double)this.getStringWidth(stringBuilder.append(current.toString()).append(c).toString()) < width) {
                current.append(c);
                continue;
            }
            result.add(current.toString());
            current = new StringBuilder("\u00a7").append(lastColorCode).append(c);
        }
        if (current.length() > 0) {
            result.add(current.toString());
        }
        return result;
    }

    private void setupMinecraftColorcodes() {
        for (int i = 0; i < 32; ++i) {
            int o = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + o;
            int g = (i >> 1 & 1) * 170 + o;
            int b = (i & 1) * 170 + o;
            if (i == 6) {
                r += 85;
            }
            if (i >= 16) {
                r /= 4;
                g /= 4;
                b /= 4;
            }
            this.colorCode[i] = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }
    }
}
