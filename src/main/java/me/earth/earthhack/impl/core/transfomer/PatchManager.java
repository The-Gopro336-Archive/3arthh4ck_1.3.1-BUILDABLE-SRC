/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.core.transfomer;

import me.earth.earthhack.impl.core.transfomer.Patch;

public interface PatchManager {
    public void addPatch(Patch var1);

    public byte[] transform(String var1, String var2, byte[] var3);
}

