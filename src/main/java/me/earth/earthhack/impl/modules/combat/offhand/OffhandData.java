/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;

final class OffhandData
extends DefaultData<Offhand> {
    public OffhandData(Offhand module) {
        super(module);
        this.register(module.health, "If your health goes below this threshold while you are not \"safe\" (see SafetyManager) a totem will be switched into your offhand.");
        this.register(module.safeH, "Same as Health but applies if you are safe.");
        this.register(module.gappleBind, "Click this Bind to switch Gapples into your Offhand.");
        this.register(module.crystalBind, "Click this Bind to switch Crystals into your Offhand.");
        this.register(module.delay, "Delay between 2 actions that move around items in your inventory. Low delays can cause desync while high delays will sometimes fail you.");
        this.register(module.cToTotem, "Switches to Totems into your Offhand instead of Gapples if the GappleBind is pressed while you are holding crystals in the offhand.");
        this.register(module.swordGap, "When holding rightclick while you are holding a sword/axe in your mainhand and a totem in your offhand Gapples will be switched into your offhand.");
        this.register(module.recover, "Places back the item that was in the offhand before a totem was put there for safety reasons.");
        this.register(module.noOGC, "While mainhanding crystals and offhanding Gapples you might accidentally place crystals while rightclicking to eat gapples. This setting prevents that.");
        this.register(module.hudMode, "Changes the way this module is displayed in the HUD arraylist.");
        this.register(module.timeOut, "Delay for recovery.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "AutoTotem, OffhandCrystal, OffhandGapple, all in one.";
    }
}

