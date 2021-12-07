package org.newdawn.slick.loading;

import java.io.IOException;

public interface DeferredResource {
    public void load() throws IOException;

    public String getDescription();
}
