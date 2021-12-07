/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.io.Files
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.java.decompiler.main.Fernflower
 *  org.jetbrains.java.decompiler.main.extern.IBytecodeProvider
 *  org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
 *  org.jetbrains.java.decompiler.main.extern.IFernflowerLogger$Severity
 *  org.jetbrains.java.decompiler.main.extern.IResultSaver
 *  org.jetbrains.java.decompiler.util.InterpreterUtil
 */
package org.spongepowered.asm.mixin.transformer.debug;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
    private final Map<String, Object> options = ImmutableMap.builder().put((Object)"din", (Object)"0").put((Object)"rbr", (Object)"0").put((Object)"dgs", (Object)"1").put((Object)"asc", (Object)"1").put((Object)"den", (Object)"1").put((Object)"hdc", (Object)"1").put((Object)"ind", (Object)"    ").build();
    private final File outputPath;
    protected final Logger logger = LogManager.getLogger((String)"fernflower");

    public RuntimeDecompiler(File outputPath) {
        this.outputPath = outputPath;
        if (this.outputPath.exists()) {
            try {
                FileUtils.deleteDirectory((File)this.outputPath);
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

                public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
                    if (this.byteCode == null) {
                        this.byteCode = InterpreterUtil.getBytes((File)new File(externalPath));
                    }
                    return this.byteCode;
                }
            }, (IResultSaver)this, this.options, (IFernflowerLogger)this);
            fernflower.getStructContext().addSpace(file, true);
            fernflower.decompileContext();
        }
        catch (Throwable ex) {
            this.logger.warn("Decompilation error while processing {}", new Object[]{file.getName()});
        }
    }

    public void saveFolder(String path) {
    }

    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        File file = new File(this.outputPath, qualifiedName + ".java");
        file.getParentFile().mkdirs();
        try {
            this.logger.info("Writing {}", new Object[]{file.getAbsolutePath()});
            Files.write((CharSequence)content, (File)file, (Charset)Charsets.UTF_8);
        }
        catch (IOException ex) {
            this.writeMessage("Cannot write source file " + file, ex);
        }
    }

    public void startReadingClass(String className) {
        this.logger.info("Decompiling {}", new Object[]{className});
    }

    public void writeMessage(String message, IFernflowerLogger.Severity severity) {
        this.logger.log(SEVERITY_LEVELS[severity.ordinal()], message);
    }

    public void writeMessage(String message, Throwable t) {
        this.logger.warn("{} {}: {}", new Object[]{message, t.getClass().getSimpleName(), t.getMessage()});
    }

    public void writeMessage(String message, IFernflowerLogger.Severity severity, Throwable t) {
        this.logger.log(SEVERITY_LEVELS[severity.ordinal()], message, t);
    }

    public void copyFile(String source, String path, String entryName) {
    }

    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    public void copyEntry(String source, String path, String archiveName, String entry) {
    }

    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
    }

    public void closeArchive(String path, String archiveName) {
    }
}

