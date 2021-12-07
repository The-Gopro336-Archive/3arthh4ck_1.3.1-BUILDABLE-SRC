package me.earth.earthhack.impl.modules.render.logoutspots.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class LogoutSpot
implements Globals {
    private final String name;
    private final AxisAlignedBB boundingBox;
    private final double x;
    private final double y;
    private final double z;

    public LogoutSpot(EntityPlayer player) {
        this.name = player.getName();
        this.boundingBox = player.getEntityBoundingBox();
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
    }

    public String getName() {
        return this.name;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getDistance() {
        return LogoutSpot.mc.player.getDistance(this.x, this.y, this.z);
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public Vec3d rounded() {
        return new Vec3d(MathUtil.round(this.x, 1), MathUtil.round(this.y, 1), MathUtil.round(this.z, 1));
    }
}
