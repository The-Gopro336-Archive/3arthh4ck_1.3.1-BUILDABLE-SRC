package me.earth.earthhack.impl.managers.client.resource;

import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class PluginResource
extends SimpleResource {
    public PluginResource(String resourcePackNameIn, ResourceLocation srResourceLocationIn, InputStream resourceInputStreamIn, InputStream mcmetaInputStreamIn, MetadataSerializer srMetadataSerializerIn) {
        super(resourcePackNameIn, srResourceLocationIn, resourceInputStreamIn, mcmetaInputStreamIn, srMetadataSerializerIn);
    }

    @Nullable
    public <T extends IMetadataSection> T getMetadata(String sectionName) {
        return null;
    }
}
