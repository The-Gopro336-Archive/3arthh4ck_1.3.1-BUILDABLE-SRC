/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.MovementInputFromRemotePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class MotionTracker
extends EntityPlayer
implements Globals {
    public EntityPlayer tracked;
    public MovementInput movementInput;
    public boolean safe;

    public MotionTracker(World worldIn, EntityPlayer from) {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Motion-Tracker-" + from.getName()));
        this.tracked = from;
        this.setEntityId(from.getEntityId() * -1);
        this.updateFromTrackedEntity();
    }

    public MotionTracker(World worldIn, MotionTracker from) {
        this(worldIn, from.tracked);
        this.movementInput = from.movementInput;
        this.safe = from.safe;
    }

    private MotionTracker(World worldIn) {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Motion-Tracker"));
    }

    public void onUpdate() {
        this.updateFromTrackedEntity();
        super.onUpdate();
    }

    public void updateSilent() {
        super.onUpdate();
    }

    public void onLivingUpdate() {
        this.movementInput.updatePlayerMoveState();
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        axisalignedbb = this.getEntityBoundingBox();
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, axisalignedbb.minY + 0.5, this.posZ + (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, axisalignedbb.minY + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, axisalignedbb.minY + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, axisalignedbb.minY + 0.5, this.posZ + (double)this.width * 0.35);
        super.onLivingUpdate();
    }

    public void updateFromTrackedEntity() {
        this.movementInput = new MovementInputFromRemotePlayer(this.tracked);
        this.movementInput.updatePlayerMoveState();
        this.copyLocationAndAnglesFrom((Entity)this.tracked);
        this.setEntityBoundingBox(this.tracked.getEntityBoundingBox());
        this.motionX = this.tracked.motionX;
        this.motionY = this.tracked.motionY;
        this.motionZ = this.tracked.motionZ;
        this.onGround = this.tracked.onGround;
        this.prevPosX = this.tracked.prevPosX;
        this.prevPosY = this.tracked.prevPosY;
        this.prevPosZ = this.tracked.prevPosZ;
        this.collided = this.tracked.collided;
        this.collidedHorizontally = this.tracked.collidedHorizontally;
        this.collidedVertically = this.tracked.collidedVertically;
        this.moveForward = this.tracked.moveForward;
        this.moveStrafing = this.tracked.moveStrafing;
        this.moveVertical = this.tracked.moveVertical;
    }

    public boolean isSpectator() {
        return false;
    }

    public boolean isCreative() {
        return false;
    }
}

