package me.earth.earthhack.impl.modules.combat.bowkill;

import java.util.ArrayList;
import java.util.Objects;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.bowkill.ListenerCPacket;
import me.earth.earthhack.impl.modules.combat.bowkill.ListenerEntityChunk;
import me.earth.earthhack.impl.modules.combat.bowkill.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.bowkill.ListenerMove;
import me.earth.earthhack.impl.modules.combat.bowkill.ListenerRightClick;
import me.earth.earthhack.impl.modules.combat.bowkill.ListenerStopUsingItem;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTarget;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class BowKiller
extends EntityTypeModule {
    protected final Setting<AuraTarget> targetMode = this.register(new EnumSetting<AuraTarget>("Target", AuraTarget.Closest));
    protected final Setting<Boolean> cancelRotate = this.register(new BooleanSetting("CancelRotate", false));
    protected final Setting<Boolean> move = this.register(new BooleanSetting("Move", false));
    protected final Setting<Boolean> blink = this.register(new BooleanSetting("Blink", true));
    protected final Setting<Boolean> staticS = this.register(new BooleanSetting("Static", true));
    protected final Setting<Boolean> always = this.register(new BooleanSetting("Always", false));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> prioEnemies = this.register(new BooleanSetting("Prio-Enemies", true));
    protected final Setting<Boolean> silent = this.register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> visCheck = this.register(new BooleanSetting("VisCheck", true));
    protected final Setting<Boolean> oppSpotted = this.register(new BooleanSetting("Opp-Spotted", false));
    protected final Setting<Integer> runs = this.register(new NumberSetting<Integer>("Runs", 8, 1, 200));
    protected final Setting<Integer> buffer = this.register(new NumberSetting<Integer>("Buffer", 10, 0, 200));
    protected final Setting<Integer> teleports = this.register(new NumberSetting<Integer>("Teleports", 0, 0, 100));
    protected final Setting<Integer> interval = this.register(new NumberSetting<Integer>("Interval", 25, 0, 100));
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("Range", 15.0, 0.0, 30.0));
    protected final Setting<Double> wallRange = this.register(new NumberSetting<Double>("WallRange", 10.0, 0.0, 30.0));
    protected final Setting<Float> targetRange = this.register(new NumberSetting<Float>("Target-Range", Float.valueOf(30.0f), Float.valueOf(0.0f), Float.valueOf(50.0f)));
    protected final Setting<Double> height = this.register(new NumberSetting<Double>("Height", 1.0, 0.0, 1.0));
    protected final Setting<Float> soft = this.register(new NumberSetting<Float>("Soft", Float.valueOf(180.0f), Float.valueOf(0.1f), Float.valueOf(180.0f)));
    protected final Setting<Integer> armor = this.register(new NumberSetting<Integer>("Armor", 0, 0, 100));
    protected int packetsSent = 0;
    protected boolean cancelling;
    protected boolean needsMessage;
    protected boolean blockUnder = false;
    protected final RotationSmoother rotationSmoother = new RotationSmoother(Managers.ROTATION);
    protected Entity target;
    protected final ArrayList<EntityData> entityDataArrayList = new ArrayList();

    public BowKiller() {
        super("BowKiller", Category.Combat);
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRightClick(this));
        this.listeners.add(new ListenerStopUsingItem(this));
        this.listeners.add(new ListenerEntityChunk(this));
    }

    @Override
    public String getDisplayInfo() {
        if (this.cancelling) {
            if (this.packetsSent >= this.runs.getValue() * 2 || this.always.getValue().booleanValue()) {
                return "\u00a7a" + this.packetsSent;
            }
            return "\u00a7c" + this.packetsSent;
        }
        return null;
    }

    @Override
    protected void onEnable() {
        this.packetsSent = 0;
        this.cancelling = false;
        this.needsMessage = true;
    }

    protected void onPacket(PacketEvent.Send<? extends CPacketPlayer> event) {
        if (!BowKiller.mc.player.onGround) {
            return;
        }
        if (this.blink.getValue().booleanValue() && this.cancelling) {
            event.setCancelled(true);
        }
    }

    protected Entity findTarget() {
        Entity closest = null;
        Entity bestEnemy = null;
        double bestAngle = 360.0;
        float lowest = Float.MAX_VALUE;
        double distance = Double.MAX_VALUE;
        double closestEnemy = Double.MAX_VALUE;
        for (Entity entity : BowKiller.mc.world.loadedEntityList) {
            if (!this.isValid(entity) || BowKiller.mc.player.getDistanceSq(entity) > (double)MathUtil.square(this.targetRange.getValue().floatValue()) || !BowKiller.mc.player.canEntityBeSeen(entity) && this.visCheck.getValue().booleanValue()) continue;
            double dist = BowKiller.mc.player.getDistanceSq(entity);
            if (this.targetMode.getValue() == AuraTarget.Angle) {
                double angle = RotationUtil.getAngle(entity, 1.75);
                if (!(angle < bestAngle)) continue;
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
                if (entity instanceof EntityLivingBase && (h = EntityUtil.getHealth((EntityLivingBase)entity)) < lowest) {
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
        return BowKiller.mc.world.rayTraceBlocks(new Vec3d(from.x, from.y + (double)BowKiller.mc.player.getEyeHeight(), from.z), new Vec3d(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ), false, true, false) == null;
    }

    @Override
    public boolean isValid(Entity entity) {
        if (entity == null || EntityUtil.isDead(entity) || entity.equals(BowKiller.mc.player) || entity.equals(BowKiller.mc.player.getRidingEntity()) || entity instanceof EntityPlayer && Managers.FRIENDS.contains((EntityPlayer)entity) || entity instanceof EntityExpBottle || entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityEnderCrystal) {
            return false;
        }
        return super.isValid(entity);
    }

    public boolean hasEntity(String id) {
        return this.entityDataArrayList.stream().anyMatch(entityData -> Objects.equals(((EntityData)entityData).id, id));
    }

    public static class EntityData {
        private final String id;
        private final long time;

        public EntityData(String id, long time) {
            this.id = id;
            this.time = time;
        }

        public String getId() {
            return this.id;
        }

        public long getTime() {
            return this.time;
        }
    }
}
