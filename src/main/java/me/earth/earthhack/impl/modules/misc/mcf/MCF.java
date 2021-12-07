package me.earth.earthhack.impl.modules.misc.mcf;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.mcf.MCFData;
import me.earth.earthhack.impl.modules.misc.mcf.MiddleClickListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MCF
extends Module {
    public MCF() {
        super("MCF", Category.Misc);
        this.listeners.add(new MiddleClickListener(this));
        this.setData(new MCFData(this));
    }

    protected void onMiddleClick() {
        Entity entity;
        if (this.isEnabled() && MCF.mc.objectMouseOver != null && MCF.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && (entity = MCF.mc.objectMouseOver.entityHit) instanceof EntityPlayer) {
            if (Managers.FRIENDS.contains((EntityPlayer)entity)) {
                Managers.FRIENDS.remove(entity);
                Managers.CHAT.sendDeleteMessage("\u00a7c" + entity.getName() + " unfriended.", entity.getName(), 4000);
            } else {
                GameProfile profile = ((EntityPlayer)entity).getGameProfile();
                Managers.FRIENDS.add(profile.getName(), profile.getId());
                Managers.CHAT.sendDeleteMessage("\u00a7b" + entity.getName() + " friended.", entity.getName(), 4000);
            }
        }
    }
}
