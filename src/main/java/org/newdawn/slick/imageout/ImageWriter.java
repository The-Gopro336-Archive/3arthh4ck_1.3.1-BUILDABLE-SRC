/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.imageout;

import java.io.IOException;
import java.io.OutputStream;
import org.newdawn.slick.Image;

public interface ImageWriter {
    public void saveImage(Image var1, String var2, OutputStream var3, boolean var4) throws IOException;
}

