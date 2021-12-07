package me.earth.earthhack.installer;

import java.util.List;
import javax.swing.SwingUtilities;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import me.earth.earthhack.installer.Installer;
import me.earth.earthhack.installer.gui.ErrorPanel;
import me.earth.earthhack.installer.gui.InstallerFrame;
import me.earth.earthhack.installer.gui.VersionPanel;
import me.earth.earthhack.installer.main.Library;
import me.earth.earthhack.installer.main.LibraryClassLoader;
import me.earth.earthhack.installer.main.LibraryFinder;
import me.earth.earthhack.installer.main.MinecraftFiles;
import me.earth.earthhack.installer.service.InstallerService;
import me.earth.earthhack.installer.version.Version;
import me.earth.earthhack.installer.version.VersionFinder;

public class EarthhackInstaller
implements Installer {
    private final MinecraftFiles files = new MinecraftFiles();
    private final InstallerFrame gui = new InstallerFrame();
    private InstallerService service;

    public void launch(LibraryClassLoader classLoader, String[] args) {
        SwingUtilities.invokeLater(this.gui::display);
        this.wrapErrorGui(() -> {
            this.files.findFiles(args);
            LibraryFinder libraryFinder = new LibraryFinder();
            for (Library library : libraryFinder.findLibraries(this.files)) {
                classLoader.installLibrary(library);
            }
            this.service = new InstallerService();
            this.refreshVersions();
        });
    }

    @Override
    public boolean refreshVersions() {
        return this.wrapErrorGui(() -> {
            VersionFinder versionFinder = new VersionFinder();
            List<Version> versions = versionFinder.findVersions(this.files);
            this.gui.schedule(new VersionPanel(this, versions));
        });
    }

    @Override
    public boolean install(Version version) {
        return this.wrapErrorGui(() -> {
            this.service.install(this.files, version);
            this.refreshVersions();
        });
    }

    @Override
    public boolean uninstall(Version version) {
        return this.wrapErrorGui(() -> {
            this.service.uninstall(version);
            this.refreshVersions();
        });
    }

    @Override
    public boolean update(boolean forge) {
        return this.wrapErrorGui(() -> {
            this.service.update(this.files, forge);
            this.refreshVersions();
        });
    }

    private boolean wrapErrorGui(SafeRunnable runnable) {
        try {
            runnable.runSafely();
            return false;
        }
        catch (Throwable throwable) {
            this.gui.schedule(new ErrorPanel(throwable));
            throwable.printStackTrace();
            return true;
        }
    }
}
