/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf;

import java.io.InputStream;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;

public abstract class FlatDefaultsAddon {
    public InputStream getDefaults(Class<?> lafClass) {
        Class<?> addonClass = this.getClass();
        String propertiesName = '/' + addonClass.getPackage().getName().replace('.', '/') + '/' + lafClass.getSimpleName() + ".properties";
        return addonClass.getResourceAsStream(propertiesName);
    }

    public void afterDefaultsLoading(LookAndFeel laf, UIDefaults defaults) {
    }

    public int getPriority() {
        return 10000;
    }
}

