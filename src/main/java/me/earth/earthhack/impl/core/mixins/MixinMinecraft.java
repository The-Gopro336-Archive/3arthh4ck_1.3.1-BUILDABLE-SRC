/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiIngame
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.resources.data.MetadataSerializer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.Timer
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldSettings
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.impl.core.mixins;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.events.keyboard.ClickLeftEvent;
import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.events.keyboard.ClickRightEvent;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.misc.AbortEatingEvent;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.player.multitask.MultiTask;
import me.earth.earthhack.impl.modules.player.sorter.Sorter;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public abstract class MixinMinecraft
implements IMinecraft {
    private static final ModuleCache<Sorter> SORTER = Caches.getModule(Sorter.class);
    private static final ModuleCache<MultiTask> MULTI_TASK = Caches.getModule(MultiTask.class);
    private static final ModuleCache<Spectate> SPECTATE = Caches.getModule(Spectate.class);
    private static final ModuleCache<AutoConfig> CONFIG = Caches.getModule(AutoConfig.class);
    private static boolean isEarthhackRunning = true;
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private int leftClickCounter;
    @Shadow
    private int rightClickDelayTimer;
    @Shadow
    @Final
    private Queue<FutureTask<?>> scheduledTasks;
    @Shadow
    public WorldClient world;
    @Shadow
    public EntityPlayerSP player;
    private int gameLoop = 0;
    @Shadow
    public GuiIngame ingameGUI;
    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    protected abstract void rightClickMouse();

    @Shadow
    protected abstract void clickMouse();

    @Shadow
    protected abstract void middleClickMouse();

    @Override
    @Accessor(value="rightClickDelayTimer")
    public abstract int getRightClickDelay();

    @Override
    @Accessor(value="rightClickDelayTimer")
    public abstract void setRightClickDelay(int var1);

    @Override
    @Accessor(value="metadataSerializer")
    public abstract MetadataSerializer getMetadataSerializer();

    @Override
    @Accessor(value="timer")
    public abstract Timer getTimer();

    @Override
    public void click(IMinecraft.Click type) {
        switch (type) {
            case RIGHT: {
                this.rightClickMouse();
                break;
            }
            case LEFT: {
                this.clickMouse();
                break;
            }
            case MIDDLE: {
                this.middleClickMouse();
                break;
            }
        }
    }

    @Override
    public int getGameLoop() {
        return this.gameLoop;
    }

    @Override
    public boolean isEarthhackRunning() {
        return isEarthhackRunning;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void runScheduledTasks() {
        Queue<FutureTask<?>> queue = this.scheduledTasks;
        synchronized (queue) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.runTask(this.scheduledTasks.poll(), (Logger)LOGGER);
            }
        }
    }

    @Inject(method={"init"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/resources/IReloadableResourceManager;registerReloadListener(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V", ordinal=0, shift=At.Shift.AFTER)})
    private void preInitHook(CallbackInfo ci) {
        Earthhack.preInit();
    }

    @Inject(method={"init"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal=2, shift=At.Shift.BEFORE)})
    private void initHook2(CallbackInfo ci) {
        Earthhack.init();
        Earthhack.postInit();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="HEAD")})
    private void runGameLoopHead(CallbackInfo callbackInfo) {
        ++this.gameLoop;
    }

    @Inject(method={"runGameLoop"}, at={@At(value="INVOKE", target="Lnet/minecraft/profiler/Profiler;endSection()V", ordinal=0, shift=At.Shift.AFTER)})
    private void post_ScheduledTasks(CallbackInfo callbackInfo) {
        Bus.EVENT_BUS.post(new GameLoopEvent());
    }

    @Inject(method={"runTick"}, at={@At(value="INVOKE", target="Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal=0, shift=At.Shift.BEFORE)})
    public void runTickHook(CallbackInfo info) {
        Bus.EVENT_BUS.post(new TickEvent());
    }

    @Inject(method={"runTick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;tick()V", shift=At.Shift.AFTER)})
    private void postUpdateWorld(CallbackInfo info) {
        Bus.EVENT_BUS.post(new TickEvent.PostWorldTick());
    }

    @Inject(method={"runTick"}, at={@At(value="RETURN")})
    public void runTickReturnHook(CallbackInfo info) {
        Bus.EVENT_BUS.post(new TickEvent.Post());
    }

    @Inject(method={"runTickKeyboard"}, at={@At(value="INVOKE_ASSIGN", target="org/lwjgl/input/Keyboard.getEventKeyState()Z", remap=false)})
    private void runTickKeyboardHook(CallbackInfo callbackInfo) {
        Bus.EVENT_BUS.post(new KeyboardEvent(Keyboard.getEventKeyState(), Keyboard.getEventKey(), Keyboard.getEventCharacter()));
    }

    @Inject(method={"runTick"}, at={@At(value="FIELD", target="Lnet/minecraft/client/Minecraft;world:Lnet/minecraft/client/multiplayer/WorldClient;", ordinal=4, shift=At.Shift.BEFORE)})
    public void post_keyboardTickHook(CallbackInfo info) {
        Bus.EVENT_BUS.post(new KeyboardEvent.Post());
    }

    @Inject(method={"middleClickMouse"}, at={@At(value="HEAD")}, cancellable=true)
    private void middleClickMouseHook(CallbackInfo callbackInfo) {
        ClickMiddleEvent event = new ClickMiddleEvent();
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"rightClickMouse"}, at={@At(value="HEAD")}, cancellable=true)
    private void rightClickMouseHook(CallbackInfo callbackInfo) {
        ClickRightEvent event = new ClickRightEvent(this.rightClickDelayTimer);
        Bus.EVENT_BUS.post(event);
        this.rightClickDelayTimer = event.getDelay();
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Redirect(method={"rightClickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClick(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult processRightClickHook(PlayerControllerMP pCMP, EntityPlayer player, World worldIn, EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            EnumActionResult enumActionResult = pCMP.processRightClick(player, worldIn, hand);
            return enumActionResult;
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Redirect(method={"rightClickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClickBlock(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult processRightClickBlockHook(PlayerControllerMP pCMP, EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            EnumActionResult enumActionResult = pCMP.processRightClickBlock(player, worldIn, pos, direction, vec, hand);
            return enumActionResult;
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Redirect(method={"rightClickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;interactWithEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult interactWithEntityHook(PlayerControllerMP pCMP, EntityPlayer player, Entity target, EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            EnumActionResult enumActionResult = pCMP.interactWithEntity(player, target, hand);
            return enumActionResult;
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Redirect(method={"rightClickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;interactWithEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult interactWithEntity2Hook(PlayerControllerMP pCMP, EntityPlayer player, Entity target, RayTraceResult ray, EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            EnumActionResult enumActionResult = pCMP.interactWithEntity(player, target, ray, hand);
            return enumActionResult;
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }

    @Inject(method={"clickMouse"}, at={@At(value="HEAD")}, cancellable=true)
    private void clickMouseHook(CallbackInfo callbackInfo) {
        ClickLeftEvent event = new ClickLeftEvent(this.leftClickCounter);
        Bus.EVENT_BUS.post(event);
        this.leftClickCounter = event.getLeftClickCounter();
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method={"clickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V"))
    private void attackEntityHook(PlayerControllerMP playerControllerMP, EntityPlayer playerIn, Entity targetEntity) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> playerControllerMP.attackEntity(playerIn, targetEntity));
    }

    @Inject(method={"displayGuiScreen"}, at={@At(value="HEAD")}, cancellable=true)
    private <T extends GuiScreen> void displayGuiScreenHook(T screen, CallbackInfo info) {
        if (this.player == null && screen instanceof GuiChat) {
            info.cancel();
            return;
        }
        GuiScreenEvent<T> event = new GuiScreenEvent<T>(screen);
        Bus.EVENT_BUS.post(event, screen == null ? null : screen.getClass());
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"shutdownMinecraftApplet"}, at={@At(value="HEAD")})
    private void shutdownMinecraftAppletHook(CallbackInfo info) {
        Earthhack.getLogger().info("Shutting down 3arthh4ck.");
        Bus.EVENT_BUS.post(new ShutDownEvent());
        try {
            Managers.CONFIG.saveAll();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Managers.THREAD.shutDown();
        isEarthhackRunning = false;
    }

    @Redirect(method={"sendClickBlockToController"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActiveHook(EntityPlayerSP playerSP) {
        return !MULTI_TASK.isEnabled() && playerSP.isHandActive();
    }

    @Redirect(method={"rightClickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal=0), require=1)
    private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
        return !MULTI_TASK.isEnabled() && playerControllerMP.getIsHittingBlock();
    }

    @Inject(method={"loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at={@At(value="HEAD")})
    private void loadWorldHook(WorldClient worldClient, String loadingMessage, CallbackInfo info) {
        if (this.world != null) {
            Bus.EVENT_BUS.post(new WorldClientEvent.Unload(this.world));
        }
    }

    @Inject(method={"getRenderViewEntity"}, at={@At(value="HEAD")}, cancellable=true)
    private void getRenderViewEntityHook(CallbackInfoReturnable<Entity> cir) {
        if (SPECTATE.isEnabled()) {
            cir.setReturnValue((Entity)((Spectate)SPECTATE.get()).getRender());
        }
    }

    @Inject(method={"launchIntegratedServer"}, at={@At(value="HEAD")})
    private void launchIntegratedServerHook(String folderName, String worldName, WorldSettings worldSettingsIn, CallbackInfo ci) {
        if (CONFIG.isEnabled()) {
            ((AutoConfig)CONFIG.get()).onConnect("singleplayer");
        }
    }

    @Redirect(method={"processKeyBinds"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I"))
    private void processKeyBindsHook(InventoryPlayer inventoryPlayer, int value) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
            inventoryPlayer.currentItem = SORTER.returnIfPresent(s -> s.getHotbarMapping(value), value);
        });
    }

    @Redirect(method={"processKeyBinds"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;onStoppedUsingItem(Lnet/minecraft/entity/player/EntityPlayer;)V"))
    private void onStoppedUsingItemHook(PlayerControllerMP playerControllerMP, EntityPlayer playerIn) {
        Bus.EVENT_BUS.post(new AbortEatingEvent());
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> playerControllerMP.onStoppedUsingItem(playerIn));
    }

    @Redirect(method={"runTickMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V"))
    private void changeCurrentItemHook(InventoryPlayer inventoryPlayer, int direction) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> inventoryPlayer.changeCurrentItem(direction));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Redirect(method={"sendClickBlockToController"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"))
    private boolean onPlayerDamageBlockHook(PlayerControllerMP pCMP, BlockPos posBlock, EnumFacing directionFacing) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            boolean bl = pCMP.onPlayerDamageBlock(posBlock, directionFacing);
            return bl;
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }
}

