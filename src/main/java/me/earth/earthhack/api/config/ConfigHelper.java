package me.earth.earthhack.api.config;

import java.io.IOException;
import java.util.Collection;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;

public interface ConfigHelper<C extends Config>
extends Nameable {
    public void save() throws IOException;

    public void refresh() throws IOException;

    public void save(String var1) throws IOException;

    public void load(String var1);

    public void refresh(String var1) throws IOException;

    public void delete(String var1) throws IOException, ConfigDeleteException;

    public Collection<C> getConfigs();
}
