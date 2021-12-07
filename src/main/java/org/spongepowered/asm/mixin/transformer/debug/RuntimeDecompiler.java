package org.spongepowered.asm.mixin.transformer.debug;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.jar.Manifest;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.util.InterpreterUtil;
import org.spongepowered.asm.mixin.transformer.ext.IDecompiler;

public class RuntimeDecompiler
extends IFernflowerLogger
implements IDecompiler,
IResultSaver {
    private static final Level[] SEVERITY_LEVELS = new Level[]{Level.TRACE, Level.INFO, Level.WARN, Level.ERROR};
    private final Map<String, Object> options = ImmutableMap.builder().put("din", "0").put("rbr", "0").put("dgs", "1").put("asc", "1").put("den", "1").put("hdc", "1").put("ind", "    ").build();
    private final File outputPath;
    protected final Logger logger = LogManager.getLogger("fernflower");

    public RuntimeDecompiler(File outputPath) {
        this.outputPath = outputPath;
        if (this.outputPath.exists()) {
            try {
                FileUtils.deleteDirectory(this.outputPath);
            }
            catch (IOException ex) {
                this.logger.warn("Error cleaning output directory: {}", new Object[]{ex.getMessage()});
            }
        }
    }

    @Override
    public void decompile(File file) {
        try {
            Fernflower fernflower = new Fernflower(new IBytecodeProvider(){
                private byte[] byteCode;

                @Override
                public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
                    if (this.byteCode == null) {
                        this.byteCode = InterpreterUtil.getBytes(new File(externalPath));
                    }
                    return this.byteCode;
                }
            }, this, this.options, this);
            fernflower.getStructContext().addSpace(file, true);
            fernflower.decompileContext();
        }
        catch (Throwable ex) {
            this.logger.warn("Decompilation error while processing {}", new Object[]{file.getName()});
        }
    }

    @Override
    public void saveFolder(String path) {
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        File file = new File(this.outputPath, qualifiedName + ".java");
        file.getParentFile().mkdirs();
        try {
            this.logger.info("Writing {}", new Object[]{file.getAbsolutePath()});
            Files.write(content, file, Charsets.UTF_8);
        }
        catch (IOException ex) {
            this.writeMessage("Cannot write source file " + file, ex);
        }
    }

    @Override
    public void startReadingClass(String className) {
        this.logger.info("Decompiling {}", new Object[]{className});
    }

    @Override
    public void writeMessage(String message, IFernflowerLogger.Severity severity) {
        this.logger.log(SEVERITY_LEVELS[severity.ordinal()], message);
    }

    @Override
    public void writeMessage(String message, Throwable t) {
        this.logger.warn("{} {}: {}", new Object[]{message, t.getClass().getSimpleName(), t.getMessage()});
    }

    @Override
    public void writeMessage(String message, IFernflowerLogger.Severity severity, Throwable t) {
        this.logger.log(SEVERITY_LEVELS[severity.ordinal()], message, t);
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
    }

    @Override
    public void closeArchive(String path, String archiveName) {
    }
}
