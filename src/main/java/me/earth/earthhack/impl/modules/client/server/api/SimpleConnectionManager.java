package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionListener;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.IPacketManager;

public class SimpleConnectionManager
implements IConnectionManager {
    private final IPacketManager packetManager;
    private final List<IConnectionListener> listeners;
    private final List<IConnection> connections;
    private final int maxConnections;

    public SimpleConnectionManager(IPacketManager packetManager, int maxConnections) {
        this.packetManager = packetManager;
        this.maxConnections = maxConnections;
        this.connections = new CopyOnWriteArrayList<IConnection>();
        this.listeners = new CopyOnWriteArrayList<IConnectionListener>();
    }

    @Override
    public IPacketManager getHandler() {
        return this.packetManager;
    }

    @Override
    public boolean accept(IConnection client) {
        if (this.connections.size() >= this.maxConnections) {
            return false;
        }
        this.connections.add(client);
        for (IConnectionListener listener : this.listeners) {
            if (listener == null) continue;
            listener.onJoin(this, client);
        }
        return true;
    }

    @Override
    public void remove(IConnection connection) {
        if (connection.isOpen()) {
            connection.close();
        }
        this.connections.remove(connection);
        for (IConnectionListener listener : this.listeners) {
            if (listener == null) continue;
            listener.onLeave(this, connection);
        }
    }

    @Override
    public List<IConnection> getConnections() {
        return this.connections;
    }

    @Override
    public void addListener(IConnectionListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(IConnectionListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void send(byte[] packet) throws IOException {
        for (IConnection connection : this.connections) {
            try {
                connection.send(packet);
            }
            catch (IOException e) {
                this.remove(connection);
                e.printStackTrace();
            }
        }
    }
}
