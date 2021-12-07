package com.formdev.flatlaf.util;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.AbstractMultiResolutionImage;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import javax.swing.ImageIcon;

class MultiResolutionImageSupport$ProducerMultiResolutionImage
extends AbstractMultiResolutionImage {
    private final Dimension[] dimensions;
    private final Function<Dimension, Image> producer;
    private final IdentityHashMap<Dimension, Image> cache = new IdentityHashMap();

    MultiResolutionImageSupport$ProducerMultiResolutionImage(Dimension[] dimensions, Function<Dimension, Image> producer) {
        this.dimensions = dimensions;
        this.producer = producer;
    }

    @Override
    public Image getResolutionVariant(double destImageWidth, double destImageHeight) {
        return this.produceAndCacheImage(new Dimension((int)destImageWidth, (int)destImageHeight));
    }

    @Override
    public List<Image> getResolutionVariants() {
        ArrayList<Image> mappedVariants = new ArrayList<Image>();
        for (Dimension size : this.dimensions) {
            mappedVariants.add(this.produceAndCacheImage(size));
        }
        return mappedVariants;
    }

    @Override
    protected Image getBaseImage() {
        return this.produceAndCacheImage(this.dimensions[0]);
    }

    private Image produceAndCacheImage(Dimension size) {
        return this.cache.computeIfAbsent(size, size2 -> new ImageIcon(this.producer.apply((Dimension)size2)).getImage());
    }
}
