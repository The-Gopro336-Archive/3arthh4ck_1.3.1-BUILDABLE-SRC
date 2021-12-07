package de.matthiasmann.twl.utils;

import de.matthiasmann.twl.utils.PNGDecoder;

class PNGDecoder$1 {
    static final int[] $SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format;

    static {
        $SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format = new int[PNGDecoder.Format.values().length];
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.ABGR.ordinal()] = 1;
        }
        catch (NoSuchFieldError ex) {
            // empty catch block
        }
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.RGBA.ordinal()] = 2;
        }
        catch (NoSuchFieldError ex) {
            // empty catch block
        }
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.BGRA.ordinal()] = 3;
        }
        catch (NoSuchFieldError ex) {
            // empty catch block
        }
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.RGB.ordinal()] = 4;
        }
        catch (NoSuchFieldError ex) {
            // empty catch block
        }
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.LUMINANCE.ordinal()] = 5;
        }
        catch (NoSuchFieldError ex) {
            // empty catch block
        }
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.ALPHA.ordinal()] = 6;
        }
        catch (NoSuchFieldError ex) {
            // empty catch block
        }
        try {
            PNGDecoder$1.$SwitchMap$de$matthiasmann$twl$utils$PNGDecoder$Format[PNGDecoder.Format.LUMINANCE_ALPHA.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
