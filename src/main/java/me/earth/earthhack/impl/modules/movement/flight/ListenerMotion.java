package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.init.MobEffects;

final class ListenerMotion
extends ModuleListener<Flight, MotionUpdateEvent> {
    private static final ModuleCache<NoSlowDown> NO_SLOW_DOWN = Caches.getModule(NoSlowDown.class);
    private static final SettingCache<Boolean, BooleanSetting, NoSlowDown> GUI = Caches.getSetting(NoSlowDown.class, BooleanSetting.class, "GuiMove", true);

    public ListenerMotion(Flight module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        switch (((Flight)this.module).mode.getValue()) {
            case ConstantiamNew: {
                if (event.getStage() == Stage.PRE) {
                    if (((Flight)this.module).constNewStage > 2) {
                        ListenerMotion.mc.player.motionY = 0.0;
                        ListenerMotion.mc.player.setPosition(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY - 0.032, ListenerMotion.mc.player.posZ);
                        ++((Flight)this.module).constNewTicks;
                        switch (((Flight)this.module).constNewTicks) {
                            case 1: {
                                ((Flight)this.module).constY *= (double)-0.95f;
                                break;
                            }
                            case 2: 
                            case 3: 
                            case 4: {
                                ((Flight)this.module).constY += 3.25E-4;
                                break;
                            }
                            case 5: {
                                ((Flight)this.module).constY += 5.0E-4;
                                ((Flight)this.module).constNewTicks = 0;
                            }
                        }
                        event.setY(ListenerMotion.mc.player.posY + ((Flight)this.module).constY);
                    }
                } else if (((Flight)this.module).constNewStage > 2) {
                    ListenerMotion.mc.player.setPosition(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY + 0.032, ListenerMotion.mc.player.posZ);
                }
                if (ListenerMotion.mc.player.onGround || ListenerMotion.mc.player.collidedVertically || ListenerMotion.mc.player.ticksExisted % 30 != 0) break;
                event.setY(event.getY() - 0.032);
                break;
            }
            case ConstoHare: 
            case ConstoHareFast: {
                if (event.getStage() == Stage.PRE) {
                    ++((Flight)this.module).oHareCounter;
                    if (ListenerMotion.mc.player.moveForward == 0.0f && ListenerMotion.mc.player.moveStrafing == 0.0f) {
                        ListenerMotion.mc.player.setPosition(ListenerMotion.mc.player.posX + 1.0, ListenerMotion.mc.player.posY + 1.0, ListenerMotion.mc.player.posZ + 1.0);
                        ListenerMotion.mc.player.setPosition(ListenerMotion.mc.player.prevPosX, ListenerMotion.mc.player.prevPosY, ListenerMotion.mc.player.prevPosZ);
                        ListenerMotion.mc.player.motionX = 0.0;
                        ListenerMotion.mc.player.motionZ = 0.0;
                    }
                    ListenerMotion.mc.player.motionY = 0.0;
                    if (ListenerMotion.mc.gameSettings.keyBindJump.isKeyDown()) {
                        ListenerMotion.mc.player.motionY += 0.5;
                    }
                    if (ListenerMotion.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        ListenerMotion.mc.player.motionY -= 0.5;
                    }
                    if (((Flight)this.module).oHareCounter != 2) break;
                    ListenerMotion.mc.player.setPosition(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY + 1.0E-10, ListenerMotion.mc.player.posZ);
                    ((Flight)this.module).oHareCounter = 0;
                    break;
                }
                double xDist = ListenerMotion.mc.player.posX - ListenerMotion.mc.player.prevPosX;
                double zDist = ListenerMotion.mc.player.posZ - ListenerMotion.mc.player.prevPosZ;
                ((Flight)this.module).oHareLastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                break;
            }
            case Constantiam: 
            case Normal: {
                ListenerMotion.mc.player.motionX = 0.0;
                ListenerMotion.mc.player.motionY = 0.0;
                ListenerMotion.mc.player.motionZ = 0.0;
                if (((Flight)this.module).glide.getValue().booleanValue()) {
                    ListenerMotion.mc.player.motionY -= ((Flight)this.module).glideSpeed.getValue().doubleValue();
                }
                if (!ListenerMotion.mc.inGameHasFocus && (!NO_SLOW_DOWN.isEnabled() || !GUI.getValue().booleanValue())) break;
                if (ListenerMotion.mc.player.movementInput.jump) {
                    ListenerMotion.mc.player.motionY += (double)0.4f;
                }
                if (ListenerMotion.mc.player.movementInput.sneak) {
                    ListenerMotion.mc.player.motionY -= (double)0.4f;
                }
                if (((Flight)this.module).mode.getValue() != FlightMode.Constantiam || ListenerMotion.mc.player.onGround || ListenerMotion.mc.player.collidedVertically || ListenerMotion.mc.player.ticksExisted % 20 != 0 || !((Flight)this.module).antiKick.getValue().booleanValue()) break;
                ListenerMotion.mc.player.setPosition(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY - 0.032, ListenerMotion.mc.player.posZ);
                break;
            }
            case Jump: {
                if (event.getStage() != Stage.PRE || ListenerMotion.mc.player.onGround) break;
                if (!ListenerMotion.mc.player.movementInput.jump) {
                    if (MovementUtil.noMovementKeys() || ListenerMotion.mc.player.movementInput.sneak) break;
                    ++((Flight)this.module).counter;
                    if (((Flight)this.module).counter < 11) break;
                    ListenerMotion.mc.player.jumpMovementFactor = 0.7f;
                    ListenerMotion.mc.player.jump();
                    ((Flight)this.module).counter = 0;
                    break;
                }
                if (ListenerMotion.mc.player.movementInput.sneak) break;
                ++((Flight)this.module).counter;
                if (((Flight)this.module).counter < 4) break;
                ListenerMotion.mc.player.jumpMovementFactor = 0.01f;
                ListenerMotion.mc.player.jump();
                ((Flight)this.module).counter = 0;
            }
        }
        if (event.getStage() == Stage.PRE) {
            ((Flight)this.module).constNewOffset = ListenerMotion.mc.player.posX - ListenerMotion.mc.player.prevPosX;
            double zDif = ListenerMotion.mc.player.posZ - ListenerMotion.mc.player.prevPosZ;
            ((Flight)this.module).lastDist = Math.sqrt(((Flight)this.module).constNewOffset * ((Flight)this.module).constNewOffset + zDif * zDif);
        }
        if (((Flight)this.module).antiKick.getValue().booleanValue() && ((Flight)this.module).mode.getValue() != FlightMode.Constantiam) {
            ++((Flight)this.module).antiCounter;
            if (((Flight)this.module).antiCounter >= 12 && !ListenerMotion.mc.player.isPotionActive(MobEffects.LEVITATION) && !ListenerMotion.mc.player.isElytraFlying() && ListenerMotion.mc.world.getCollisionBoxes(ListenerMotion.mc.player, ListenerMotion.mc.player.getEntityBoundingBox().grow(0.0625).expand(0.0, -0.55, 0.0)).isEmpty()) {
                event.setY(event.getY() - 0.03126);
                if (((Flight)this.module).antiCounter >= 22) {
                    ((Flight)this.module).antiCounter = 0;
                }
            }
        }
    }
}
