/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.client.pingbypass.ListenerCustomPayload;
import me.earth.earthhack.impl.modules.client.pingbypass.ListenerKeepAlive;
import me.earth.earthhack.impl.modules.client.pingbypass.ListenerLogin;
import me.earth.earthhack.impl.modules.client.pingbypass.ListenerTick;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassData;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadManager;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.FriendSerializer;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting.SettingSerializer;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sSafety.ServerSafety;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ServerAutoCrystal;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sinventory.ServerInventory;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.ServerUtil;

public class PingBypass
extends Module {
    private static final ModuleCache<Media> MEDIA = Caches.getModule(Media.class);
    private final PayloadManager payloadManager = new PayloadManager();
    protected final Setting<String> port = this.register(new StringSetting("Port", "0"));
    protected final Setting<Integer> pings = this.register(new NumberSetting<Integer>("Pings", 5, 1, 30));
    protected SettingSerializer serializer;
    protected FriendSerializer friendSerializer;
    protected StopWatch timer = new StopWatch();
    protected String serverName;
    protected long startTime;
    protected int serverPing;
    protected long ping;
    protected boolean handled;

    public PingBypass() {
        super("PingBypass", Category.Client);
        this.register(new BooleanSetting("NoRender", false));
        this.register(new StringSetting("IP", "Proxy-IP"));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerKeepAlive(this));
        this.listeners.add(new ListenerLogin(this));
        this.listeners.add(new ListenerCustomPayload(this.payloadManager));
        ServerAutoTotem sAutoTotem = new ServerAutoTotem(this);
        ServerAutoCrystal sCrystal = new ServerAutoCrystal(this);
        ServerInventory sInventory = new ServerInventory(this);
        ServerSafety sSafety = new ServerSafety(this);
        try {
            Managers.MODULES.register(sAutoTotem);
            Managers.MODULES.register(sCrystal);
            Managers.MODULES.register(sInventory);
            Managers.MODULES.register(sSafety);
        }
        catch (AlreadyRegisteredException e) {
            throw new IllegalStateException("Couldn't register PingBypass Submodules : " + e.getTrying().getName(), e);
        }
        this.serializer = new SettingSerializer(this, sAutoTotem, sCrystal, Managers.MODULES.getByClass(FakePlayer.class), sSafety, Managers.MODULES.getByClass(PingSpoof.class), sInventory);
        this.listeners.addAll(this.serializer.getListeners());
        this.friendSerializer = new FriendSerializer();
        this.listeners.addAll(this.friendSerializer.getListeners());
        this.registerPayloadReaders();
        this.setData(new PingBypassData(this));
    }

    @Override
    protected void onEnable() {
        Managers.FRIENDS.addObserver(this.friendSerializer.getObserver());
        ServerUtil.disconnectFromMC("PingBypass enabled.");
        this.serializer.clear();
        this.friendSerializer.clear();
    }

    @Override
    protected void onDisable() {
        Managers.FRIENDS.removeObserver(this.friendSerializer.getObserver());
        ServerUtil.disconnectFromMC("PingBypass disabled.");
        this.serializer.clear();
        this.friendSerializer.clear();
    }

    @Override
    public String getDisplayInfo() {
        return this.ping + "ms";
    }

    private void registerPayloadReaders() {
        this.payloadManager.register((short)0, buffer -> {
            String name = buffer.readString(32767);
            mc.addScheduledTask(() -> {
                this.setServerName(name);
                MEDIA.computeIfPresent(media -> media.setPingBypassName(this.getServerName()));
            });
        });
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String name) {
        this.serverName = name;
    }

    public int getServerPing() {
        return this.serverPing;
    }

    public int getPort() {
        try {
            return Integer.parseInt(this.port.getValue());
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public PayloadManager getPayloadManager() {
        return this.payloadManager;
    }
}

