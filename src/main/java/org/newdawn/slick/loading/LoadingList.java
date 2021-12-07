/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.loading;

import java.util.ArrayList;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;

public class LoadingList {
    private static LoadingList single = new LoadingList();
    private ArrayList deferred = new ArrayList();
    private int total;

    public static LoadingList get() {
        return single;
    }

    public static void setDeferredLoading(boolean loading) {
        single = new LoadingList();
        InternalTextureLoader.get().setDeferredLoading(loading);
        SoundStore.get().setDeferredLoading(loading);
    }

    public static boolean isDeferredLoading() {
        return InternalTextureLoader.get().isDeferredLoading();
    }

    private LoadingList() {
    }

    public void add(DeferredResource resource) {
        ++this.total;
        this.deferred.add(resource);
    }

    public void remove(DeferredResource resource) {
        Log.info("Early loading of deferred resource due to req: " + resource.getDescription());
        --this.total;
        this.deferred.remove(resource);
    }

    public int getTotalResources() {
        return this.total;
    }

    public int getRemainingResources() {
        return this.deferred.size();
    }

    public DeferredResource getNext() {
        if (this.deferred.size() == 0) {
            return null;
        }
        return (DeferredResource)this.deferred.remove(0);
    }
}

