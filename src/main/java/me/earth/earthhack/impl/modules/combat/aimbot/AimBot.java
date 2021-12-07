package me.earth.earthhack.impl.modules.combat.aimbot;

import java.util.LinkedList;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.aimbot.ListenerGameLoop;
import me.earth.earthhack.impl.modules.combat.aimbot.ListenerMotion;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class AimBot
extends EntityTypeModule {
    protected final Setting<Boolean> silent = this.register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> fov = this.register(new BooleanSetting("Fov", false));
    protected final Setting<Integer> extrapolate = this.register(new NumberSetting<Integer>("Extrapolate", 0, 0, 10));
    protected final Setting<Double> maxRange = this.register(new NumberSetting<Double>("MaxRange", 100.0, 0.0, 500.0));
    protected Entity target;
    protected float yaw;
    protected float pitch;

    public AimBot() {
        super("AimBot", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.unregister(this.vehicles);
        this.unregister(this.misc);
    }

    @Override
    public String getDisplayInfo() {
        return this.target != null ? this.target.getName() : null;
    }

    public Entity getTarget() {
        LinkedList entites = new LinkedList();
        Entity closest = null;
        double closestAngle = 360.0;
        double x = RotationUtil.getRotationPlayer().posX;
        double y = RotationUtil.getRotationPlayer().posY;
        double z = RotationUtil.getRotationPlayer().posZ;
        float h = AimBot.mc.player.getEyeHeight();
        for (Entity entity : AimBot.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase) || entity.equals(AimBot.mc.player) || entity.equals(RotationUtil.getRotationPlayer()) || !EntityUtil.isValid(entity, this.maxRange.getValue()) || !this.isValid(entity) || !RayTraceUtil.canBeSeen(new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), x, y, z, h) && !RayTraceUtil.canBeSeen(new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight() / 2.0, entity.posZ), x, y, z, h) && !RayTraceUtil.canBeSeen(new Vec3d(entity.posX, entity.posY, entity.posZ), x, y, z, h)) continue;
            double angle = RotationUtil.getAngle(entity, 1.4);
            if (this.fov.getValue().booleanValue() && angle > (double)(AimBot.mc.gameSettings.fovSetting / 2.0f) || !(angle < closestAngle) || this.fov.getValue().booleanValue() && !(angle < (double)(AimBot.mc.gameSettings.fovSetting / 2.0f))) continue;
            closest = entity;
            closestAngle = angle;
        }
        return closest;
    }
}
