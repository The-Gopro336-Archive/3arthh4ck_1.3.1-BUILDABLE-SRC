package me.earth.earthhack.impl.modules.combat.killaura;

import com.google.common.collect.Multimap;
import java.util.Collection;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraSwitch;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion
extends ModuleListener<KillAura, MotionUpdateEvent> {
    public ListenerMotion(KillAura module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            ListenerMotion.pre((KillAura)this.module, event, true);
        } else {
            ListenerMotion.post((KillAura)this.module);
        }
    }

    public static void pre(KillAura module, MotionUpdateEvent event, boolean teleports) {
        module.isAttacking = false;
        if (!module.shouldAttack()) {
            return;
        }
        if (!module.whileEating.getValue().booleanValue() && ListenerMotion.mc.player.getActiveItemStack().getItem() instanceof ItemFood) {
            return;
        }
        module.target = module.findTarget();
        if (module.target == null) {
            return;
        }
        module.slot = -1;
        if (module.autoSwitch.getValue() != AuraSwitch.None) {
            module.slot = ListenerMotion.findSlot();
        }
        boolean passed = ListenerMotion.passedDelay(module, module.slot);
        float[] rotations = null;
        module.isTeleporting = false;
        if (module.rotate.getValue().booleanValue()) {
            rotations = module.rotationSmoother.getRotations(RotationUtil.getRotationPlayer(), module.target, module.height.getValue(), module.soft.getValue().floatValue());
        }
        if (!(!passed || module.isTeleporting || !module.isInRange(Managers.POSITION.getVec(), module.target) || !module.efficient.getValue().booleanValue() && module.isInRange(ListenerMotion.mc.player.getPositionVector(), module.target) || module.rotate.getValue().booleanValue() && (!RotationUtil.isLegit(module.target, new Entity[0]) || module.rotationTicks.getValue() > 1 && module.rotationSmoother.getRotationTicks() < module.rotationTicks.getValue()))) {
            module.eff = Managers.POSITION.getVec();
            ListenerMotion.attack(module, module.target, module.slot);
            module.eff = null;
            module.timer.reset((long)(1000.0 / (double)module.cps.getValue().floatValue()));
            if (rotations != null && module.stay.getValue().booleanValue()) {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
            return;
        }
        if (module.rotate.getValue().booleanValue() && !module.stay.getValue().booleanValue() && !passed) {
            return;
        }
        if (rotations != null) {
            event.setYaw((float)rotations[0]);
            event.setPitch(rotations[1]);
            if (module.rotationSmoother.isRotating()) {
                return;
            }
        }
        module.isAttacking = passed;
        if (module.isAttacking) {
            module.timer.reset((long)(1000.0 / (double)module.cps.getValue().floatValue()));
        }
        if (teleports && module.isAttacking && (module.movingTeleport.getValue().booleanValue() || MovementUtil.noMovementKeys())) {
            boolean canSee = ListenerMotion.mc.player.canEntityBeSeen(module.target);
            double tp = module.teleportRange.getValue();
            double tpSq = MathUtil.square(tp);
            double distSq = ListenerMotion.mc.player.getDistanceSq(module.target);
            double dist = Math.sqrt(distSq);
            switch (module.auraTeleport.getValue()) {
                case Smart: {
                    if (!(!canSee && distSq >= 9.0) && !(distSq >= 36.0) || !(dist - tp < 3.0) && (!canSee || !(dist - tp < 6.0))) break;
                    Vec3d vec = module.target.getPositionVector();
                    Vec3d own = ListenerMotion.mc.player.getPositionVector();
                    if (!module.yTeleport.getValue().booleanValue() && vec.y != own.y) {
                        Vec3d noY = new Vec3d(vec.x, own.y, vec.z);
                        double cSq = (canSee ? 36.0 : 9.0) - 5.0E-4;
                        double aSq = noY.squareDistanceTo(vec);
                        double b = Math.sqrt(cSq - aSq);
                        Vec3d dir = own.subtract(noY).normalize();
                        Vec3d result = noY.add(dir.scale(b));
                        if (result.distanceTo(own) <= tp && module.isInRange(result, module.target)) {
                            ListenerMotion.teleport(module, result, event);
                        }
                        return;
                    }
                    Vec3d dir = own.subtract(vec).normalize();
                    Vec3d result = vec.add(dir.scale((canSee ? 6.0 : 3.0) - 0.005));
                    ListenerMotion.teleport(module, result, event);
                    break;
                }
                case Full: {
                    if (!(distSq <= tpSq)) break;
                    ListenerMotion.teleport(module, module.target.getPositionVector(), event);
                    break;
                }
            }
        }
    }

    public static void post(KillAura module) {
        if (module.target == null || !module.isAttacking) {
            return;
        }
        ListenerMotion.attack(module, module.target, module.slot);
    }

    private static void teleport(KillAura module, Vec3d to, MotionUpdateEvent event) {
        module.isTeleporting = true;
        module.pos = to;
        event.setX(to.x);
        event.setY(to.y);
        event.setZ(to.z);
    }

    private static void attack(KillAura module, Entity entity, int slot) {
        boolean stopShield;
        if (module.rotate.getValue().booleanValue() && !module.rotationSmoother.isRotating() && module.rotationSmoother.getRotationTicks() < module.rotationTicks.getValue()) {
            module.rotationSmoother.incrementRotationTicks();
            return;
        }
        if (!module.isInRange(Managers.POSITION.getVec(), module.target)) {
            return;
        }
        boolean stopSneak = module.stopSneak.getValue() != false && Managers.ACTION.isSneaking();
        boolean stopSprint = module.stopSprint.getValue() != false && ListenerMotion.mc.player.isSprinting();
        boolean bl = stopShield = module.stopShield.getValue() != false && ListenerMotion.mc.player.isActiveItemStackBlocking();
        if (stopSneak) {
            ListenerMotion.mc.player.connection.sendPacket(new CPacketEntityAction(ListenerMotion.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        if (stopSprint) {
            ListenerMotion.mc.player.connection.sendPacket(new CPacketEntityAction(ListenerMotion.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
        if (stopShield) {
            module.releaseShield();
        }
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
            int last = ListenerMotion.mc.player.inventory.currentItem;
            if (slot != -1) {
                InventoryUtil.switchTo(slot);
            }
            module.ourCrit = true;
            ListenerMotion.mc.playerController.attackEntity(ListenerMotion.mc.player, entity);
            module.ourCrit = false;
            module.swing.getValue().swing(EnumHand.MAIN_HAND);
            if (module.autoSwitch.getValue() != AuraSwitch.Keep && slot != -1) {
                InventoryUtil.switchTo(last);
            }
        });
        if (stopSneak) {
            ListenerMotion.mc.player.connection.sendPacket(new CPacketEntityAction(ListenerMotion.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        if (stopSprint) {
            ListenerMotion.mc.player.connection.sendPacket(new CPacketEntityAction(ListenerMotion.mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
        if (stopShield) {
            module.useShield();
        }
    }

    private static boolean passedDelay(KillAura module, int slot) {
        if (!(!module.delay.getValue().booleanValue() || module.t2k.getValue().booleanValue() && DamageUtil.isSharper(ListenerMotion.mc.player.getHeldItemMainhand(), 1000))) {
            float cooldown;
            float swing;
            float tps;
            float f = tps = module.tps.getValue() != false ? 20.0f - Managers.TPS.getTps() : 0.0f;
            if (slot == -1) {
                return ListenerMotion.mc.player.getCooledAttackStrength(0.5f - tps) >= 1.0f;
            }
            ItemStack stack = ListenerMotion.mc.player.inventory.getStackInSlot(slot);
            double value = ListenerMotion.mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
            Multimap map = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
            Collection modifiers = map.get(SharedMonsterAttributes.ATTACK_SPEED.getName());
            if (modifiers != null) {
                for (AttributeModifier modifier : modifiers) {
                    value += modifier.getAmount();
                }
            }
            return MathHelper.clamp((float)((swing = (float)((IEntityLivingBase)((Object)ListenerMotion.mc.player)).getTicksSinceLastSwing() + 0.5f - tps) / (cooldown = (float)(1.0 / value * 20.0))), (float)0.0f, (float)1.0f) >= 1.0f;
        }
        return module.cps.getValue().floatValue() >= 20.0f || module.timer.passed((long)(1000.0 / (double)module.cps.getValue().floatValue()));
    }

    private static int findSlot() {
        int slot = -1;
        int bestSharp = -1;
        for (int i = 8; i > -1; --i) {
            int level;
            ItemStack stack = ListenerMotion.mc.player.inventory.getStackInSlot(i);
            if (!(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemAxe) || (level = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.SHARPNESS, (ItemStack)stack)) <= bestSharp) continue;
            bestSharp = level;
            slot = i;
        }
        return slot;
    }
}
