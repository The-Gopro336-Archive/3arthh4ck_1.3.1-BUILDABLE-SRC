/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.MovementInputFromOptions
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.modules.player.spectate.EntityPlayerNoInterp;
import me.earth.earthhack.impl.modules.player.spectate.ListenerAnimation;
import me.earth.earthhack.impl.modules.player.spectate.ListenerAttack;
import me.earth.earthhack.impl.modules.player.spectate.ListenerMotion;
import me.earth.earthhack.impl.modules.player.spectate.ListenerMove;
import me.earth.earthhack.impl.modules.player.spectate.ListenerRemove;
import me.earth.earthhack.impl.modules.player.spectate.ListenerUpdate;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.World;

public class Spectate
extends DisablingModule
implements CustomCommandModule {
    protected final Setting<Boolean> stopMove = this.register(new BooleanSetting("NoMove", true));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> playerRotations = this.register(new BooleanSetting("Spectator-Rotate", false));
    protected EntityPlayerNoInterp fakePlayer;
    protected EntityPlayerNoInterp render;
    protected MovementInput input;
    protected EntityPlayer player;
    protected boolean spectating;

    public Spectate() {
        super("Spectate", Category.Player);
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerAttack(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerRemove(this));
        this.listeners.add(new ListenerAnimation(this));
        this.setData(new SimpleData(this, "FreeCam but more Vanilla."));
    }

    @Override
    public String getDisplayInfo() {
        if (this.spectating) {
            EntityPlayer thePlayer = this.player;
            return thePlayer != null ? thePlayer.getName() : null;
        }
        return null;
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 2 && Spectate.mc.world != null && Spectate.mc.player != null) {
            EntityPlayer player = null;
            for (EntityPlayer p : Spectate.mc.world.playerEntities) {
                if (p == null || !args[1].equalsIgnoreCase(p.getName())) continue;
                player = p;
                break;
            }
            if (player != null) {
                this.specate(player);
                ModuleUtil.sendMessage(this, "\u00a7aNow spectating: " + player.getName());
                return true;
            }
            Setting<?> setting = this.getSetting(args[1]);
            if (setting == null) {
                ChatUtil.sendMessage("\u00a7cCould not find setting or player \u00a7f" + args[1] + "\u00a7c" + " in the Spectate module.");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getInput(String[] args, PossibleInputs inputs) {
        if (args.length == 1) {
            inputs.setCompletion(TextUtil.substring(this.getName(), args[0].length())).setRest(" <setting/player> <value>");
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        String next = LookUpUtil.findNextPlayerName(args[1]);
        if (next != null) {
            inputs.setCompletion(TextUtil.substring(next, args[1].length()));
            return true;
        }
        return false;
    }

    @Override
    public CustomCompleterResult complete(Completer completer) {
        String player;
        if (!completer.isSame() && completer.getArgs().length == 2 && (player = LookUpUtil.findNextPlayerName(completer.getArgs()[1])) != null) {
            return CustomCompleterResult.SUPER;
        }
        return CustomCompleterResult.PASS;
    }

    @Override
    protected void onEnable() {
        if (Spectate.mc.player == null || Spectate.mc.world == null) {
            this.disable();
            return;
        }
        if (Spectate.mc.player.movementInput instanceof MovementInputFromOptions) {
            Spectate.mc.player.movementInput = new MovementInput();
        }
        this.input = new MovementInputFromOptions(Spectate.mc.gameSettings);
        this.render = new EntityPlayerNoInterp((World)Spectate.mc.world);
        this.render.copyLocationAndAnglesFrom((Entity)Spectate.mc.player);
        this.render.inventory = Spectate.mc.player.inventory;
        this.render.inventoryContainer = Spectate.mc.player.inventoryContainer;
        this.render.inventory.copyInventory(Spectate.mc.player.inventory);
        this.render.setEntityBoundingBox(Spectate.mc.player.getEntityBoundingBox());
        this.render.resetPositionToBB();
        this.fakePlayer = new EntityPlayerNoInterp((World)Spectate.mc.world);
        this.fakePlayer.copyLocationAndAnglesFrom((Entity)Spectate.mc.player);
        this.fakePlayer.inventory.copyInventory(Spectate.mc.player.inventory);
        this.fakePlayer.inventory = Spectate.mc.player.inventory;
        this.fakePlayer.inventoryContainer = Spectate.mc.player.inventoryContainer;
        Spectate.mc.world.addEntityToWorld(-10000, (Entity)this.fakePlayer);
        Spectate.mc.entityRenderer.loadEntityShader(null);
    }

    @Override
    protected void onDisable() {
        MovementInput input;
        EntityPlayerSP playerSP = Spectate.mc.player;
        if (playerSP != null && (input = playerSP.movementInput) != null && input.getClass() == MovementInput.class) {
            mc.addScheduledTask(() -> {
                playerSP.movementInput = new MovementInputFromOptions(Spectate.mc.gameSettings);
            });
        }
        EntityPlayer specPlayer = this.player;
        if (this.spectating) {
            if (specPlayer != null) {
                ((IEntityNoInterp)specPlayer).setNoInterping(true);
            }
            this.spectating = false;
        }
        mc.addScheduledTask(() -> {
            this.player = null;
        });
        if (Spectate.mc.world != null) {
            mc.addScheduledTask(() -> {
                if (Spectate.mc.world != null) {
                    this.player = null;
                    Spectate.mc.world.removeEntity((Entity)this.fakePlayer);
                }
            });
        }
    }

    public boolean shouldTurn() {
        return !this.spectating || this.player == null || this.playerRotations.getValue() != false;
    }

    public void specate(EntityPlayer player) {
        if (this.isEnabled()) {
            this.disable();
        }
        this.spectating = true;
        this.player = player;
        ((IEntityNoInterp)player).setNoInterping(false);
        this.enable();
    }

    public EntityPlayer getRender() {
        return this.spectating ? (this.player == null ? Spectate.mc.player : this.player) : this.render;
    }

    public EntityPlayer getFake() {
        return this.fakePlayer;
    }
}

