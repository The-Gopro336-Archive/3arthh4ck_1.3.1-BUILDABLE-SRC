/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

public class CachedRender {
    protected static SGL GL = Renderer.get();
    private Runnable runnable;
    private int list = -1;

    public CachedRender(Runnable runnable) {
        this.runnable = runnable;
        this.build();
    }

    private void build() {
        if (this.list != -1) {
            throw new RuntimeException("Attempt to build the display list more than once in CachedRender");
        }
        this.list = GL.glGenLists(1);
        SlickCallable.enterSafeBlock();
        GL.glNewList(this.list, 4864);
        this.runnable.run();
        GL.glEndList();
        SlickCallable.leaveSafeBlock();
    }

    public void render() {
        if (this.list == -1) {
            throw new RuntimeException("Attempt to render cached operations that have been destroyed");
        }
        SlickCallable.enterSafeBlock();
        GL.glCallList(this.list);
        SlickCallable.leaveSafeBlock();
    }

    public void destroy() {
        GL.glDeleteLists(this.list, 1);
        this.list = -1;
    }
}

