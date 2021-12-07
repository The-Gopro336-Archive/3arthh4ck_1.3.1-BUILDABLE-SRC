package com.formdev.flatlaf;

import com.formdev.flatlaf.FlatDarkLaf;

public class FlatDarculaLaf
extends FlatDarkLaf {
    public static final String NAME = "FlatLaf Darcula";

    public static boolean setup() {
        return FlatDarculaLaf.setup(new FlatDarculaLaf());
    }

    @Deprecated
    public static boolean install() {
        return FlatDarculaLaf.setup();
    }

    public static void installLafInfo() {
        FlatDarculaLaf.installLafInfo(NAME, FlatDarculaLaf.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "FlatLaf Darcula Look and Feel";
    }
}
