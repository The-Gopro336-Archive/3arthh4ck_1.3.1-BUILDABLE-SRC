/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.config.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;

public class PlaceHolderConfig
implements ConfigHelper,
Registrable {
    private final String name;

    public PlaceHolderConfig(String name) {
        this.name = name;
    }

    @Override
    public void onUnRegister() throws CantUnregisterException {
        throw new CantUnregisterException(this);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("This is a PlaceHolder");
    }

    @Override
    public void refresh() throws IOException {
        throw new UnsupportedOperationException("This is a PlaceHolder");
    }

    @Override
    public void save(String name) {
        throw new UnsupportedOperationException("This is a PlaceHolder");
    }

    @Override
    public void load(String name) {
        throw new UnsupportedOperationException("This is a PlaceHolder");
    }

    @Override
    public void refresh(String name) throws IOException {
        throw new UnsupportedOperationException("This is a PlaceHolder");
    }

    @Override
    public void delete(String name) throws ConfigDeleteException {
        throw new ConfigDeleteException("This is just a PlaceHolder");
    }

    public Collection<? extends Nameable> getConfigs() {
        return new ArrayList();
    }

    @Override
    public String getName() {
        return this.name;
    }
}

