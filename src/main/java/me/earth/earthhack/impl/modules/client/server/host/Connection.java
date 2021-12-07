package me.earth.earthhack.impl.modules.client.server.host;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.impl.modules.client.server.api.AbstractConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.IPacket;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

public final class Connection
extends AbstractConnection
implements SafeRunnable {
    private static final AtomicInteger ID = new AtomicInteger();
    private final IConnectionManager manager;
    private final int id;

    public Connection(IConnectionManager manager, Socket socket) {
        super(socket);
        this.manager = manager;
        this.name = "unknown";
        this.id = ID.incrementAndGet();
    }

    @Override
    public void runSafely() throws Throwable {
        try (DataInputStream in = new DataInputStream(this.getInputStream());){
            while (this.isOpen()) {
                IPacket p = ProtocolUtil.readPacket(in);
                this.manager.getHandler().handle(this, p.getId(), p.getBuffer());
            }
        }
    }

    @Override
    public void handle(Throwable t) {
        this.manager.remove(this);
    }

    @Override
    public String getName() {
        return this.name == null ? this.id + "" : this.name;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
