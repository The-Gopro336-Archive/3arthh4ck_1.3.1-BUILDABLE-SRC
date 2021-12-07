/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.MovementInputFromOptions
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.spectate.EntityPlayerNoInterp;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.MathHelper;

final class ListenerUpdate
extends ModuleListener<Spectate, UpdateEvent> {
    public ListenerUpdate(Spectate module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (ListenerUpdate.mc.player.movementInput instanceof MovementInputFromOptions) {
            ListenerUpdate.mc.player.movementInput = new MovementInput();
        }
        EntityPlayerNoInterp render = ((Spectate)this.module).render;
        EntityPlayerNoInterp fake = ((Spectate)this.module).fakePlayer;
        fake.setPositionAndRotationDirect(ListenerUpdate.mc.player.posX, ListenerUpdate.mc.player.posY, ListenerUpdate.mc.player.posZ, ListenerUpdate.mc.player.rotationYaw, ListenerUpdate.mc.player.rotationPitch, 3, false);
        fake.setAbsorptionAmount(ListenerUpdate.mc.player.getAbsorptionAmount());
        fake.setHealth(ListenerUpdate.mc.player.getHealth());
        fake.hurtTime = ListenerUpdate.mc.player.hurtTime;
        fake.maxHurtTime = ListenerUpdate.mc.player.maxHurtTime;
        fake.attackedAtYaw = ListenerUpdate.mc.player.attackedAtYaw;
        render.noClip = true;
        render.setAbsorptionAmount(ListenerUpdate.mc.player.getAbsorptionAmount());
        render.setHealth(ListenerUpdate.mc.player.getHealth());
        render.setAir(ListenerUpdate.mc.player.getAir());
        render.getFoodStats().setFoodLevel(ListenerUpdate.mc.player.getFoodStats().getFoodLevel());
        render.getFoodStats().setFoodSaturationLevel(ListenerUpdate.mc.player.getFoodStats().getSaturationLevel());
        render.setVelocity(0.0, 0.0, 0.0);
        render.setPrimaryHand(ListenerUpdate.mc.player.getPrimaryHand());
        render.hurtTime = ListenerUpdate.mc.player.hurtTime;
        render.maxHurtTime = ListenerUpdate.mc.player.maxHurtTime;
        render.attackedAtYaw = ListenerUpdate.mc.player.attackedAtYaw;
        render.rotationYaw %= 360.0f;
        render.rotationPitch %= 360.0f;
        render.prevRotationYaw = render.rotationYaw;
        render.prevRotationPitch = render.rotationPitch;
        render.prevRotationYawHead = render.rotationYawHead;
        while (render.rotationYaw - render.prevRotationYaw < -180.0f) {
            render.prevRotationYaw -= 360.0f;
        }
        while (render.rotationYaw - render.prevRotationYaw >= 180.0f) {
            render.prevRotationYaw += 360.0f;
        }
        while (render.rotationPitch - render.prevRotationPitch < -180.0f) {
            render.prevRotationPitch -= 360.0f;
        }
        while (render.rotationPitch - render.prevRotationPitch >= 180.0f) {
            render.prevRotationPitch += 360.0f;
        }
        while (render.rotationYawHead - render.prevRotationYawHead < -180.0f) {
            render.prevRotationYawHead -= 360.0f;
        }
        while (render.rotationYawHead - render.prevRotationYawHead >= 180.0f) {
            render.prevRotationYawHead += 360.0f;
        }
        render.lastTickPosX = render.posX;
        render.lastTickPosY = render.posY;
        render.lastTickPosZ = render.posZ;
        render.prevPosX = render.posX;
        render.prevPosY = render.posY;
        render.prevPosZ = render.posZ;
        ((Spectate)this.module).input.updatePlayerMoveState();
        double[] dir = MovementUtil.strafe((Entity)render, ((Spectate)this.module).input, 0.5);
        if (((Spectate)this.module).input.moveStrafe != 0.0f || ((Spectate)this.module).input.moveForward != 0.0f) {
            render.motionX = dir[0];
            render.motionZ = dir[1];
        } else {
            render.motionX = 0.0;
            render.motionZ = 0.0;
        }
        if (((Spectate)this.module).input.jump) {
            render.motionY += 0.5;
        }
        if (((Spectate)this.module).input.sneak) {
            render.motionY -= 0.5;
        }
        render.setEntityBoundingBox(render.getEntityBoundingBox().offset(render.motionX, render.motionY, render.motionZ));
        render.resetPositionToBB();
        render.chunkCoordX = MathHelper.floor((double)(render.posX / 16.0));
        render.chunkCoordZ = MathHelper.floor((double)(render.posZ / 16.0));
    }
}

