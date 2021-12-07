package me.earth.earthhack.impl.util.minecraft;

import java.util.Objects;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public class ArmUtil
implements Globals {
    public static void swingPacket(EnumHand hand) {
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketAnimation(hand));
    }

    public static void swingArmNoPacket(EnumHand hand) {
        if (!ArmUtil.mc.player.isSwingInProgress || ArmUtil.mc.player.swingProgressInt >= ((IEntityLivingBase)((Object)ArmUtil.mc.player)).armSwingAnimationEnd() / 2 || ArmUtil.mc.player.swingProgressInt < 0) {
            ArmUtil.mc.player.swingProgressInt = -1;
            ArmUtil.mc.player.isSwingInProgress = true;
            ArmUtil.mc.player.swingingHand = hand;
        }
    }

    public static void swingArm(EnumHand hand) {
        ArmUtil.mc.player.swingArm(hand);
    }
}
