package org.spongepowered.asm.mixin.struct;

import java.util.LinkedHashMap;
import java.util.Map;
import org.spongepowered.asm.mixin.struct.SourceMap;

class SourceMap$Stratum {
    private static final String STRATUM_MARK = "*S";
    private static final String FILE_MARK = "*F";
    private static final String LINES_MARK = "*L";
    public final String name;
    private final Map<String, SourceMap.File> files = new LinkedHashMap<String, SourceMap.File>();

    public SourceMap$Stratum(String name) {
        this.name = name;
    }

    public SourceMap.File addFile(int lineOffset, int size, String sourceFileName, String sourceFilePath) {
        SourceMap.File file = this.files.get(sourceFilePath);
        if (file == null) {
            file = new SourceMap.File(this.files.size() + 1, lineOffset, size, sourceFileName, sourceFilePath);
            this.files.put(sourceFilePath, file);
        }
        return file;
    }

    void appendTo(StringBuilder sb) {
        sb.append(STRATUM_MARK).append(" ").append(this.name).append(SourceMap.NEWLINE);
        sb.append(FILE_MARK).append(SourceMap.NEWLINE);
        for (SourceMap.File file : this.files.values()) {
            file.appendFile(sb);
        }
        sb.append(LINES_MARK).append(SourceMap.NEWLINE);
        for (SourceMap.File file : this.files.values()) {
            file.appendLines(sb);
        }
    }
}
