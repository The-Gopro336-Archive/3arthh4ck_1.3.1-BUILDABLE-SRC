/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.earth.earthhack.impl.modules.client.server.api.ICloseable;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionEntry;
import me.earth.earthhack.impl.modules.client.server.api.ISender;

public interface IConnection
extends IConnectionEntry,
ICloseable,
ISender {
    public void setName(String var1);

    public InputStream getInputStream() throws IOException;

    public OutputStream getOutputStream() throws IOException;
}

