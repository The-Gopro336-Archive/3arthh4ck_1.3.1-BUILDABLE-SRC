package org.newdawn.slick.font.effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.font.effects.EffectUtil;

public class FilterEffect
implements Effect {
    private BufferedImageOp filter;

    public FilterEffect() {
    }

    public FilterEffect(BufferedImageOp filter) {
        this.filter = filter;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        BufferedImage scratchImage = EffectUtil.getScratchImage();
        this.filter.filter(image, scratchImage);
        image.getGraphics().drawImage(scratchImage, 0, 0, null);
    }

    public BufferedImageOp getFilter() {
        return this.filter;
    }

    public void setFilter(BufferedImageOp filter) {
        this.filter = filter;
    }
}
