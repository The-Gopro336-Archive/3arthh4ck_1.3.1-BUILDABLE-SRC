/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util;

import java.io.InputStream;
import java.net.URL;

public interface ResourceLocation {
    public InputStream getResourceAsStream(String var1);

    public URL getResource(String var1);
}

