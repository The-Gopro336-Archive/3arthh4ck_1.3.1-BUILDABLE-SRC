/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.IResource
 *  net.minecraft.client.resources.data.MetadataSerializer
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.managers.client.resource;

import java.io.InputStream;
import me.earth.earthhack.impl.managers.client.resource.PluginResource;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class PluginResourceLocation
extends ResourceLocation {
    private final String resourcePack;

    protected PluginResourceLocation(int unused, String resourcePack, String ... resourceName) {
        super(unused, resourceName);
        this.resourcePack = resourcePack;
    }

    public PluginResourceLocation(String resourceName, String resourcePack) {
        super(resourceName);
        this.resourcePack = resourcePack;
    }

    public PluginResourceLocation(String namespaceIn, String pathIn, String resourcePack) {
        super(namespaceIn, pathIn);
        this.resourcePack = resourcePack;
    }

    public String getResourcePack() {
        return this.resourcePack;
    }

    public IResource toResource(String resourcePackNameIn, ResourceLocation srResourceLocationIn, InputStream resourceInputStreamIn, InputStream mcmetaInputStreamIn, MetadataSerializer srMetadataSerializerIn) {
        return new PluginResource(resourcePackNameIn, srResourceLocationIn, resourceInputStreamIn, mcmetaInputStreamIn, srMetadataSerializerIn);
    }
}

