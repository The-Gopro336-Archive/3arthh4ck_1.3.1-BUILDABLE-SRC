/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.MovementInput
 */
package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;

public class MovementInputFromRemotePlayer
extends MovementInput {
    private final EntityPlayer player;

    public MovementInputFromRemotePlayer(EntityPlayer player) {
        this.player = player;
    }

    public void updatePlayerMoveState() {
        MovementInput input = MovementUtil.inverse((Entity)this.player, 0.2783);
        this.moveForward = input.moveForward;
        this.moveStrafe = input.moveStrafe;
        if (this.moveForward == 0.0f && this.moveStrafe == 0.0f) {
            this.forwardKeyDown = false;
            this.backKeyDown = false;
            this.leftKeyDown = false;
            this.rightKeyDown = false;
        }
        if (this.moveForward < 0.0f) {
            this.backKeyDown = true;
        } else if (this.moveForward > 0.0f) {
            this.forwardKeyDown = true;
        }
        if (this.moveStrafe < 0.0f) {
            this.rightKeyDown = true;
        } else if (this.moveStrafe > 0.0f) {
            this.leftKeyDown = true;
        }
        this.jump = false;
        this.sneak = input.sneak;
        if (this.sneak) {
            this.moveForward = (float)((double)this.moveForward * 0.3);
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
        }
    }
}

