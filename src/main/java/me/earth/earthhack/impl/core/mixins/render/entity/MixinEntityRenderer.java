package me.earth.earthhack.impl.core.mixins.render.entity;

import com.google.common.base.Predicate;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.misc.ReachEvent;
import me.earth.earthhack.impl.event.events.render.AspectRatioEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.modules.player.raytrace.RayTrace;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.viewclip.CameraClip;
import me.earth.earthhack.impl.modules.render.weather.Weather;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.render.GLUProjection;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer
implements IEntityRenderer {
    private static final ModuleCache<NoRender> NO_RENDER = Caches.getModule(NoRender.class);
    private static final ModuleCache<BlockHighlight> BLOCK_HIGHLIGHT = Caches.getModule(BlockHighlight.class);
    private static final ModuleCache<BlockTweaks> BLOCK_TWEAKS = Caches.getModule(BlockTweaks.class);
    private static final ModuleCache<CameraClip> CAMERA_CLIP = Caches.getModule(CameraClip.class);
    private static final ModuleCache<Weather> WEATHER = Caches.getModule(Weather.class);
    private static final ModuleCache<RayTrace> RAYTRACE = Caches.getModule(RayTrace.class);
    private static final ModuleCache<Spectate> SPECTATE = Caches.getModule(Spectate.class);
    private static final SettingCache<Boolean, BooleanSetting, CameraClip> EXTEND = Caches.getSetting(CameraClip.class, BooleanSetting.class, "Extend", false);
    private static final SettingCache<Double, NumberSetting<Double>, CameraClip> DISTANCE = Caches.getSetting(CameraClip.class, Setting.class, "Distance", 10.0);
    @Shadow
    @Final
    private Minecraft field_78531_r;
    @Shadow
    private ItemStack field_190566_ab;
    private float lastReach;

    @Shadow
    protected abstract void func_78467_g(float var1);

    @Shadow
    protected abstract void func_78479_a(float var1, int var2);

    @Override
    public void invokeOrientCamera(float partialTicks) {
        this.func_78467_g(partialTicks);
    }

    @Override
    public void invokeSetupCameraTransform(float partialTicks, int pass) {
        this.func_78479_a(partialTicks, pass);
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap=false))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        AspectRatioEvent event = new AspectRatioEvent((float)this.field_78531_r.displayWidth / (float)this.field_78531_r.displayHeight);
        Bus.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap=false))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        AspectRatioEvent event = new AspectRatioEvent((float)this.field_78531_r.displayWidth / (float)this.field_78531_r.displayHeight);
        Bus.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
        GLUProjection projection = GLUProjection.getInstance();
        IntBuffer viewPort = GLAllocation.createDirectIntBuffer((int)16);
        FloatBuffer modelView = GLAllocation.createDirectFloatBuffer((int)16);
        FloatBuffer projectionPort = GLAllocation.createDirectFloatBuffer((int)16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projectionPort);
        GL11.glGetInteger(2978, viewPort);
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        projection.updateMatrices(viewPort, modelView, projectionPort, (double)scaledResolution.getScaledWidth() / (double)Minecraft.getMinecraft().displayWidth, (double)scaledResolution.getScaledHeight() / (double)Minecraft.getMinecraft().displayHeight);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap=false))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        AspectRatioEvent event = new AspectRatioEvent((float)this.field_78531_r.displayWidth / (float)this.field_78531_r.displayHeight);
        Bus.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }

    @Inject(method={"renderItemActivation"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemActivationHook(CallbackInfo info) {
        if (this.field_190566_ab != null && NO_RENDER.returnIfPresent(NoRender::noTotems, false).booleanValue() && this.field_190566_ab.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }

    @Inject(method={"renderWorldPass"}, at={@At(value="INVOKE", target="net/minecraft/client/renderer/GlStateManager.clear(I)V", ordinal=1, shift=At.Shift.AFTER)})
    private void renderWorldPassHook(int pass, float partialTicks, long finishTimeNano, CallbackInfo info) {
        if (Display.isActive() || Display.isVisible()) {
            Bus.EVENT_BUS.post(new Render3DEvent(partialTicks));
        }
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="FIELD", target="Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
        if (NO_RENDER.returnIfPresent(NoRender::noNausea, false).booleanValue()) {
            return -3.4028235E38f;
        }
        return entityPlayerSP.prevTimeInPortal;
    }

    @Inject(method={"setupFog"}, at={@At(value="RETURN")}, cancellable=true)
    public void setupFogHook(int startCoords, float partialTicks, CallbackInfo info) {
        if (NO_RENDER.returnIfPresent(NoRender::noFog, false).booleanValue()) {
            GlStateManager.setFogDensity((float)0.0f);
        }
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NO_RENDER.returnIfPresent(NoRender::noHurtCam, false).booleanValue() || ESP.isRendering) {
            info.cancel();
        }
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcludingHook(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        if (BLOCK_TWEAKS.isEnabled() && ((BlockTweaks)BLOCK_TWEAKS.get()).noMiningTrace()) {
            return Collections.emptyList();
        }
        try {
            Predicate p = e -> predicate.test((Entity)e) && !e.equals(this.field_78531_r.player);
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, p);
        }
        catch (Exception e2) {
            Earthhack.getLogger().warn("It's that Exception again...");
            e2.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="net/minecraft/client/multiplayer/PlayerControllerMP.getBlockReachDistance()F"))
    private float getBlockReachDistanceHook(PlayerControllerMP controller) {
        ReachEvent event = new ReachEvent(controller.getBlockReachDistance(), 0.0f);
        Bus.EVENT_BUS.post(event);
        this.lastReach = event.isCancelled() ? event.getReach() : 0.0f;
        return this.field_78531_r.playerController.getBlockReachDistance() + this.lastReach;
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="net/minecraft/util/math/Vec3d.distanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
    private double distanceToHook(Vec3d vec3d, Vec3d vec3d1) {
        return vec3d.distanceTo(vec3d1) - (double)this.lastReach;
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        return CAMERA_CLIP.isEnabled() && EXTEND.getValue() != false ? DISTANCE.getValue() : range;
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        return CAMERA_CLIP.isEnabled() ? (EXTEND.getValue().booleanValue() ? DISTANCE.getValue() : 4.0) : range;
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderGlobal;drawSelectionBox(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/RayTraceResult;IF)V"))
    public void drawSelectionBoxHook(RenderGlobal renderGlobal, EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks) {
        if (!BLOCK_HIGHLIGHT.isEnabled()) {
            renderGlobal.drawSelectionBox(player, movingObjectPositionIn, execute, partialTicks);
        }
    }

    @Inject(method={"renderWorldPass"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/EntityRenderer;renderRainSnow(F)V")})
    public void weatherHook(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (WEATHER.isEnabled()) {
            ((Weather)WEATHER.get()).render(partialTicks);
        }
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;rayTrace(DF)Lnet/minecraft/util/math/RayTraceResult;"))
    private RayTraceResult getMouseOverHook(Entity entity, double blockReachDistance, float partialTicks) {
        if (RAYTRACE.returnIfPresent(RayTrace::isActive, false).booleanValue()) {
            Vec3d start = entity.getPositionEyes(partialTicks);
            Vec3d look = entity.getLook(partialTicks);
            Vec3d end = start.addVector(look.x * blockReachDistance, look.y * blockReachDistance, look.z * blockReachDistance);
            if (RAYTRACE.returnIfPresent(RayTrace::liquidCrystalPlace, false).booleanValue() && (this.field_78531_r.player.isInsideOfMaterial(Material.WATER) || this.field_78531_r.player.isInsideOfMaterial(Material.LAVA)) && InventoryUtil.isHolding(Blocks.OBSIDIAN)) {
                MutableWrapper<BlockPos> opposite = new MutableWrapper<BlockPos>();
                RayTraceResult result = this.traceInLiquid(start, end, opposite, true);
                if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (result.getBlockPos().equals(opposite.get())) {
                        result.sideHit = result.sideHit.getOpposite();
                    }
                    return result;
                }
                result = this.traceInLiquid(start, end, opposite, false);
                if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (result.getBlockPos().equals(opposite.get())) {
                        result.sideHit = result.sideHit.getOpposite();
                    }
                    return result;
                }
            }
            if (RAYTRACE.returnIfPresent(RayTrace::phaseCheck, false).booleanValue()) {
                return RayTracer.traceTri(this.field_78531_r.world, this.field_78531_r.world, start, end, false, false, true, (b, p, ef) -> {
                    AxisAlignedBB bb = this.field_78531_r.world.getBlockState((BlockPos)p).func_185900_c(this.field_78531_r.world, (BlockPos)p).offset((BlockPos)p);
                    if (RotationUtil.getRotationPlayer().getEntityBoundingBox().intersects(bb) && (double)p.getY() > this.field_78531_r.player.posY + 0.25) {
                        return false;
                    }
                    if (ef == null) {
                        return true;
                    }
                    for (Entity e : this.field_78531_r.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(p.offset(ef)))) {
                        if (e == null || e.isDead || this.field_78531_r.player.equals(e) || RotationUtil.getRotationPlayer().equals(e)) continue;
                        return false;
                    }
                    return true;
                });
            }
        }
        return entity.rayTrace(blockReachDistance, partialTicks);
    }

    @Redirect(method={"updateCameraAndRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/entity/EntityPlayerSP;turn(FF)V"))
    private void turnHook(EntityPlayerSP entityPlayerSP, float yaw, float pitch) {
        if (SPECTATE.isEnabled()) {
            EntityPlayer spectate;
            if (((Spectate)SPECTATE.get()).shouldTurn() && (spectate = ((Spectate)SPECTATE.get()).getRender()) != null) {
                spectate.turn(yaw, pitch);
                spectate.rotationYawHead = spectate.rotationYaw;
            }
            return;
        }
        entityPlayerSP.turn(yaw, pitch);
    }

    private RayTraceResult traceInLiquid(Vec3d start, Vec3d end, MutableWrapper<BlockPos> opposite, boolean air) {
        return RayTracer.traceTri(this.field_78531_r.world, this.field_78531_r.world, start, end, false, false, true, (b, p, ef) -> {
            if (ef == null || RotationUtil.getRotationPlayer().getEntityBoundingBox().intersects(new AxisAlignedBB((BlockPos)p))) {
                return false;
            }
            BlockPos pos = p.offset(ef);
            BlockPos up = pos.up();
            if ((!air || this.field_78531_r.world.getBlockState(up).getBlock() == Blocks.AIR && this.field_78531_r.world.getBlockState(up.up()).getBlock() == Blocks.AIR) && this.field_78531_r.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(up.getX(), up.getY(), up.getZ(), (double)up.getX() + 1.0, (double)up.getY() + 2.0, (double)up.getZ() + 1.0)).isEmpty()) {
                return true;
            }
            pos = p.offset(ef.getOpposite());
            up = pos.up();
            if ((!air || this.field_78531_r.world.getBlockState(up).getBlock() == Blocks.AIR && this.field_78531_r.world.getBlockState(up.up()).getBlock() == Blocks.AIR) && this.field_78531_r.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(up.getX(), up.getY(), up.getZ(), (double)up.getX() + 1.0, (double)up.getY() + 2.0, (double)up.getZ() + 1.0)).isEmpty()) {
                opposite.set((BlockPos)p);
                return true;
            }
            return !(b instanceof BlockLiquid) && !(b instanceof BlockAir);
        }, (b, p, ef) -> true);
    }
}
