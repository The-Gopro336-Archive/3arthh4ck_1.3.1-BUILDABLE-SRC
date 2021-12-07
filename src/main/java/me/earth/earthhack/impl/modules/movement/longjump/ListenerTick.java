/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerTick
extends ModuleListener<LongJump, TickEvent> {
    private static final double[] MOVE = new double[]{0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.39706, 0.394525, 0.392, 0.3894, 0.38644, 0.383655, 0.381105, 0.37867, 0.37625, 0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.35945, 0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};

    public ListenerTick(LongJump module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (ListenerTick.mc.player == null || ListenerTick.mc.world == null || ((LongJump)this.module).mode.getValue() != JumpMode.Cowabunga) {
            return;
        }
        if (KeyBoardUtil.isKeyDown(((LongJump)this.module).invalidBind)) {
            ((LongJump)this.module).invalidPacket();
        }
        if (MovementUtil.noMovementKeys()) {
            return;
        }
        float direction = ListenerTick.mc.player.rotationYaw + (float)(ListenerTick.mc.player.moveForward < 0.0f ? 180 : 0) + (ListenerTick.mc.player.moveStrafing > 0.0f ? -90.0f * (ListenerTick.mc.player.moveForward < 0.0f ? -0.5f : (ListenerTick.mc.player.moveForward > 0.0f ? 0.5f : 1.0f)) : 0.0f) - (ListenerTick.mc.player.moveStrafing < 0.0f ? -90.0f * (ListenerTick.mc.player.moveForward < 0.0f ? -0.5f : (ListenerTick.mc.player.moveForward > 0.0f ? 0.5f : 1.0f)) : 0.0f);
        float x = (float)Math.cos((double)(direction + 90.0f) * Math.PI / 180.0);
        float z = (float)Math.sin((double)(direction + 90.0f) * Math.PI / 180.0);
        if (!ListenerTick.mc.player.collidedVertically) {
            ++((LongJump)this.module).airTicks;
            if (ListenerTick.mc.player.movementInput.sneak) {
                ((LongJump)this.module).invalidPacket();
            }
            ((LongJump)this.module).groundTicks = 0;
            if (!ListenerTick.mc.player.collidedVertically) {
                if (ListenerTick.mc.player.motionY == -0.07190068807140403) {
                    ListenerTick.mc.player.motionY *= (double)0.35f;
                }
                if (ListenerTick.mc.player.motionY == -0.10306193759436909) {
                    ListenerTick.mc.player.motionY *= (double)0.55f;
                }
                if (ListenerTick.mc.player.motionY == -0.13395038817442878) {
                    ListenerTick.mc.player.motionY *= (double)0.67f;
                }
                if (ListenerTick.mc.player.motionY == -0.16635183030382) {
                    ListenerTick.mc.player.motionY *= (double)0.69f;
                }
                if (ListenerTick.mc.player.motionY == -0.19088711097794803) {
                    ListenerTick.mc.player.motionY *= (double)0.71f;
                }
                if (ListenerTick.mc.player.motionY == -0.21121925191528862) {
                    ListenerTick.mc.player.motionY *= (double)0.2f;
                }
                if (ListenerTick.mc.player.motionY == -0.11979897632390576) {
                    ListenerTick.mc.player.motionY *= (double)0.93f;
                }
                if (ListenerTick.mc.player.motionY == -0.18758479151225355) {
                    ListenerTick.mc.player.motionY *= (double)0.72f;
                }
                if (ListenerTick.mc.player.motionY == -0.21075983825251726) {
                    ListenerTick.mc.player.motionY *= (double)0.76f;
                }
                if (((LongJump)this.module).getDistance((EntityPlayer)ListenerTick.mc.player, 69.0) < 0.5) {
                    if (ListenerTick.mc.player.motionY == -0.23537393014173347) {
                        ListenerTick.mc.player.motionY *= (double)0.03f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08531999505205401) {
                        ListenerTick.mc.player.motionY *= -0.5;
                    }
                    if (ListenerTick.mc.player.motionY == -0.03659320313669756) {
                        ListenerTick.mc.player.motionY *= (double)-0.1f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.07481386749524899) {
                        ListenerTick.mc.player.motionY *= (double)-0.07f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.0732677700939672) {
                        ListenerTick.mc.player.motionY *= (double)-0.05f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.07480988066790395) {
                        ListenerTick.mc.player.motionY *= (double)-0.04f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.0784000015258789) {
                        ListenerTick.mc.player.motionY *= (double)0.1f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08608320193943977) {
                        ListenerTick.mc.player.motionY *= (double)0.1f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08683615560584318) {
                        ListenerTick.mc.player.motionY *= (double)0.05f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08265497329678266) {
                        ListenerTick.mc.player.motionY *= (double)0.05f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08245009535659828) {
                        ListenerTick.mc.player.motionY *= (double)0.05f;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08244005633718426) {
                        ListenerTick.mc.player.motionY = -0.08243956442521608;
                    }
                    if (ListenerTick.mc.player.motionY == -0.08243956442521608) {
                        ListenerTick.mc.player.motionY = -0.08244005590677261;
                    }
                    if (ListenerTick.mc.player.motionY > -0.1 && ListenerTick.mc.player.motionY < -0.08 && !ListenerTick.mc.player.onGround && ListenerTick.mc.player.movementInput.forwardKeyDown) {
                        ListenerTick.mc.player.motionY = -1.0E-4f;
                    }
                } else {
                    if (ListenerTick.mc.player.motionY < -0.2 && ListenerTick.mc.player.motionY > -0.24) {
                        ListenerTick.mc.player.motionY *= 0.7;
                    }
                    if (ListenerTick.mc.player.motionY < -0.25 && ListenerTick.mc.player.motionY > -0.32) {
                        ListenerTick.mc.player.motionY *= 0.8;
                    }
                    if (ListenerTick.mc.player.motionY < -0.35 && ListenerTick.mc.player.motionY > -0.8) {
                        ListenerTick.mc.player.motionY *= 0.98;
                    }
                    if (ListenerTick.mc.player.motionY < -0.8 && ListenerTick.mc.player.motionY > -1.6) {
                        ListenerTick.mc.player.motionY *= 0.99;
                    }
                }
            }
            Managers.TIMER.setTimer(0.85f);
            if (ListenerTick.mc.player.movementInput.forwardKeyDown) {
                try {
                    ListenerTick.mc.player.motionX = (double)x * MOVE[((LongJump)this.module).airTicks - 1] * 3.0;
                    ListenerTick.mc.player.motionZ = (double)z * MOVE[((LongJump)this.module).airTicks - 1] * 3.0;
                    return;
                }
                catch (Exception ex) {
                    return;
                }
            }
            ListenerTick.mc.player.motionX = 0.0;
            ListenerTick.mc.player.motionZ = 0.0;
            return;
        }
        Managers.TIMER.setTimer(1.0f);
        ((LongJump)this.module).airTicks = 0;
        ++((LongJump)this.module).groundTicks;
        ListenerTick.mc.player.motionX /= 13.0;
        ListenerTick.mc.player.motionZ /= 13.0;
        if (((LongJump)this.module).groundTicks == 1) {
            ((LongJump)this.module).updatePosition(ListenerTick.mc.player.posX, ListenerTick.mc.player.posY, ListenerTick.mc.player.posZ);
            ((LongJump)this.module).updatePosition(ListenerTick.mc.player.posX + 0.0624, ListenerTick.mc.player.posY, ListenerTick.mc.player.posZ);
            ((LongJump)this.module).updatePosition(ListenerTick.mc.player.posX, ListenerTick.mc.player.posY + 0.419, ListenerTick.mc.player.posZ);
            ((LongJump)this.module).updatePosition(ListenerTick.mc.player.posX + 0.0624, ListenerTick.mc.player.posY, ListenerTick.mc.player.posZ);
            ((LongJump)this.module).updatePosition(ListenerTick.mc.player.posX, ListenerTick.mc.player.posY + 0.419, ListenerTick.mc.player.posZ);
        }
        if (((LongJump)this.module).groundTicks > 2) {
            ((LongJump)this.module).groundTicks = 0;
            ListenerTick.mc.player.motionX = (double)x * 0.3;
            ListenerTick.mc.player.motionY = 0.424f;
            ListenerTick.mc.player.motionZ = (double)z * 0.3;
        }
    }
}

