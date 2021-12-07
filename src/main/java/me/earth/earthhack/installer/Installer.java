package me.earth.earthhack.installer;

import me.earth.earthhack.installer.version.Version;

public interface Installer {
    public boolean refreshVersions();

    public boolean install(Version var1);

    public boolean uninstall(Version var1);

    public boolean update(boolean var1);
}
