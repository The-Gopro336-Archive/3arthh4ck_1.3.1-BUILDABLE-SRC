/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.client.server.api.ICloseable;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.IHost;
import me.earth.earthhack.impl.modules.client.server.api.IShutDownHandler;
import me.earth.earthhack.impl.modules.client.server.host.Connection;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

public final class Host
implements SafeRunnable,
Globals,
IHost {
    private final IConnectionManager manager;
    private final ExecutorService service;
    private final IShutDownHandler module;
    private final ServerSocket socket;
    private final boolean receive;
    private Future<?> future;

    private Host(IConnectionManager connectionManager, ExecutorService service, IShutDownHandler module, int port, boolean receive) throws IOException {
        this.socket = new ServerSocket(port);
        this.service = service;
        this.manager = connectionManager;
        this.module = module;
        this.receive = receive;
    }

    @Override
    public void runSafely() throws Throwable {
        while (!this.future.isCancelled()) {
            Socket client = this.socket.accept();
            Connection connection = new Connection(this.manager, client);
            if (!this.manager.accept(connection)) {
                client.close();
                continue;
            }
            if (!this.receive) continue;
            this.service.submit(connection);
        }
    }

    @Override
    public void handle(Throwable t) {
        this.module.disable(t.getMessage());
    }

    @Override
    public int getPort() {
        return this.socket.getLocalPort();
    }

    @Override
    public IConnectionManager getConnectionManager() {
        return this.manager;
    }

    @Override
    public void close() {
        if (this.future != null) {
            this.future.cancel(true);
        }
        if (this.isOpen()) {
            try {
                this.socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.manager.getConnections().forEach(ICloseable::close);
        this.manager.getConnections().clear();
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public static Host createAndStart(ExecutorService service, IConnectionManager manager, IShutDownHandler module, int port, boolean receive) throws IOException {
        Host host = new Host(manager, service, module, port, receive);
        host.setFuture(service.submit(host));
        return host;
    }
}

