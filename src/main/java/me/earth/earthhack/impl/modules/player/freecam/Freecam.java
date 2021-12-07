package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.freecam.ListenerMotion;
import me.earth.earthhack.impl.modules.player.freecam.ListenerMove;
import me.earth.earthhack.impl.modules.player.freecam.ListenerOverlay;
import me.earth.earthhack.impl.modules.player.freecam.ListenerPacket;
import me.earth.earthhack.impl.modules.player.freecam.ListenerPosLook;
import me.earth.earthhack.impl.modules.player.freecam.ListenerPush;
import me.earth.earthhack.impl.modules.player.freecam.ListenerUpdate;
import me.earth.earthhack.impl.modules.player.freecam.mode.CamMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class Freecam
extends DisablingModule {
    protected final Setting<CamMode> mode = this.register(new EnumSetting<CamMode>("Mode", CamMode.Position));
    protected final Setting<Float> speed = this.register(new NumberSetting<Float>("Speed", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private EntityOtherPlayerMP fakePlayer;

    public Freecam() {
        super("Freecam", Category.Player);
        this.listeners.add(new ListenerPacket(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerOverlay(this));
        this.listeners.add(new ListenerPush(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        SimpleData data = new SimpleData(this, "Allows you to look around freely.");
        data.register(this.mode, "-Cancel cancels movement packets.\n-Spanish good for dupes.\n-Position very legit freecam and, unless you toggle freecam while standing in the air, almost undetectable.");
        data.register(this.speed, "The speed you want to move with while in freecam.");
        this.setData(data);
    }

    public CamMode getMode() {
        return this.mode.getValue();
    }

    @Override
    protected void onEnable() {
        if (Freecam.mc.player == null) {
            this.disable();
            return;
        }
        Freecam.mc.player.dismountRidingEntity();
        this.fakePlayer = PlayerUtil.createFakePlayerAndAddToWorld(Freecam.mc.player.getGameProfile());
        this.fakePlayer.onGround = Freecam.mc.player.onGround;
    }

    @Override
    protected void onDisable() {
        if (Freecam.mc.player == null) {
            return;
        }
        Freecam.mc.player.setPosition(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ);
        Freecam.mc.player.noClip = false;
        PlayerUtil.removeFakePlayer(this.fakePlayer);
        this.fakePlayer = null;
    }

    public EntityOtherPlayerMP getPlayer() {
        return this.fakePlayer;
    }

    public void rotate(float yaw, float pitch) {
        if (this.fakePlayer != null) {
            this.fakePlayer.rotationYawHead = yaw;
            this.fakePlayer.setPositionAndRotation(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ, yaw, pitch);
            this.fakePlayer.setPositionAndRotationDirect(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ, yaw, pitch, 3, false);
        }
    }
}
