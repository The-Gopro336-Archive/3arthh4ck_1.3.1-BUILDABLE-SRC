/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.register;

import me.earth.earthhack.api.register.exception.CantUnregisterException;

public interface Registrable {
    default public void onRegister() {
    }

    default public void onUnRegister() throws CantUnregisterException {
    }
}

