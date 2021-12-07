package com.formdev.flatlaf;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import java.util.ArrayList;
import javax.swing.UIDefaults;

public class IntelliJTheme$ThemeLaf
extends FlatLaf {
    private final IntelliJTheme theme;

    public IntelliJTheme$ThemeLaf(IntelliJTheme theme) {
        this.theme = theme;
    }

    @Override
    public String getName() {
        return this.theme.name;
    }

    @Override
    public String getDescription() {
        return this.getName();
    }

    @Override
    public boolean isDark() {
        return this.theme.dark;
    }

    public IntelliJTheme getTheme() {
        return this.theme;
    }

    @Override
    void applyAdditionalDefaults(UIDefaults defaults) {
        this.theme.applyProperties(defaults);
    }

    protected ArrayList<Class<?>> getLafClassesForDefaultsLoading() {
        ArrayList lafClasses = new ArrayList();
        lafClasses.add(FlatLaf.class);
        lafClasses.add(this.theme.dark ? FlatDarkLaf.class : FlatLightLaf.class);
        lafClasses.add(this.theme.dark ? FlatDarculaLaf.class : FlatIntelliJLaf.class);
        lafClasses.add(IntelliJTheme$ThemeLaf.class);
        return lafClasses;
    }
}
