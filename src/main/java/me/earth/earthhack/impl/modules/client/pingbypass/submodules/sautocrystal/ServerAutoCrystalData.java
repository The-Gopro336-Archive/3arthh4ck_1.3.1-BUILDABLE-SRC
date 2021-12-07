package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ServerAutoCrystal;

final class ServerAutoCrystalData
extends DefaultData<ServerAutoCrystal> {
    public ServerAutoCrystalData(ServerAutoCrystal module) {
        super(module);
        this.register("Place", "Decides if we place crystals or not.");
        this.register("Target", "Which Players to target. \n-Closest targets the closest player \n-Damage targets the player we can damage most(CPU intensive)");
        this.register("PlaceRange", "Max distance from you to the position to place on.");
        this.register("PlaceTrace", "Max distance through walls, to the position to place on. (Most severs allow the same wall- and normal range for placing crystals)");
        this.register("MinDamage", "Minimum damage a crystal we place has to deal to the enemy.");
        this.register("PlaceDelay", "Delay (in ms) between each time we place a crystal.");
        this.register("MaxSelfPlace", "Maximum damage a crystal we place can deal to us.");
        this.register("FacePlace", "If the targets health is below this value we ignore MinDamage and faceplace him.");
        this.register("MultiPlace", "Maximum amount of crystals dealing damage that can exist at the same time.");
        this.register("CountMin", "If off : only count crystals that deal more than minDamage towards the MultiPlace value. Recommended On if you have higher Ping.");
        this.register("AntiSurround", "Places on positions that already have crystals on them to speed up the AutoCrystal. (Has similar effects to SetDead)");
        this.register("1.13+", "After version 1.13 it's possible to place crystals in 1 block high spaces. Use for ViaVersion servers.");
        this.register("Attack", "When to attack: -Always well, attacks always -BreakSlot Only attack if we are holding a crystal, this is very recommended, since you can leave the AutoCrystal on at all times -Calc Same as breakslot but will show an ESP.");
        this.register("Break", "Decides if AutoCrystal attacks crystals or not.");
        this.register("BreakRange", "Max distance between you and the crystal you want to break.");
        this.register("BreakTrace", "Max distance through walls between you and the crystal you want to break. Most servers are a lot more strict with this than with place wall range. For Orientation: Vanilla servers have a wall range of 3 blocks.");
        this.register("MinBreakDmg", "Minimum damage that a crystal has to deal to the enemy to be attacked by us.");
        this.register("SlowBreak", "Crystals dealing damage that lies between MinBreakDmg and this value will be broken slowly with the given SlowDelay.");
        this.register("SlowDelay", "Delay that crystals that deal less Damage than SlowBreak get blown up with.");
        this.register("BreakDelay", "Delay between each time we attack a crystal.");
        this.register("MaxSelfBreak", "Maximum damage a crystal we break can deal to us.");
        this.register("Instant", "Attacks crystals immediately when they spawn. Can speed up AutoCrystal by up to 100%. (Previously known as Predict)");
        this.register("Rotate", "Some AntiCheats require you to look at the positions you place/break on : -None don't rotate -Break only rotate for breaking crystals -Place only rotate for placing crystals -All rotate for both placing/breaking");
        this.register("Stay", "Keeps the rotations to a position from placement til attacking.");
        this.register("MultiThread", "Especially Target - Damage canbe heavy for the CPU, this will transfer the calculations to another Thread which will make the AutoCrystal eat up less FPS. It's possible to rotate using this, but not recommended.");
        this.register("Suicide", "Only recommended if you run around with 20 Totem kits. Goes all out and will ignore damage dealt to you.");
        this.register("Range", "Distance from crystal to target.");
        this.register("Override", "Ignore MinBreakDmg and MinPlaceDmg if we can deal lethal damage to the target.");
        this.register("MinFace", "MinDamage for Faceplacing.");
        this.register("AntiFriendPop", "Calculates damage dealt to friends.");
        this.register("AutoSwitch", "Automatically switches to crystals: -None never switch -Bind use a bind to toggle switching on and off -Always always switch.");
        this.register("MainHand", "If On, AutoSwitch will switch to the main hand, if Off, to the off hand if the Offhand module is enabled.");
        this.register("SwitchBind", "The bind for AutoSwitch Mode Bind.");
        this.register("SwitchBack", "If the SwitchBind is pressed again, while we are holding crystals in Offhand -> switch to Totems in Offhand.");
        this.register("Swing", "Determines whether and with which Arm to swing with.");
        this.register("CombinedTrace", "Prevents placing Crystals that don't lie within the break wall range.");
        this.register("SetDead", "Removes crystals after we attacked them (Client sided), which can speed up AutoCrystal. Similar effects to AntiSurround, but will send less attack packets.");
        this.register("Cooldown", "Most servers have a cooldown within which you can't attack entities after you switched your mainhand slot. Attacking crystals during this time can cause the AntiCheat to flag you. This setting prevents that by waiting for the given delay (ms).");
        this.register("PartialTicks", "Only touch if you read the code. Required when you want to use Multithreading and Rotate.");
        this.register("FallBack", "Due to the Damage calculation crystals that deal no damage to you and the enemy can block high damaging position. This setting will cause the AutoCrystal to break such Crystals.");
        this.register("FB-Dmg", "Max Damage a FallBack crystal can deal to you.");
        this.register("SoundRemove", "Explosion sounds arrive before the actual Explosion at our client. It can be used to improve AutoCrystal speeds.");
        this.register("HoldFP", "Faceplaces while you hold the left Mouse button.");
        this.register("MultiTask", "Recommended On. If Off: won't place in ticks that we attacked a crystal in.");
        this.register("ThreadMode", "The entry point for starting a Thread when MultiThreading. Recommended ones would be Pre and Delay.");
        this.register("ThreadDelay", "Delay between each Thread when ThreadMode is Delay. Low thread delays can be intensive.");
        this.register("ID-Predict", "Purely a fun setting. Drastically increases your chances of getting kicked, but can reach the theoretical speed limit of up to 20 crystals/second.");
        this.register("NoOffhandParticles", "Blocks the particles that appear when attacking crystals with a weapon in your mainhand.");
    }

    @Override
    public int getColor() {
        return -65536;
    }

    @Override
    public String getDescription() {
        return "An AutoCrystal for the PingBypass.";
    }
}
