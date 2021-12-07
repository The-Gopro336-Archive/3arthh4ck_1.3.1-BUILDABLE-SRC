package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

class SetDeadManager$1
extends EventListener<PacketEvent.Receive<SPacketSoundEffect>> {
    SetDeadManager$1(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
        SPacketSoundEffect p = (SPacketSoundEffect)event.getPacket();
        if (p.getCategory() == SoundCategory.BLOCKS && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && SetDeadManager.this.shouldRemove()) {
            Vec3d pos = new Vec3d(p.getX(), p.getY(), p.getZ());
            Globals.mc.addScheduledTask(() -> {
                SetDeadManager.this.removeCrystals(pos, 11.0f, Globals.mc.world.loadedEntityList);
                for (SoundObserver observer : SetDeadManager.this.observers) {
                    if (!observer.shouldBeNotified()) continue;
                    observer.onChange(p);
                }
            });
        }
    }
}
