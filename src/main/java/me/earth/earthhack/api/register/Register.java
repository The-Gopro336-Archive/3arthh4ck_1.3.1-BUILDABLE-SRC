/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.register;

import java.util.Collection;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;

public interface Register<T extends Nameable> {
    public void register(T var1) throws AlreadyRegisteredException;

    public void unregister(T var1) throws CantUnregisterException;

    public T getObject(String var1);

    public <C extends T> C getByClass(Class<C> var1);

    public Collection<T> getRegistered();
}

