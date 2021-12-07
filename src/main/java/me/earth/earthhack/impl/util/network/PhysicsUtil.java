package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;

public class PhysicsUtil
implements Globals {
    public static void runPhysicsTick() {
        int lastSwing = ((IEntityLivingBase)((Object)PhysicsUtil.mc.player)).getTicksSinceLastSwing();
        int useCount = ((IEntityLivingBase)((Object)PhysicsUtil.mc.player)).getActiveItemStackUseCount();
        int hurtTime = PhysicsUtil.mc.player.hurtTime;
        float prevSwingProgress = PhysicsUtil.mc.player.prevSwingProgress;
        float swingProgress = PhysicsUtil.mc.player.swingProgress;
        int swingProgressInt = PhysicsUtil.mc.player.swingProgressInt;
        boolean isSwingInProgress = PhysicsUtil.mc.player.isSwingInProgress;
        float rotationYaw = PhysicsUtil.mc.player.rotationYaw;
        float prevRotationYaw = PhysicsUtil.mc.player.prevRotationYaw;
        float renderYawOffset = PhysicsUtil.mc.player.renderYawOffset;
        float prevRenderYawOffset = PhysicsUtil.mc.player.prevRenderYawOffset;
        float rotationYawHead = PhysicsUtil.mc.player.rotationYawHead;
        float prevRotationYawHead = PhysicsUtil.mc.player.prevRotationYawHead;
        float cameraYaw = PhysicsUtil.mc.player.cameraYaw;
        float prevCameraYaw = PhysicsUtil.mc.player.prevCameraYaw;
        float renderArmYaw = PhysicsUtil.mc.player.renderArmYaw;
        float prevRenderArmYaw = PhysicsUtil.mc.player.prevRenderArmYaw;
        float renderArmPitch = PhysicsUtil.mc.player.renderArmPitch;
        float prevRenderArmPitch = PhysicsUtil.mc.player.prevRenderArmPitch;
        float walk = PhysicsUtil.mc.player.distanceWalkedModified;
        float prevWalk = PhysicsUtil.mc.player.prevDistanceWalkedModified;
        double chasingPosX = PhysicsUtil.mc.player.chasingPosX;
        double prevChasingPosX = PhysicsUtil.mc.player.prevChasingPosX;
        double chasingPosY = PhysicsUtil.mc.player.chasingPosY;
        double prevChasingPosY = PhysicsUtil.mc.player.prevChasingPosY;
        double chasingPosZ = PhysicsUtil.mc.player.chasingPosZ;
        double prevChasingPosZ = PhysicsUtil.mc.player.prevChasingPosZ;
        float limbSwingAmount = PhysicsUtil.mc.player.limbSwingAmount;
        float prevLimbSwingAmount = PhysicsUtil.mc.player.prevLimbSwingAmount;
        float limbSwing = PhysicsUtil.mc.player.limbSwing;
        ((IEntityPlayerSP)((Object)PhysicsUtil.mc.player)).superUpdate();
        ((IEntityLivingBase)((Object)PhysicsUtil.mc.player)).setTicksSinceLastSwing(lastSwing);
        ((IEntityLivingBase)((Object)PhysicsUtil.mc.player)).setActiveItemStackUseCount(useCount);
        PhysicsUtil.mc.player.hurtTime = hurtTime;
        PhysicsUtil.mc.player.prevSwingProgress = prevSwingProgress;
        PhysicsUtil.mc.player.swingProgress = swingProgress;
        PhysicsUtil.mc.player.swingProgressInt = swingProgressInt;
        PhysicsUtil.mc.player.isSwingInProgress = isSwingInProgress;
        PhysicsUtil.mc.player.rotationYaw = rotationYaw;
        PhysicsUtil.mc.player.prevRotationYaw = prevRotationYaw;
        PhysicsUtil.mc.player.renderYawOffset = renderYawOffset;
        PhysicsUtil.mc.player.prevRenderYawOffset = prevRenderYawOffset;
        PhysicsUtil.mc.player.rotationYawHead = rotationYawHead;
        PhysicsUtil.mc.player.prevRotationYawHead = prevRotationYawHead;
        PhysicsUtil.mc.player.cameraYaw = cameraYaw;
        PhysicsUtil.mc.player.prevCameraYaw = prevCameraYaw;
        PhysicsUtil.mc.player.renderArmYaw = renderArmYaw;
        PhysicsUtil.mc.player.prevRenderArmYaw = prevRenderArmYaw;
        PhysicsUtil.mc.player.renderArmPitch = renderArmPitch;
        PhysicsUtil.mc.player.prevRenderArmPitch = prevRenderArmPitch;
        PhysicsUtil.mc.player.distanceWalkedModified = walk;
        PhysicsUtil.mc.player.prevDistanceWalkedModified = prevWalk;
        PhysicsUtil.mc.player.chasingPosX = chasingPosX;
        PhysicsUtil.mc.player.prevChasingPosX = prevChasingPosX;
        PhysicsUtil.mc.player.chasingPosY = chasingPosY;
        PhysicsUtil.mc.player.prevChasingPosY = prevChasingPosY;
        PhysicsUtil.mc.player.chasingPosZ = chasingPosZ;
        PhysicsUtil.mc.player.prevChasingPosZ = prevChasingPosZ;
        PhysicsUtil.mc.player.limbSwingAmount = limbSwingAmount;
        PhysicsUtil.mc.player.prevLimbSwingAmount = prevLimbSwingAmount;
        PhysicsUtil.mc.player.limbSwing = limbSwing;
        ((IEntityPlayerSP)((Object)PhysicsUtil.mc.player)).invokeUpdateWalkingPlayer();
    }
}
