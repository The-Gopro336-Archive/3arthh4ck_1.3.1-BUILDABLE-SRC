package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;

public abstract class AbstractConnection
implements IConnection {
    protected final Socket socket;
    protected String name;

    public AbstractConnection(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public AbstractConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void send(byte[] packet) throws IOException {
        this.socket.getOutputStream().write(packet);
    }

    @Override
    public void close() {
        try {
            this.socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }
}
