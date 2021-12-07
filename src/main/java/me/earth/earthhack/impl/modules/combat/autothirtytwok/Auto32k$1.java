package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;
import net.minecraft.util.EnumFacing;

class Auto32k$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step;
    static final int[] $SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType;
    static final int[] $SwitchMap$net$minecraft$util$EnumFacing;

    static {
        $SwitchMap$net$minecraft$util$EnumFacing = new int[EnumFacing.values().length];
        try {
            Auto32k$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.DOWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.UP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.WEST.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType = new int[Auto32k.PlaceType.values().length];
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType[Auto32k.PlaceType.MOUSE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType[Auto32k.PlaceType.CLOSE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType[Auto32k.PlaceType.ENEMY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType[Auto32k.PlaceType.MIDDLE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType[Auto32k.PlaceType.FAR.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$PlaceType[Auto32k.PlaceType.SAFE.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step = new int[Auto32k.Step.values().length];
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.PRE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.HOPPER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.SHULKER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.CLICKHOPPER.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.HOPPERGUI.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.DISPENSER.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.DISPENSER_HELPING.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.CLICK_DISPENSER.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.DISPENSER_GUI.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Auto32k$1.$SwitchMap$me$earth$earthhack$impl$modules$combat$autothirtytwok$Auto32k$Step[Auto32k.Step.REDSTONE.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
