package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.killaura.KillAuraData;
import me.earth.earthhack.impl.modules.combat.killaura.ListenerEntityEquipment;
import me.earth.earthhack.impl.modules.combat.killaura.ListenerGameLoop;
import me.earth.earthhack.impl.modules.combat.killaura.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.killaura.ListenerRiding;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraSwitch;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTarget;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTeleport;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;

public class KillAura
extends EntityTypeModule {
    protected final Setting<Boolean> passengers = this.register(new BooleanSetting("Passengers", false));
    protected final Setting<AuraTarget> targetMode = this.register(new EnumSetting<AuraTarget>("Target", AuraTarget.Closest));
    protected final Setting<Boolean> prioEnemies = this.register(new BooleanSetting("Enemies", true));
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("Range", 6.0, 0.0, 6.0));
    protected final Setting<Double> wallRange = this.register(new NumberSetting<Double>("WallRange", 3.0, 0.0, 6.0));
    protected final Setting<Boolean> swordOnly = this.register(new BooleanSetting("Sword/Axe", true));
    protected final Setting<Boolean> delay = this.register(new BooleanSetting("Delay", true));
    protected final Setting<Float> cps = this.register(new NumberSetting<Float>("CPS", Float.valueOf(20.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> stopSneak = this.register(new BooleanSetting("Release-Sneak", true));
    protected final Setting<Boolean> stopSprint = this.register(new BooleanSetting("Release-Sprint", true));
    protected final Setting<Boolean> stopShield = this.register(new BooleanSetting("AutoBlock", true));
    protected final Setting<Boolean> whileEating = this.register(new BooleanSetting("While-Eating", true));
    protected final Setting<Boolean> stay = this.register(new BooleanSetting("Stay", false));
    protected final Setting<Float> soft = this.register(new NumberSetting<Float>("Soft", Float.valueOf(180.0f), Float.valueOf(0.1f), Float.valueOf(180.0f)));
    protected final Setting<Integer> rotationTicks = this.register(new NumberSetting<Integer>("Rotation-Ticks", 0, 0, 10));
    protected final Setting<AuraTeleport> auraTeleport = this.register(new EnumSetting<AuraTeleport>("Teleport", AuraTeleport.None));
    protected final Setting<Double> teleportRange = this.register(new NumberSetting<Double>("TP-Range", 0.0, 0.0, 100.0));
    protected final Setting<Boolean> yTeleport = this.register(new BooleanSetting("Y-Teleport", false));
    protected final Setting<Boolean> movingTeleport = this.register(new BooleanSetting("Move-Teleport", false));
    protected final Setting<Swing> swing = this.register(new EnumSetting<Swing>("Swing", Swing.Full));
    protected final Setting<Boolean> tps = this.register(new BooleanSetting("TPS-Sync", true));
    protected final Setting<Boolean> t2k = this.register(new BooleanSetting("Fast-32ks", true));
    protected final Setting<Float> health = this.register(new NumberSetting<Float>("Health", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(15.0f)));
    protected final Setting<Integer> armor = this.register(new NumberSetting<Integer>("Armor", 0, 0, 100));
    protected final Setting<Float> targetRange = this.register(new NumberSetting<Float>("Target-Range", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Boolean> multi32k = this.register(new BooleanSetting("Multi-32k", false));
    protected final Setting<Integer> packets = this.register(new NumberSetting<Integer>("Packets", 1, 0, 20));
    protected final Setting<Double> height = this.register(new NumberSetting<Double>("Height", 1.0, 0.0, 1.0));
    protected final Setting<Boolean> ridingTeleports = this.register(new BooleanSetting("Riding-Teleports", false));
    protected final Setting<Boolean> efficient = this.register(new BooleanSetting("Efficient", false));
    protected final Setting<Boolean> cancelEntityEquip = this.register(new BooleanSetting("NoEntityEquipment", false));
    protected final Setting<Boolean> tpInfo = this.register(new BooleanSetting("TP-Info", false));
    protected final Setting<Integer> coolDown = this.register(new NumberSetting<Integer>("Cooldown", 0, 0, 500));
    protected final Setting<Boolean> m1Attack = this.register(new BooleanSetting("Hold-Mouse", false));
    protected final Setting<AuraSwitch> autoSwitch = this.register(new EnumSetting<AuraSwitch>("AutoSwitch", AuraSwitch.None));
    protected final RotationSmoother rotationSmoother = new RotationSmoother(Managers.ROTATION);
    protected final DiscreteTimer timer = new GuardTimer();
    protected boolean isTeleporting;
    protected boolean isAttacking;
    protected boolean ourCrit;
    protected Entity target;
    protected Vec3d eff;
    protected Vec3d pos;
    protected int slot;

    public KillAura() {
        super("KillAura", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRiding(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerEntityEquipment(this));
        this.setData(new KillAuraData(this));
    }

    @Override
    public String getDisplayInfo() {
        if (this.target == null || EntityUtil.isDead(this.target)) {
            return null;
        }
        double distance = KillAura.mc.player.getDistanceSq(this.target);
        if (distance > (double)MathUtil.square(this.targetRange.getValue().floatValue()) || !this.shouldAttack() && (!this.tpInfo.getValue().booleanValue() || this.teleportRange.getValue() == 0.0 || this.auraTeleport.getValue() != AuraTeleport.Smart)) {
            return null;
        }
        StringBuilder name = new StringBuilder(EntityNames.getName(this.target)).append("\u00a77").append(", ");
        if (distance >= 36.0) {
            name.append("\u00a7c");
        } else if (!RotationUtil.getRotationPlayer().canEntityBeSeen(this.target) && distance >= 9.0) {
            if (this.target instanceof EntityPlayer && ((EntityPlayer)this.target).canEntityBeSeen(RotationUtil.getRotationPlayer())) {
                name.append("\u00a7f");
            } else {
                name.append("\u00a76");
            }
        } else {
            name.append("\u00a7a");
        }
        return name.append(MathUtil.round(Math.sqrt(distance), 2)).toString();
    }

    @Override
    public boolean isValid(Entity entity) {
        if (entity == null || KillAura.mc.player.getDistanceSq(entity) > (double)MathUtil.square(this.targetRange.getValue().floatValue()) || EntityUtil.isDead(entity) || entity.equals(KillAura.mc.player) || entity.equals(KillAura.mc.player.getRidingEntity()) || entity instanceof EntityPlayer && Managers.FRIENDS.contains((EntityPlayer)entity) || this.passengers.getValue() == false && KillAura.mc.player.getPassengers().contains(entity) || entity instanceof EntityExpBottle || entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityEnderCrystal) {
            return false;
        }
        return super.isValid(entity);
    }

    public Entity getTarget() {
        return this.target;
    }

    protected Entity findTarget() {
        Entity closest = null;
        Entity bestEnemy = null;
        double bestAngle = 360.0;
        float lowest = Float.MAX_VALUE;
        double distance = Double.MAX_VALUE;
        double closestEnemy = Double.MAX_VALUE;
        for (Entity entity : KillAura.mc.world.loadedEntityList) {
            if (!this.isValid(entity)) continue;
            double dist = KillAura.mc.player.getDistanceSq(entity);
            if (this.targetMode.getValue() == AuraTarget.Angle) {
                double angle = RotationUtil.getAngle(entity, 1.75);
                if (!(angle < bestAngle) || !(Math.sqrt(dist) - this.teleportRange.getValue() < 6.0)) continue;
                closest = entity;
                bestAngle = angle;
                continue;
            }
            if (this.prioEnemies.getValue().booleanValue() && entity instanceof EntityPlayer && Managers.ENEMIES.contains((EntityPlayer)entity) && dist < closestEnemy) {
                bestEnemy = entity;
                closestEnemy = dist;
            }
            if (this.isInRange(RotationUtil.getRotationPlayer(), entity)) {
                float h;
                if (this.health.getValue().floatValue() != 0.0f && entity instanceof EntityLivingBase && (h = EntityUtil.getHealth((EntityLivingBase)entity)) < this.health.getValue().floatValue() && h < lowest) {
                    closest = entity;
                    distance = dist;
                    lowest = h;
                }
                if (this.armor.getValue() != 0) {
                    for (ItemStack stack : entity.getArmorInventoryList()) {
                        if (stack.getItem() instanceof ItemElytra || !(DamageUtil.getPercent(stack) < (float)this.armor.getValue().intValue())) continue;
                        closest = entity;
                        distance = dist;
                        break;
                    }
                }
            }
            if (closest == null) {
                closest = entity;
                distance = dist;
                continue;
            }
            if (!(dist < distance)) continue;
            closest = entity;
            distance = dist;
        }
        return bestEnemy != null ? bestEnemy : closest;
    }

    public boolean isInRange(Entity from, Entity target) {
        return this.isInRange(from.getPositionVector(), target);
    }

    public boolean isInRange(Vec3d from, Entity target) {
        double distance = from.squareDistanceTo(target.getPositionVector());
        if (distance >= MathUtil.square(this.range.getValue())) {
            return false;
        }
        if (distance < MathUtil.square(this.wallRange.getValue())) {
            return true;
        }
        return KillAura.mc.world.rayTraceBlocks(new Vec3d(from.x, from.y + (double)KillAura.mc.player.getEyeHeight(), from.z), new Vec3d(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ), false, true, false) == null;
    }

    protected boolean shouldAttack() {
        if (this.m1Attack.getValue().booleanValue() && !Mouse.isButtonDown(0)) {
            return false;
        }
        return this.swordOnly.getValue() == false || KillAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || KillAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe;
    }

    protected void releaseShield() {
        if (KillAura.mc.player.getHeldItemOffhand().getItem() instanceof ItemShield) {
            KillAura.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(KillAura.mc.player), EnumFacing.getFacingFromVector((float)((float)Managers.POSITION.getX()), (float)((float)Managers.POSITION.getY()), (float)((float)Managers.POSITION.getZ()))));
        }
    }

    protected void useShield() {
        if ((KillAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || KillAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe) && KillAura.mc.player.getHeldItemOffhand().getItem() instanceof ItemShield) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> KillAura.mc.playerController.processRightClick(KillAura.mc.player, KillAura.mc.world, EnumHand.OFF_HAND));
        }
    }

    public Vec3d criticalCallback(Vec3d vec3d) {
        if (this.isEnabled() && this.ourCrit) {
            if (this.eff != null) {
                return this.eff;
            }
            switch (this.auraTeleport.getValue()) {
                case Smart: {
                    if (!this.isTeleporting || this.pos == null) break;
                    return this.pos;
                }
                case Full: {
                    return Managers.POSITION.getVec();
                }
            }
        }
        return vec3d;
    }
}
