package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.init.Blocks;

final class ListenerCollision
extends ModuleListener<Phase, CollisionEvent> {
    public ListenerCollision(Phase module) {
        super(module, CollisionEvent.class);
    }

    @Override
    public void invoke(CollisionEvent event) {
        if (ListenerCollision.mc.player == null || ListenerCollision.mc.player.movementInput == null || !ListenerCollision.mc.player.equals(event.getEntity())) {
            return;
        }
        switch (((Phase)this.module).mode.getValue()) {
            case Constantiam: {
                if (event.getBB() == null || !(event.getBB().maxY > ListenerCollision.mc.player.getEntityBoundingBox().minY) || ListenerCollision.mc.world.getBlockState(PositionUtil.getPosition().up()).getBlock() == Blocks.AIR) break;
                event.setBB(null);
                break;
            }
            case ConstantiamNew: {
                if (!((Phase)this.module).isPhasing()) break;
                event.setBB(null);
                break;
            }
            case Normal: {
                if (((Phase)this.module).onlyBlock.getValue() != false && !((Phase)this.module).isPhasing() || ((Phase)this.module).autoClick.getValue() != false && ((Phase)this.module).requireClick.getValue() != false && ((Phase)this.module).clickBB.getValue() != false && !((Phase)this.module).clickTimer.passed(((Phase)this.module).clickDelay.getValue().intValue()) || ((Phase)this.module).forwardBB.getValue().booleanValue() && !ListenerCollision.mc.gameSettings.keyBindForward.isKeyDown()) {
                    return;
                }
                if (event.getBB() == null || !(event.getBB().maxY > ListenerCollision.mc.player.getEntityBoundingBox().minY) || !ListenerCollision.mc.player.isSneaking()) break;
                event.setBB(null);
                break;
            }
            case Sand: {
                event.setBB(null);
                ListenerCollision.mc.player.noClip = true;
                break;
            }
            case Climb: {
                if (ListenerCollision.mc.player.collidedHorizontally) {
                    event.setBB(null);
                }
                if (!ListenerCollision.mc.player.movementInput.sneak && (!ListenerCollision.mc.player.movementInput.jump || !((double)event.getPos().getY() > ListenerCollision.mc.player.posY))) break;
                event.setCancelled(true);
                break;
            }
        }
    }
}
