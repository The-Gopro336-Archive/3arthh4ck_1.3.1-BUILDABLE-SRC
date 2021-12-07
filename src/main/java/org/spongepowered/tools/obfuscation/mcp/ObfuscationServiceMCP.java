package org.spongepowered.tools.obfuscation.mcp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import org.spongepowered.tools.obfuscation.mcp.ObfuscationEnvironmentMCP;
import org.spongepowered.tools.obfuscation.service.IObfuscationService;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

public class ObfuscationServiceMCP
implements IObfuscationService {
    public static final String SEARGE = "searge";
    public static final String NOTCH = "notch";
    public static final String REOBF_SRG_FILE = "reobfSrgFile";
    public static final String REOBF_EXTRA_SRG_FILES = "reobfSrgFiles";
    public static final String REOBF_NOTCH_FILE = "reobfNotchSrgFile";
    public static final String REOBF_EXTRA_NOTCH_FILES = "reobfNotchSrgFiles";
    public static final String OUT_SRG_SRG_FILE = "outSrgFile";
    public static final String OUT_NOTCH_SRG_FILE = "outNotchSrgFile";
    public static final String OUT_REFMAP_FILE = "outRefMapFile";

    @Override
    public Set<String> getSupportedOptions() {
        return ImmutableSet.of(REOBF_SRG_FILE, REOBF_EXTRA_SRG_FILES, REOBF_NOTCH_FILE, REOBF_EXTRA_NOTCH_FILES, OUT_SRG_SRG_FILE, OUT_NOTCH_SRG_FILE, new String[]{OUT_REFMAP_FILE});
    }

    @Override
    public Collection<ObfuscationTypeDescriptor> getObfuscationTypes() {
        return ImmutableList.of(new ObfuscationTypeDescriptor(SEARGE, REOBF_SRG_FILE, REOBF_EXTRA_SRG_FILES, OUT_SRG_SRG_FILE, ObfuscationEnvironmentMCP.class), new ObfuscationTypeDescriptor(NOTCH, REOBF_NOTCH_FILE, REOBF_EXTRA_NOTCH_FILES, OUT_NOTCH_SRG_FILE, ObfuscationEnvironmentMCP.class));
    }
}
