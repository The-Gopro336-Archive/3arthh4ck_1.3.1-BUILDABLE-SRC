package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.movement.StepEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerStep
extends ModuleListener<Step, StepEvent> {
    public ListenerStep(Step module) {
        super(module, StepEvent.class);
    }

    @Override
    public void invoke(StepEvent event) {
        if (!Managers.NCP.passed(((Step)this.module).lagTime.getValue())) {
            ((Step)this.module).stepping = false;
            return;
        }
        if (event.getStage() == Stage.PRE) {
            if (ListenerStep.mc.player.getRidingEntity() != null) {
                ListenerStep.mc.player.getRidingEntity().stepHeight = ((Step)this.module).entityStep.getValue() != false ? 256.0f : 1.0f;
            }
            ((Step)this.module).y = event.getBB().minY;
            ((Step)this.module).stepping = ((Step)this.module).canStep();
            if (((Step)this.module).stepping) {
                event.setHeight(((Step)this.module).height.getValue().floatValue());
            }
        } else {
            int slot;
            EntityPlayer closest;
            if (!((Step)this.module).vanilla.getValue().booleanValue()) {
                double height = event.getBB().minY - ((Step)this.module).y;
                if (((Step)this.module).stepping && height > (double)event.getHeight()) {
                    double[] offsets = new double[]{0.42, height < 1.0 && height > 0.8 ? 0.753 : 0.75, 1.0, 1.16, 1.23, 1.2};
                    if (height >= 2.0) {
                        offsets = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
                    }
                    for (int i = 0; i < (height > 1.0 ? offsets.length : 2); ++i) {
                        ListenerStep.mc.player.connection.sendPacket(new CPacketPlayer.Position(ListenerStep.mc.player.posX, ListenerStep.mc.player.posY + offsets[i], ListenerStep.mc.player.posZ, true));
                    }
                    if (((Step)this.module).autoOff.getValue().booleanValue()) {
                        ((Step)this.module).disable();
                    }
                }
            }
            if (((Step)this.module).gapple.getValue().booleanValue() && ((Step)this.module).stepping && !((Step)this.module).breakTimer.passed(60L) && InventoryUtil.isHolding(ItemPickaxe.class) && !InventoryUtil.isHolding(ItemAppleGold.class) && (closest = EntityUtil.getClosestEnemy()) != null && closest.getDistanceSq(ListenerStep.mc.player) < 144.0 && (slot = InventoryUtil.findHotbarItem(Items.GOLDEN_APPLE, new Item[0])) != -1) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> InventoryUtil.switchTo(slot));
            }
        }
    }
}
