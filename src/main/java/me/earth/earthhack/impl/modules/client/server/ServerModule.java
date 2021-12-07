/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.server.ListenerCPacket;
import me.earth.earthhack.impl.modules.client.server.ListenerMove;
import me.earth.earthhack.impl.modules.client.server.ListenerNoUpdate;
import me.earth.earthhack.impl.modules.client.server.ListenerStartEating;
import me.earth.earthhack.impl.modules.client.server.ListenerStopEating;
import me.earth.earthhack.impl.modules.client.server.api.IClient;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionEntry;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.IHost;
import me.earth.earthhack.impl.modules.client.server.api.IPacketManager;
import me.earth.earthhack.impl.modules.client.server.api.IServerList;
import me.earth.earthhack.impl.modules.client.server.api.IShutDownHandler;
import me.earth.earthhack.impl.modules.client.server.api.IVelocityHandler;
import me.earth.earthhack.impl.modules.client.server.api.SimpleConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.SimplePacketManager;
import me.earth.earthhack.impl.modules.client.server.api.SimpleServerList;
import me.earth.earthhack.impl.modules.client.server.client.Client;
import me.earth.earthhack.impl.modules.client.server.host.Host;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.EatingHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.PacketHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.PositionHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.ServerListHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.VelocityHandler;
import me.earth.earthhack.impl.modules.client.server.util.ChatLogger;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.entity.player.EntityPlayer;

public class ServerModule
extends Module
implements IShutDownHandler,
GlobalExecutor,
IVelocityHandler {
    protected final Setting<ServerMode> mode = this.register(new EnumSetting<ServerMode>("Mode", ServerMode.Client));
    protected final Setting<String> ip = this.register(new StringSetting("IP", "127.0.0.1"));
    protected final Setting<String> port = this.register(new StringSetting("Port", "0"));
    protected final Setting<Integer> max = this.register(new NumberSetting<Integer>("Connections", 50, 1, 50));
    protected final Setting<Boolean> clientInput = this.register(new BooleanSetting("ClientMessages", false));
    protected final Setting<String> name = this.register(new StringSetting("Name", "3arthh4ck-Host"));
    protected final Setting<Boolean> sync = this.register(new BooleanSetting("Sync", false));
    protected final IServerList serverList = new SimpleServerList();
    protected final IPacketManager sPackets = new SimplePacketManager();
    protected final IPacketManager cPackets = new SimplePacketManager();
    protected ServerMode currentMode;
    protected IConnectionManager connectionManager;
    protected IClient client;
    protected IHost host;
    protected boolean isEating;
    protected double lastX;
    protected double lastY;
    protected double lastZ;

    public ServerModule() {
        super("Server", Category.Client);
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
        this.listeners.add(new ListenerStartEating(this));
        this.listeners.add(new ListenerStopEating(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerNoUpdate(this));
        this.name.setValue(mc.getSession().getProfile().getName());
        Observer<Object> observer = e -> {
            if (this.isEnabled()) {
                ChatUtil.sendMessageScheduled("The server has to be restarted in order for the changes to take effect.");
            }
        };
        this.mode.addObserver(observer);
        this.ip.addObserver(observer);
        this.port.addObserver(observer);
        this.max.addObserver(observer);
        this.clientInput.addObserver(observer);
        this.name.addObserver(observer);
        this.setupConnectionManagers();
    }

    @Override
    public String getDisplayInfo() {
        if (this.host != null) {
            return this.host.getConnectionManager().getConnections().size() + "";
        }
        return null;
    }

    @Override
    protected void onEnable() {
        try {
            boolean receive = this.clientInput.getValue();
            int port = Integer.parseInt(this.port.getValue());
            this.currentMode = this.mode.getValue();
            switch (this.currentMode) {
                case Host: {
                    this.connectionManager = new SimpleConnectionManager(this.cPackets, this.max.getValue());
                    this.host = Host.createAndStart(EXECUTOR, this.connectionManager, this, port, receive);
                    ModuleUtil.sendMessage(this, "\u00a7aServer is listening on port: \u00a7f" + this.host.getPort() + "\u00a7a" + ".");
                    break;
                }
                case Client: {
                    this.client = new Client(this.sPackets, this.serverList, this.ip.getValue(), port);
                    Managers.THREAD.submit((SafeRunnable)((Object)this.client));
                    String s = this.name.getValue();
                    this.client.setName(s);
                    ProtocolUtil.sendMessage(this.client, 0, s);
                    break;
                }
            }
        }
        catch (NumberFormatException e) {
            ModuleUtil.disableRed(this, "Couldn't parse port: " + this.port.getValue() + ".");
        }
        catch (Throwable t) {
            ModuleUtil.disableRed(this, t.getMessage());
        }
    }

    @Override
    protected void onDisable() {
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
        if (this.host != null) {
            this.host.close();
            this.host = null;
        }
        this.connectionManager = null;
        this.serverList.set(new IConnectionEntry[0]);
    }

    @Override
    public void disable(String message) {
        mc.addScheduledTask(() -> ModuleUtil.disableRed(this, message));
    }

    public IClient getClient() {
        return this.client;
    }

    public IHost getHost() {
        return this.host;
    }

    private void setupConnectionManagers() {
        ChatLogger logger = new ChatLogger();
        this.sPackets.add(3, new PacketHandler(logger));
        this.sPackets.add(9, new PositionHandler(logger));
        this.sPackets.add(10, new VelocityHandler(this));
        this.sPackets.add(11, new EatingHandler());
        this.sPackets.add(8, new ServerListHandler(this.serverList));
    }

    @Override
    public void onVelocity(double x, double y, double z) {
        EntityPlayer player = RotationUtil.getRotationPlayer();
        if (player == null) {
            return;
        }
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        mc.addScheduledTask(() -> player.setVelocity(x, y, z));
    }

    @Override
    public double getLastX() {
        return this.lastX;
    }

    @Override
    public double getLastY() {
        return this.lastY;
    }

    @Override
    public double getLastZ() {
        return this.lastZ;
    }
}

