/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
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
        return ImmutableSet.of((Object)REOBF_SRG_FILE, (Object)REOBF_EXTRA_SRG_FILES, (Object)REOBF_NOTCH_FILE, (Object)REOBF_EXTRA_NOTCH_FILES, (Object)OUT_SRG_SRG_FILE, (Object)OUT_NOTCH_SRG_FILE, (Object[])new String[]{OUT_REFMAP_FILE});
    }

    @Override
    public Collection<ObfuscationTypeDescriptor> getObfuscationTypes() {
        return ImmutableList.of((Object)new ObfuscationTypeDescriptor(SEARGE, REOBF_SRG_FILE, REOBF_EXTRA_SRG_FILES, OUT_SRG_SRG_FILE, ObfuscationEnvironmentMCP.class), (Object)new ObfuscationTypeDescriptor(NOTCH, REOBF_NOTCH_FILE, REOBF_EXTRA_NOTCH_FILES, OUT_NOTCH_SRG_FILE, ObfuscationEnvironmentMCP.class));
    }
}

