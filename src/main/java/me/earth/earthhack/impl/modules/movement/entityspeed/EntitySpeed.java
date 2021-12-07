package me.earth.earthhack.impl.modules.movement.entityspeed;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.entityspeed.EntitySpeedData;
import me.earth.earthhack.impl.modules.movement.entityspeed.ListenerTick;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;

public class EntitySpeed
extends Module {
    protected final Setting<Float> speed = this.register(new NumberSetting<Float>("Speed", Float.valueOf(3.8f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    protected final Setting<Boolean> noStuck = this.register(new BooleanSetting("NoStuck", false));
    protected final Setting<Boolean> resetStuck = this.register(new BooleanSetting("Reset-Stuck", false));
    protected final Setting<Integer> stuckTime = this.register(new NumberSetting<Integer>("Stuck-Time", 10000, 0, 10000));
    protected final StopWatch stuckTimer = new StopWatch();
    protected final StopWatch jumpTimer = new StopWatch();
    protected List<BlockPos> positions = new ArrayList<BlockPos>();

    public EntitySpeed() {
        super("EntitySpeed", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        this.setData(new EntitySpeedData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.speed.getValue() + "";
    }

    public static void strafe(double speed) {
        MovementInput input = EntitySpeed.mc.player.movementInput;
        double forward = input.moveForward;
        double strafe = input.moveStrafe;
        float yaw = EntitySpeed.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            EntitySpeed.mc.player.getRidingEntity().motionX = 0.0;
            EntitySpeed.mc.player.getRidingEntity().motionZ = 0.0;
            return;
        }
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += forward > 0.0 ? -45.0f : 45.0f;
            } else if (strafe < 0.0) {
                yaw += forward > 0.0 ? 45.0f : -45.0f;
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            } else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        EntitySpeed.mc.player.getRidingEntity().motionX = forward * speed * cos + strafe * speed * sin;
        EntitySpeed.mc.player.getRidingEntity().motionZ = forward * speed * sin - strafe * speed * cos;
    }
}
