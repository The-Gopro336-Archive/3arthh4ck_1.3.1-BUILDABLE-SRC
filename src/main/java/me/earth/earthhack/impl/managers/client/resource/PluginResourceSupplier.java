package me.earth.earthhack.impl.managers.client.resource;

import java.io.InputStream;
import java.util.Objects;
import me.earth.earthhack.impl.managers.client.resource.PluginResourceLocation;
import me.earth.earthhack.impl.managers.client.resource.ResourceException;
import me.earth.earthhack.impl.managers.client.resource.ResourceSupplier;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.MetadataSerializer;

public class PluginResourceSupplier
implements ResourceSupplier {
    private final PluginResourceLocation location;
    private final MetadataSerializer metadataSerializer;
    private final ClassLoader classLoader;

    public PluginResourceSupplier(PluginResourceLocation location, MetadataSerializer metadataSerializer, ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.metadataSerializer = Objects.requireNonNull(metadataSerializer);
        this.location = location;
    }

    @Override
    public IResource get() throws ResourceException {
        String target = String.format("%s/%s/%s", "assets", this.location.getResourceDomain(), this.location.getResourcePath());
        try {
            InputStream stream = this.classLoader.getResourceAsStream(target);
            if (stream == null) {
                throw new ResourceException("PluginResource: " + this.location + " had no InputStream!");
            }
            return this.location.toResource(this.location.getResourcePack(), this.location, stream, stream, this.metadataSerializer);
        }
        catch (Exception e) {
            throw new ResourceException(e);
        }
    }
}
