package com.formdev.flatlaf.util;

import java.awt.Image;
import java.awt.image.AbstractMultiResolutionImage;
import java.awt.image.MultiResolutionImage;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import javax.swing.ImageIcon;

class MultiResolutionImageSupport$MappedMultiResolutionImage
extends AbstractMultiResolutionImage {
    private final Image mrImage;
    private final Function<Image, Image> mapper;
    private final IdentityHashMap<Image, Image> cache = new IdentityHashMap();

    MultiResolutionImageSupport$MappedMultiResolutionImage(Image mrImage, Function<Image, Image> mapper) {
        assert (mrImage instanceof MultiResolutionImage);
        this.mrImage = mrImage;
        this.mapper = mapper;
    }

    @Override
    public Image getResolutionVariant(double destImageWidth, double destImageHeight) {
        Image variant = ((MultiResolutionImage)((Object)this.mrImage)).getResolutionVariant(destImageWidth, destImageHeight);
        return this.mapAndCacheImage(variant);
    }

    @Override
    public List<Image> getResolutionVariants() {
        List<Image> variants = ((MultiResolutionImage)((Object)this.mrImage)).getResolutionVariants();
        ArrayList<Image> mappedVariants = new ArrayList<Image>();
        for (Image image : variants) {
            mappedVariants.add(this.mapAndCacheImage(image));
        }
        return mappedVariants;
    }

    @Override
    protected Image getBaseImage() {
        return this.mapAndCacheImage(this.mrImage);
    }

    private Image mapAndCacheImage(Image image) {
        return this.cache.computeIfAbsent(image, img -> new ImageIcon(this.mapper.apply((Image)img)).getImage());
    }
}
