/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package org.newdawn.slick.opengl;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;

public abstract class SlickCallable {
    private static Texture lastUsed;
    private static boolean inSafe;

    public static void enterSafeBlock() {
        if (inSafe) {
            return;
        }
        Renderer.get().flush();
        lastUsed = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        GL11.glPushAttrib((int)1048575);
        GL11.glPushClientAttrib((int)-1);
        GL11.glMatrixMode((int)5888);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5888);
        inSafe = true;
    }

    public static void leaveSafeBlock() {
        if (!inSafe) {
            return;
        }
        GL11.glMatrixMode((int)5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode((int)5888);
        GL11.glPopMatrix();
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();
        if (lastUsed != null) {
            lastUsed.bind();
        } else {
            TextureImpl.bindNone();
        }
        inSafe = false;
    }

    public final void call() throws SlickException {
        SlickCallable.enterSafeBlock();
        this.performGLOperations();
        SlickCallable.leaveSafeBlock();
    }

    protected abstract void performGLOperations() throws SlickException;

    static {
        inSafe = false;
    }
}

